import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import Spinner from './Spinner';
import { useAuth } from './AuthContext';
import { useTranslation } from 'react-i18next';

export default function RecommendedProducts() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const { authHeader, token } = useAuth();
  const { t } = useTranslation();

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    axios
      .get('/api/recommendations/user', { headers: authHeader() })
      .then((res) => setProducts(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [token]);

  if (!token || loading || products.length === 0) {
    return loading && token ? <Spinner /> : null;
  }

  return (
    <div className="mt-8">
      <h2 className="text-xl font-semibold mb-4">{t('recommended')}</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {products.map((p) => (
          <div
            key={p.id}
            className="border rounded p-4 hover:shadow-lg transform hover:-translate-y-1 transition"
          >
            <Link to={`/products/${p.id}`}>
              {p.imageUrl && (
                <img src={p.imageUrl} alt={p.name} className="w-full h-40 object-cover mb-2" />
              )}
              <h3 className="font-semibold">{p.name}</h3>
            </Link>
            <p className="text-gray-600">${p.price}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
