import React from 'react';
import axios from 'axios';
import { useCart } from './CartContext';

export default function Cart() {
  const { items, removeItem, clearCart, getTotalPrice } = useCart();

  const placeOrder = async () => {
    try {
      await axios.post('/api/orders', {
        items: items.map((it) => ({ productId: it.id, quantity: it.quantity })),
      });
      clearCart();
      alert('Order placed!');
    } catch (e) {
      alert('Failed to place order');
    }
  };
  if (items.length === 0) {
    return (
      <div>
        <h2>Your Cart</h2>
        <p>Your cart is empty.</p>
      </div>
    );
  }

  return (
    <div>
      <h2>Your Cart</h2>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Total</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map(item => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>${item.price}</td>
              <td>{item.quantity}</td>
              <td>${(item.price * item.quantity).toFixed(2)}</td>
              <td>
                <button onClick={() => removeItem(item.id)}>Remove</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3>Total: ${getTotalPrice().toFixed(2)}</h3>
      <button onClick={placeOrder}>Place Order</button>
    </div>
  );
}
