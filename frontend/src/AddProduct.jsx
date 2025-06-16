import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth, parseJwt } from './AuthContext';
import { useTranslation } from 'react-i18next';

export default function AddProduct() {
  const { token, authHeader } = useAuth();
  const role = token ? parseJwt(token).role : null;
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [stockQuantity, setStockQuantity] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [message, setMessage] = useState('');
  const [errors, setErrors] = useState({});
  const { t } = useTranslation();
  useEffect(() => {
    axios.get('/api/categories').then(res => setCategories(res.data)).catch(() => {});
  }, []);

  const submit = async () => {
    const errs = {};
    if (!name) errs.name = t('nameRequired');
    if (!price) errs.price = t('priceRequired');
    if (!categoryId) errs.categoryId = t('categoryRequired');
    if (Object.keys(errs).length) {
      setErrors(errs);
      return;
    }
    setErrors({});
    try {
      let imageUrl = '';
      if (imageFile) {
        const fd = new FormData();
        fd.append('file', imageFile);
        const res = await axios.post('/api/products/upload-image', fd, {
          headers: { ...authHeader(), 'Content-Type': 'multipart/form-data' },
        });
        imageUrl = res.data.url;
      }
      await axios.post(
        '/api/products',
        { name, price, description, categoryId: Number(categoryId), imageUrl, stockQuantity: Number(stockQuantity) },
        { headers: authHeader() }
      );
      setMessage(t('productCreated'));
      setName('');
      setPrice('');
      setDescription('');
      setCategoryId('');
      setStockQuantity('');
      setImageFile(null);
    } catch (err) {
      setMessage(t('failedToCreateProduct'));
    }
  };

  if (role !== 'ADMIN') {
    return <div>{t('notAuthorized')}</div>;
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">{t('addProduct')}</h2>
      <div className="mb-2">
        <input
          className="border p-2 w-full"
          placeholder={t('namePlaceholder')}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        {errors.name && <p className="text-red-600 text-sm">{errors.name}</p>}
      </div>
      <div className="mb-2">
        <input
          className="border p-2 w-full"
          placeholder={t('pricePlaceholder')}
          value={price}
          onChange={(e) => setPrice(e.target.value)}
        />
        {errors.price && <p className="text-red-600 text-sm">{errors.price}</p>}
      </div>
      <div className="mb-2">
        <input
          className="border p-2 w-full"
          placeholder="Stock Quantity"
          value={stockQuantity}
          onChange={(e) => setStockQuantity(e.target.value)}
        />
      </div>
      <textarea
        className="border p-2 w-full mb-2"
        placeholder={t('descriptionPlaceholder')}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <div className="mb-2">
        <select
          className="border p-2 w-full"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
        >
          <option value="">{t('selectCategory')}</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
        {errors.categoryId && (
          <p className="text-red-600 text-sm">{errors.categoryId}</p>
        )}
      </div>
      <input
        type="file"
        onChange={(e) => setImageFile(e.target.files[0])}
        className="mb-2"
      />
      <button
        onClick={submit}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        {t('save')}
      </button>
      {message && <p className="mt-2 text-green-600">{message}</p>}
    </div>
  );
}
