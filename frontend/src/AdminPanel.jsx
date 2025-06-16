import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';

export default function AdminPanel() {
  const { authHeader, role } = useAuth();
  const [products, setProducts] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ name: '', price: '', description: '', categoryId: '', imageUrl: '', imageFile: null });

  const load = () => {
    axios.get('/api/products')
      .then(res => setProducts(res.data.content || res.data))
      .catch(() => {});
  };

  useEffect(() => {
    if (role === 'ADMIN') {
      load();
    }
  }, [role]);

  const startEdit = (p) => {
    setEditing(p.id);
    setForm({
      name: p.name,
      price: p.price,
      description: p.description,
      categoryId: p.category ? p.category.id : '',
      imageUrl: p.imageUrl || '',
      imageFile: null
    });
  };

  const save = async () => {
    let url = form.imageUrl;
    if (form.imageFile) {
      const fd = new FormData();
      fd.append('file', form.imageFile);
      const res = await axios.post('/api/products/upload-image', fd, { headers: { ...authHeader(), 'Content-Type': 'multipart/form-data' } });
      url = res.data.url;
    }
    await axios.put(`/api/products/${editing}`, { ...form, categoryId: Number(form.categoryId), imageUrl: url }, { headers: authHeader() });
    setEditing(null);
    setForm({ name: '', price: '', description: '', categoryId: '', imageUrl: '', imageFile: null });
    load();
  };

  const remove = async (id) => {
    await axios.delete(`/api/products/${id}`, { headers: authHeader() });
    load();
  };

  if (role !== 'ADMIN') {
    return <div>Not authorized</div>;
  }

  return (
    <div>
      <h2>Admin Panel</h2>
      {editing && (
        <div>
          <h3>Edit Product</h3>
          <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Name" />
          <input value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} placeholder="Price" />
          <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Description" />
          <input value={form.categoryId} onChange={e => setForm({ ...form, categoryId: e.target.value })} placeholder="Category ID" />
          <input type="file" onChange={e => setForm({ ...form, imageFile: e.target.files[0] })} />
          <button onClick={save}>Save</button>
          <button onClick={() => setEditing(null)}>Cancel</button>
        </div>
      )}
      <ul>
        {products.map(p => (
          <li key={p.id}>
            {p.name} - ${p.price}
            <button onClick={() => startEdit(p)}>Edit</button>
            <button onClick={() => remove(p.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
