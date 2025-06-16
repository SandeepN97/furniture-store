import React from 'react';
import ReactDOM from 'react-dom/client';
import './i18n';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import App from './App';
import ProductDetails from './ProductDetails';
import Cart from './Cart';
import Checkout from './Checkout';
import OrderHistory from './OrderHistory';
import Login from './Login';
import AddProduct from './AddProduct';
import AdminPanel from './AdminPanel';
import AdminDashboard from './AdminDashboard';
import InventoryScanner from './InventoryScanner';
import CheckoutSuccess from './CheckoutSuccess';
import Blog from './Blog';
import Layout from './Layout';
import { CartProvider } from './CartContext';
import { AuthProvider } from './AuthContext';
import { ToastProvider } from './ToastContext';
import { DarkModeProvider } from './DarkModeContext';

ReactDOM.createRoot(document.getElementById('root')).render(
  <DarkModeProvider>
    <ToastProvider>
      <AuthProvider>
        <CartProvider>
          <BrowserRouter>
          <Routes>
            <Route path="/" element={<Layout />}>
            <Route index element={<App />} />
            <Route path="blog" element={<Blog />} />
            <Route path="products/:id" element={<ProductDetails />} />
              <Route path="cart" element={<Cart />} />
              <Route path="checkout" element={<Checkout />} />
              <Route path="orders" element={<OrderHistory />} />
              <Route path="login" element={<Login />} />
              <Route path="add-product" element={<AddProduct />} />
              <Route path="admin" element={<AdminPanel />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="scan" element={<InventoryScanner />} />
              <Route path="success" element={<CheckoutSuccess />} />
            </Route>
          </Routes>
        </BrowserRouter>
        </CartProvider>
      </AuthProvider>
    </ToastProvider>
  </DarkModeProvider>
);
