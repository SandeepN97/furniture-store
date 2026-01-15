import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useSearchParams } from 'react-router-dom';
import { useCart } from './CartContext';
import Spinner from './Spinner';
import SearchBar from './SearchBar';
import { useToast } from './ToastContext';

export default function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [pageInfo, setPageInfo] = useState({ number: 0, totalPages: 1 });
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useSearchParams();
  const { addItem } = useCart();
  const { showToast } = useToast();

  const page = parseInt(searchParams.get('page')) || 0;
  const categoryId = searchParams.get('categoryId') || '';
  const search = searchParams.get('search') || '';
  const minPrice = searchParams.get('minPrice') || '';
  const maxPrice = searchParams.get('maxPrice') || '';
  const inStock = searchParams.get('inStock') === 'true' ? true : null;
  const sortBy = searchParams.get('sortBy') || '';
  const size = 12;

  useEffect(() => {
    axios
      .get('/api/categories')
      .then((res) => setCategories(res.data))
      .catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);

    let sortParams = {};
    if (sortBy === 'price_asc') sortParams = { sort: 'price,asc' };
    else if (sortBy === 'price_desc') sortParams = { sort: 'price,desc' };
    else if (sortBy === 'rating') sortParams = { sort: 'averageRating,desc' };
    else if (sortBy === 'newest') sortParams = { sort: 'id,desc' };

    axios
      .get('/api/products', {
        params: {
          page,
          size,
          categoryId: categoryId || null,
          search: search || null,
          minPrice: minPrice || null,
          maxPrice: maxPrice || null,
          inStock: inStock,
          ...sortParams
        },
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
  }, [page, categoryId, search, minPrice, maxPrice, inStock, sortBy]);

  const handleSearch = (filters) => {
    setSearchParams({
      page: 0,
      categoryId,
      search: filters.search || '',
      minPrice: filters.minPrice || '',
      maxPrice: filters.maxPrice || '',
      inStock: filters.inStock || '',
      sortBy: filters.sortBy || ''
    });
  };

  const next = () => setSearchParams({
    page: page + 1,
    categoryId,
    search,
    minPrice,
    maxPrice,
    inStock: inStock || '',
    sortBy
  });

  const prev = () => setSearchParams({
    page: Math.max(page - 1, 0),
    categoryId,
    search,
    minPrice,
    maxPrice,
    inStock: inStock || '',
    sortBy
  });

  const changeCategory = (e) => setSearchParams({
    page: 0,
    categoryId: e.target.value,
    search,
    minPrice,
    maxPrice,
    inStock: inStock || '',
    sortBy
  });

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 dark:text-white">Furniture Store</h1>

      <SearchBar onSearch={handleSearch} />

      <select
        className="border p-2 mb-4 rounded dark:bg-gray-700 dark:border-gray-600 dark:text-white"
        value={categoryId}
        onChange={changeCategory}
      >
        <option value="">All Categories</option>
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
              <button
                onClick={() => addItem(p)}
                className="mt-2 bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600"
              >
                Add to Cart
              </button>
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
          Prev
        </button>
        <button
          onClick={next}
          disabled={pageInfo.number >= pageInfo.totalPages - 1}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
}
