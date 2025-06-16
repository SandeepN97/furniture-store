import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from './CartContext';
import { useAuth, parseJwt } from './AuthContext';

export default function Navbar() {
  const { getItemCount } = useCart();
  const { token, logout } = useAuth();
  const role = token ? parseJwt(token).role : null;
  return (
    <nav className="bg-gray-800 text-white p-4 flex flex-wrap gap-4">
      <Link to="/" className="hover:underline">
        Home
      </Link>
      {role === 'ADMIN' && (
        <>
          <Link to="/add-product" className="hover:underline">
            Add Product
          </Link>
          <Link to="/admin" className="hover:underline">
            Admin Panel
          </Link>
        </>
      )}
      <Link to="/cart" className="hover:underline ml-auto">
        Cart ({getItemCount()})
      </Link>
      {token ? (
        <>
          <Link to="/orders" className="hover:underline">
            Orders
          </Link>
          <button onClick={logout} className="hover:underline">
            Logout
          </button>
        </>
      ) : (
        <Link to="/login" className="hover:underline">
          Login
        </Link>
      )}
    </nav>
  );
}
