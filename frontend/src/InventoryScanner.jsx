import React, { useState } from 'react';
import { QrReader } from 'react-qr-reader';
import axios from 'axios';
import { useAuth, parseJwt } from './AuthContext';

export default function InventoryScanner() {
  const { token, authHeader } = useAuth();
  const role = token ? parseJwt(token).role : null;
  const [product, setProduct] = useState(null);
  const [error, setError] = useState('');

  const handleResult = (result, error) => {
    if (!!result) {
      const id = result?.text;
      axios
        .get(`/api/products/${id}`, { headers: authHeader() })
        .then((res) => {
          setProduct(res.data);
          setError('');
        })
        .catch(() => {
          setProduct(null);
          setError('Product not found');
        });
    } else if (!!error) {
      setError('Scan error');
    }
  };

  if (role !== 'ADMIN') {
    return <div>Not authorized</div>;
  }

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-2">Inventory Scanner</h2>
      <QrReader onResult={handleResult} constraints={{ facingMode: 'environment' }} style={{ width: '100%' }} />
      {product && (
        <div className="mt-4">
          <h3 className="font-semibold">{product.name}</h3>
          <p>Stock: {product.stockQuantity}</p>
        </div>
      )}
      {error && <p className="text-red-600 mt-2">{error}</p>}
    </div>
  );
}
