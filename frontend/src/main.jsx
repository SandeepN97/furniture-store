import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import App from './App';
import ProductDetails from './ProductDetails';
import Cart from './Cart';
import Checkout from './Checkout';
import OrderHistory from './OrderHistory';
import Login from './Login';
import AddProduct from './AddProduct';
import AdminPanel from './AdminPanel';
import CheckoutSuccess from './CheckoutSuccess';
import Layout from './Layout';
import Wishlist from './Wishlist';
import UserProfile from './UserProfile';
import AdminOrders from './AdminOrders';
import AdminAnalytics from './AdminAnalytics';
import { CartProvider } from './CartContext';
import { AuthProvider } from './AuthContext';
import { ToastProvider } from './ToastContext';
import { ThemeProvider } from './ThemeContext';

ReactDOM.createRoot(document.getElementById('root')).render(
  <ThemeProvider>
    <ToastProvider>
      <AuthProvider>
        <CartProvider>
          <BrowserRouter>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<App />} />
              <Route path="products/:id" element={<ProductDetails />} />
              <Route path="cart" element={<Cart />} />
              <Route path="checkout" element={<Checkout />} />
              <Route path="orders" element={<OrderHistory />} />
              <Route path="login" element={<Login />} />
              <Route path="wishlist" element={<Wishlist />} />
              <Route path="profile" element={<UserProfile />} />
              <Route path="add-product" element={<AddProduct />} />
              <Route path="admin" element={<AdminPanel />} />
              <Route path="admin/orders" element={<AdminOrders />} />
              <Route path="admin/analytics" element={<AdminAnalytics />} />
              <Route path="success" element={<CheckoutSuccess />} />
            </Route>
          </Routes>
          </BrowserRouter>
        </CartProvider>
      </AuthProvider>
    </ToastProvider>
  </ThemeProvider>
);
