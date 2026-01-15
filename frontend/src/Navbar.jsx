import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from './CartContext';
import { useAuth, parseJwt } from './AuthContext';
import { useTheme } from './ThemeContext';

export default function Navbar() {
  const { getItemCount } = useCart();
  const { token, logout } = useAuth();
  const { toggle, theme } = useTheme();
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
          <Link to="/admin/orders" className="hover:underline">
            Manage Orders
          </Link>
          <Link to="/admin/analytics" className="hover:underline">
            Analytics
          </Link>
        </>
      )}
      {token && (
        <>
          <Link to="/wishlist" className="hover:underline">
            Wishlist
          </Link>
          <Link to="/orders" className="hover:underline">
            Orders
          </Link>
          <Link to="/profile" className="hover:underline">
            Profile
          </Link>
        </>
      )}
      <Link to="/cart" className="hover:underline ml-auto">
        Cart ({getItemCount()})
      </Link>
      <button onClick={toggle} className="hover:underline">
        {theme === 'dark' ? 'Light' : 'Dark'} Mode
      </button>
      {token ? (
        <button onClick={logout} className="hover:underline">
          Logout
        </button>
      ) : (
        <Link to="/login" className="hover:underline">
          Login
        </Link>
      )}
    </nav>
  );
}
