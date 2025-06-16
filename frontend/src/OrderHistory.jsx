import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';

export default function OrderHistory() {
  const { authHeader } = useAuth();
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios
      .get('/api/orders/user', { headers: authHeader() })
      .then((res) => setOrders(res.data))
      .catch(() => {});
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  if (orders.length === 0) {
    return <div>No orders found.</div>;
  }

  return (
    <div>
      <h2>Your Orders</h2>
      <ul>
        {orders.map((o) => (
          <li key={o.id}>
            {o.orderDate} - ${o.totalPrice}
          </li>
        ))}
      </ul>
    </div>
  );
}
