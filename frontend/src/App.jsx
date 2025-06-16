import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useSearchParams } from 'react-router-dom';
import { useCart } from './CartContext';
import Spinner from './Spinner';
import { useTranslation } from 'react-i18next';
import RecommendedProducts from './RecommendedProducts';

export default function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [pageInfo, setPageInfo] = useState({ number: 0, totalPages: 1 });
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useSearchParams();
  const { addItem } = useCart();
  const { t } = useTranslation();

  const page = parseInt(searchParams.get('page')) || 0;
  const categoryId = searchParams.get('categoryId') || '';
  const size = 5;

  useEffect(() => {
    axios
      .get('/api/categories')
      .then((res) => setCategories(res.data))
      .catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    axios
      .get('/api/products', {
        params: { page, size, categoryId: categoryId || null },
      })
      .then((res) => {
        if (res.data.content) {
          setProducts(res.data.content);
          setPageInfo({ number: res.data.number, totalPages: res.data.totalPages });
        } else {
          setProducts(res.data);
          setPageInfo({ number: 0, totalPages: 1 });
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [page, categoryId]);

  const next = () => setSearchParams({ page: page + 1, categoryId });
  const prev = () => setSearchParams({ page: Math.max(page - 1, 0), categoryId });
  const changeCategory = (e) => setSearchParams({ page: 0, categoryId: e.target.value });

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">{t('furnitureStore')}</h1>
      <select
        className="border p-2 mb-4"
        value={categoryId}
        onChange={changeCategory}
      >
        <option value="">{t('allCategories')}</option>
        {categories.map((c) => (
          <option key={c.id} value={c.id}>
            {c.name}
          </option>
        ))}
      </select>

      {loading ? (
        <Spinner />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {products.map((p) => (
            <div
              key={p.id}
              className="border rounded p-4 hover:shadow-lg transform hover:-translate-y-1 transition"
            >
              <Link to={`/products/${p.id}`}> 
                {p.imageUrl && (
                  <img
                    src={p.imageUrl}
                    alt={p.name}
                    className="w-full h-40 object-cover mb-2"
                  />
                )}
                <h3 className="font-semibold">{p.name}</h3>
              </Link>
              <p className="text-gray-600">${p.price}</p>
              {p.stockQuantity > 0 ? (
                <button
                  onClick={() => addItem(p)}
                  className="mt-2 bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600"
                >
                  {t('addToCart')}
                </button>
              ) : (
                <p className="text-red-600 mt-2">{t('outOfStock')}</p>
              )}
            </div>
          ))}
        </div>
      )}

      <div className="mt-4 flex justify-between">
        <button
          onClick={prev}
          disabled={page === 0}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          {t('prev')}
        </button>
        <button
          onClick={next}
          disabled={pageInfo.number >= pageInfo.totalPages - 1}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          {t('next')}
        </button>
      </div>
      <RecommendedProducts />
    </div>
  );
}
