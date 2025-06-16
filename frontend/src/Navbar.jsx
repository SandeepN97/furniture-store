import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from './CartContext';
import { useAuth, parseJwt } from './AuthContext';
import { useTranslation } from 'react-i18next';
import { useDarkMode } from './DarkModeContext';

export default function Navbar() {
  const { getItemCount } = useCart();
  const { token, logout } = useAuth();
  const role = token ? parseJwt(token).role : null;
  const { t, i18n } = useTranslation();
  const { dark, setDark } = useDarkMode();

  const changeLang = (e) => {
    i18n.changeLanguage(e.target.value);
  };

  return (
    <nav className="bg-gray-800 text-white p-4 flex flex-wrap gap-4 items-center">
      <Link to="/" className="hover:underline">
        {t('home')}
      </Link>
      <Link to="/blog" className="hover:underline">
        Blog
      </Link>
      {role === 'ADMIN' && (
        <>
          <Link to="/add-product" className="hover:underline">
            {t('addProduct')}
          </Link>
          <Link to="/admin" className="hover:underline">
            {t('adminPanel')}
          </Link>
          <Link to="/dashboard" className="hover:underline">
            {t('dashboard')}
          </Link>
          <Link to="/scan" className="hover:underline">
            {t('scanInventory')}
          </Link>
        </>
      )}
      <Link to="/cart" className="hover:underline ml-auto">
        {t('cart')} ({getItemCount()})
      </Link>
      {token ? (
        <>
          <Link to="/orders" className="hover:underline">
            {t('orders')}
          </Link>
          <button onClick={logout} className="hover:underline">
            {t('logout')}
          </button>
        </>
      ) : (
        <Link to="/login" className="hover:underline">
          {t('login')}
        </Link>
      )}
      <select
        onChange={changeLang}
        value={i18n.language}
        className="ml-2 text-black p-1 rounded"
      >
        <option value="en">EN</option>
        <option value="es">ES</option>
        <option value="ne">NP</option>
      </select>
      <button onClick={() => setDark(!dark)} className="ml-2">{dark ? '☀️' : '🌙'}</button>
    </nav>
  );
}
