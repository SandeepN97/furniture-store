import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

export default function App() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    axios.get('/api/products')
      .then(res => setProducts(res.data.content || res.data))
      .catch(() => {});
  }, []);

  return (
    <div>
      <h1>Furniture Store</h1>
      <ul>
        {products.map(p => (
          <li key={p.id}>
            <Link to={`/products/${p.id}`}>{p.name}</Link> ({p.category?.name}) - ${p.price}
          </li>
        ))}
      </ul>
    </div>
  );
}
