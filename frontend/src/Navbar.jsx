import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from './CartContext';
import { useAuth } from './AuthContext';

export default function Navbar() {
  const { getItemCount } = useCart();
  const { token, logout } = useAuth();
  return (
    <nav>
      <Link to="/">Home</Link> |{' '}
      <Link to="/add-product">Add Product</Link> |{' '}
      <Link to="/cart">Cart ({getItemCount()})</Link> |{' '}
      {token ? (
        <>
          <Link to="/orders">Orders</Link> |{' '}
          <button onClick={logout}>Logout</button>
        </>
      ) : (
        <Link to="/login">Login</Link>
      )}
    </nav>
  );
}
