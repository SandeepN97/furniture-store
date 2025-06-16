import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useCart } from './CartContext';
import Spinner from './Spinner';

export default function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const { addItem } = useCart();
  useEffect(() => {
    axios.get(`/api/products/${id}`)
      .then(res => setProduct(res.data))
      .catch(() => {});
  }, [id]);

  if (!product) {
    return <Spinner />;
  }

  return (
    <div className="p-4 max-w-xl mx-auto">
      <h2 className="text-2xl font-bold mb-2">{product.name}</h2>
      {product.imageUrl && (
        <img
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-64 object-cover mb-2"
        />
      )}
      <p className="text-gray-600">Category: {product.category?.name}</p>
      <p className="text-xl">Price: ${product.price}</p>
      <p className="mb-4">{product.description}</p>
      <button
        onClick={() => addItem(product)}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        Add to Cart
      </button>
    </div>
  );
}
