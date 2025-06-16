import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';

export default function AddProduct() {
  const { authHeader } = useAuth();
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    axios.get('/api/categories').then(res => setCategories(res.data)).catch(() => {});
  }, []);

  const submit = async () => {
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

  return (
    <div>
      <h2>Add Product</h2>
      <input placeholder="Name" value={name} onChange={e => setName(e.target.value)} />
      <input placeholder="Price" value={price} onChange={e => setPrice(e.target.value)} />
      <textarea placeholder="Description" value={description} onChange={e => setDescription(e.target.value)} />
      <select value={categoryId} onChange={e => setCategoryId(e.target.value)}>
        <option value="">Select Category</option>
        {categories.map(c => (
          <option key={c.id} value={c.id}>{c.name}</option>
        ))}
      </select>
      <input type="file" onChange={e => setImageFile(e.target.files[0])} />
      <button onClick={submit}>Save</button>
      {message && <p>{message}</p>}
    </div>
  );
}
