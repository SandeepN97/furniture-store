import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useCart } from './CartContext';
import { useAuth } from './AuthContext';

export default function Checkout() {
  const { items, clearCart, getTotalPrice } = useCart();
  const { authHeader, token } = useAuth();
  const [name, setName] = useState('');
  const [coupon, setCoupon] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const submit = async () => {
    try {
      const endpoint = token ? '/api/orders' : '/api/orders/guest';
      await axios.post(
        endpoint,
        {
          customerName: name,
          items: items.map((it) => ({ productId: it.id, quantity: it.quantity })),
          couponCode: coupon || null,
        },
        { headers: authHeader() }
      );
      clearCart();
      navigate('/success');
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
      <input
        type="text"
        placeholder="Coupon code"
        value={coupon}
        onChange={(e) => setCoupon(e.target.value)}
      />
      <button onClick={submit}>Place Order</button>
      {message && <p>{message}</p>}
      <button
        onClick={async () => {
          const res = await axios.post('/api/cart/save', { items });
          localStorage.setItem('savedCart', res.data.token);
          setMessage('Cart saved!');
        }}
      >
        Save Cart
      </button>
    </div>
  );
}
