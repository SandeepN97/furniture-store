import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useCart } from './CartContext';
import { useAuth } from './AuthContext';

export default function Checkout() {
  const { items, clearCart, getTotalPrice } = useCart();
  const { authHeader } = useAuth();
  const [name, setName] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const submit = async () => {
    try {
      await axios.post('/api/orders', {
        customerName: name,
        items: items.map((it) => ({ productId: it.id, quantity: it.quantity })),
      }, { headers: authHeader() });
      clearCart();
      setMessage('Order submitted successfully!');
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      setMessage('Failed to submit order');
    }
  };

  if (items.length === 0) {
    return <div>Your cart is empty.</div>;
  }

  return (
    <div>
      <h2>Checkout</h2>
      <ul>
        {items.map((it) => (
          <li key={it.id}>
            {it.name} x {it.quantity} - $
            {(it.price * it.quantity).toFixed(2)}
          </li>
        ))}
      </ul>
      <p>Total: ${getTotalPrice().toFixed(2)}</p>
      <input
        type="text"
        placeholder="Your name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <button onClick={submit}>Submit Order</button>
      {message && <p>{message}</p>}
    </div>
  );
}
