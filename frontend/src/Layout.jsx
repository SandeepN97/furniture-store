import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import OrderNotifications from './OrderNotifications';

export default function Layout() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <OrderNotifications />
      <div className="flex-1">
        <Outlet />
      </div>
    </div>
  );
}
