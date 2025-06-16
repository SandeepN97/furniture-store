import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth, parseJwt } from './AuthContext';

export default function AddProduct() {
  const { token, authHeader } = useAuth();
  const role = token ? parseJwt(token).role : null;
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [message, setMessage] = useState('');
  const [errors, setErrors] = useState({});
  useEffect(() => {
    axios.get('/api/categories').then(res => setCategories(res.data)).catch(() => {});
  }, []);

  const submit = async () => {
    const errs = {};
    if (!name) errs.name = 'Name is required';
    if (!price) errs.price = 'Price is required';
    if (!categoryId) errs.categoryId = 'Category is required';
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
        { name, price, description, categoryId: Number(categoryId), imageUrl },
        { headers: authHeader() }
      );
      setMessage('Product created');
      setName('');
      setPrice('');
      setDescription('');
      setCategoryId('');
      setImageFile(null);
    } catch (err) {
      setMessage('Failed to create product');
    }
  };

  if (role !== 'ADMIN') {
    return <div>Not authorized</div>;
  }

  return (
    <div className="p-4 max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">Add Product</h2>
      <div className="mb-2">
        <input
          className="border p-2 w-full"
          placeholder="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        {errors.name && <p className="text-red-600 text-sm">{errors.name}</p>}
      </div>
      <div className="mb-2">
        <input
          className="border p-2 w-full"
          placeholder="Price"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
        />
        {errors.price && <p className="text-red-600 text-sm">{errors.price}</p>}
      </div>
      <textarea
        className="border p-2 w-full mb-2"
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <div className="mb-2">
        <select
          className="border p-2 w-full"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
        >
          <option value="">Select Category</option>
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
        Save
      </button>
      {message && <p className="mt-2 text-green-600">{message}</p>}
    </div>
  );
}
