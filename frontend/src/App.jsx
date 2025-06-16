import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useSearchParams } from 'react-router-dom';

export default function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [pageInfo, setPageInfo] = useState({ number: 0, totalPages: 1 });
  const [searchParams, setSearchParams] = useSearchParams();

  const page = parseInt(searchParams.get('page')) || 0;
  const categoryId = searchParams.get('categoryId') || '';
  const size = 5;

  useEffect(() => {
    axios.get('/api/categories')
      .then(res => setCategories(res.data))
      .catch(() => {});
  }, []);

  useEffect(() => {
    axios.get('/api/products', { params: { page, size, categoryId: categoryId || null } })
      .then(res => {
        if (res.data.content) {
          setProducts(res.data.content);
          setPageInfo({ number: res.data.number, totalPages: res.data.totalPages });
        } else {
          setProducts(res.data);
          setPageInfo({ number: 0, totalPages: 1 });
        }
      })
      .catch(() => {});
  }, [page, categoryId]);

  const next = () => setSearchParams({ page: page + 1, categoryId });
  const prev = () => setSearchParams({ page: Math.max(page - 1, 0), categoryId });
  const changeCategory = (e) => setSearchParams({ page: 0, categoryId: e.target.value });

  return (
    <div>
      <h1>Furniture Store</h1>
      <select value={categoryId} onChange={changeCategory}>
        <option value="">All Categories</option>
        {categories.map(c => (
          <option key={c.id} value={c.id}>{c.name}</option>
        ))}
      </select>
      <ul>
        {products.map(p => (
          <li key={p.id}>
            <Link to={`/products/${p.id}`}>{p.name}</Link> ({p.category?.name}) - ${p.price}
          </li>
        ))}
      </ul>
      <button onClick={prev} disabled={page === 0}>Prev</button>
      <button onClick={next} disabled={pageInfo.number >= pageInfo.totalPages - 1}>Next</button>
    </div>
  );
}
