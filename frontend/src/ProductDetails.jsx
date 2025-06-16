import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useCart } from './CartContext';
import Spinner from './Spinner';
import { useTranslation } from 'react-i18next';

export default function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const { addItem } = useCart();
  const { t } = useTranslation();

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
      {product.stockQuantity > 0 ? (
        <button
          onClick={() => addItem(product)}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          {t('addToCart')}
        </button>
      ) : (
        <p className="text-red-600">{t('outOfStock')}</p>
      )}
    </div>
  );
}
