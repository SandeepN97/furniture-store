import { useState, useEffect } from 'react';
import axios from 'axios';

export default function AdminAnalytics() {
    const [analytics, setAnalytics] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchAnalytics();
    }, []);

    const fetchAnalytics = async () => {
        try {
            const response = await axios.get('/api/admin/analytics');
            setAnalytics(response.data);
        } catch (error) {
            console.error('Failed to load analytics:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="container mx-auto px-4 py-8 text-center dark:text-white">Loading analytics...</div>;
    }

    if (!analytics) {
        return <div className="container mx-auto px-4 py-8 text-center dark:text-white">No data available</div>;
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-6 dark:text-white">Analytics Dashboard</h1>

            {/* Key Metrics */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                    <h3 className="text-gray-500 dark:text-gray-400 text-sm font-semibold mb-2">Total Revenue</h3>
                    <p className="text-3xl font-bold text-green-600 dark:text-green-400">
                        ${analytics.totalRevenue?.toFixed(2) || '0.00'}
                    </p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                    <h3 className="text-gray-500 dark:text-gray-400 text-sm font-semibold mb-2">Total Orders</h3>
                    <p className="text-3xl font-bold text-blue-600 dark:text-blue-400">
                        {analytics.totalOrders || 0}
                    </p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                    <h3 className="text-gray-500 dark:text-gray-400 text-sm font-semibold mb-2">Total Products</h3>
                    <p className="text-3xl font-bold text-purple-600 dark:text-purple-400">
                        {analytics.totalProducts || 0}
                    </p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                    <h3 className="text-gray-500 dark:text-gray-400 text-sm font-semibold mb-2">Total Users</h3>
                    <p className="text-3xl font-bold text-yellow-600 dark:text-yellow-400">
                        {analytics.totalUsers || 0}
                    </p>
                </div>
            </div>

            {/* Low Stock Alert */}
            {analytics.lowStockCount > 0 && (
                <div className="bg-red-100 dark:bg-red-900 border-l-4 border-red-500 p-4 mb-8">
                    <p className="text-red-700 dark:text-red-200">
                        ⚠️ Warning: {analytics.lowStockCount} product(s) are running low on stock
                    </p>
                </div>
            )}

            {/* Orders by Status */}
            <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md mb-8">
                <h2 className="text-xl font-bold mb-4 dark:text-white">Orders by Status</h2>
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
                    {Object.entries(analytics.ordersByStatus || {}).map(([status, count]) => (
                        <div key={status} className="text-center">
                            <p className="text-2xl font-bold dark:text-white">{count}</p>
                            <p className="text-sm text-gray-500 dark:text-gray-400">{status}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* Recent Orders */}
            <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-bold mb-4 dark:text-white">Recent Orders</h2>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead>
                            <tr className="border-b dark:border-gray-700">
                                <th className="text-left py-2 dark:text-gray-300">Order ID</th>
                                <th className="text-left py-2 dark:text-gray-300">Customer</th>
                                <th className="text-left py-2 dark:text-gray-300">Date</th>
                                <th className="text-left py-2 dark:text-gray-300">Total</th>
                                <th className="text-left py-2 dark:text-gray-300">Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {(analytics.recentOrders || []).slice(0, 10).map((order) => (
                                <tr key={order.id} className="border-b dark:border-gray-700">
                                    <td className="py-2 dark:text-white">#{order.id}</td>
                                    <td className="py-2 dark:text-white">{order.customerName}</td>
                                    <td className="py-2 dark:text-white">
                                        {new Date(order.orderDate).toLocaleDateString()}
                                    </td>
                                    <td className="py-2 dark:text-white">${order.totalPrice}</td>
                                    <td className="py-2">
                                        <span className="text-sm px-2 py-1 rounded bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200">
                                            {order.status}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
