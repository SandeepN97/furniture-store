import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend } from 'chart.js';
import { useAuth } from './AuthContext';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

export default function AdminDashboard() {
  const { authHeader, role } = useAuth();
  const [data, setData] = useState(null);

  useEffect(() => {
    if (role === 'ADMIN') {
      axios
        .get('/api/admin/analytics', { headers: authHeader() })
        .then((res) => setData(res.data))
        .catch(() => {});
    }
  }, [role]);

  if (role !== 'ADMIN') {
    return <div>Not authorized</div>;
  }

  if (!data) {
    return <p className="p-4">Loading...</p>;
  }

  const chartData = {
    labels: data.topProducts.map((p) => p.name),
    datasets: [
      {
        label: 'Quantity Sold',
        data: data.topProducts.map((p) => p.quantity),
        backgroundColor: 'rgba(59, 130, 246, 0.6)',
      },
    ],
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Dashboard</h2>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        <div className="p-4 border rounded">Users: {data.totalUsers}</div>
        <div className="p-4 border rounded">Orders: {data.totalOrders}</div>
        <div className="p-4 border rounded">Revenue: ${data.totalRevenue}</div>
      </div>
      <div className="max-w-xl">
        <Bar data={chartData} />
      </div>
    </div>
  );
}
