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
      const res = await axios.post(
        '/api/payments/create-checkout-session',
        {
          items: items.map((it) => ({ productId: it.id, quantity: it.quantity })),
        },
        { headers: authHeader() }
      );
      window.location.href = res.data.url;
    } catch (err) {
      setMessage('Failed to start checkout');
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
      <button onClick={submit}>Pay with Stripe</button>
      {message && <p>{message}</p>}
    </div>
  );
}
