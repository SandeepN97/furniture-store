import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

export default function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    axios.get(`/api/products/${id}`)
      .then(res => setProduct(res.data))
      .catch(() => {});
  }, [id]);

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h2>{product.name}</h2>
      {product.imageUrl && <img src={product.imageUrl} alt={product.name} />}
      <p>Category: {product.category?.name}</p>
      <p>Price: ${product.price}</p>
      <p>{product.description}</p>
    </div>
  );
}
