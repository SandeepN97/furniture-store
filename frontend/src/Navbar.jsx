import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from './CartContext';

export default function Navbar() {
  const { getItemCount } = useCart();

  return (
    <nav>
      <Link to="/">Home</Link> |{' '}
      <Link to="/cart">Cart ({getItemCount()})</Link>
    </nav>
  );
}
