import React, { useEffect, useState } from 'react';

export default function App() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch('/api/products')
      .then(res => res.json())
      .then(setProducts)
      .catch(() => {});
  }, []);

  return (
    <div>
      <h1>Furniture Store</h1>
      <ul>
        {products.map(p => (
          <li key={p.id}>{p.name} - ${'{'}p.price{'}'}</li>
        ))}
      </ul>
    </div>
  );
}
