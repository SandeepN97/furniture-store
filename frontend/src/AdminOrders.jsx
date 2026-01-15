import { useState, useEffect } from 'react';
import axios from 'axios';
import { useToast } from './ToastContext';

export default function AdminOrders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterStatus, setFilterStatus] = useState('');
    const { showToast } = useToast();

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const response = await axios.get('/api/admin/orders');
            setOrders(response.data.content || response.data);
        } catch (error) {
            showToast('Failed to load orders', 'error');
        } finally {
            setLoading(false);
        }
    };

    const updateOrderStatus = async (orderId, newStatus, trackingNumber = '') => {
        try {
            await axios.put(`/api/admin/orders/${orderId}/status`, {
                status: newStatus,
                trackingNumber: trackingNumber || undefined
            });
            showToast('Order status updated', 'success');
            fetchOrders();
        } catch (error) {
            showToast('Failed to update order', 'error');
        }
    };

    const getStatusColor = (status) => {
        const colors = {
            PENDING: 'bg-yellow-100 text-yellow-800',
            CONFIRMED: 'bg-blue-100 text-blue-800',
            PROCESSING: 'bg-purple-100 text-purple-800',
            SHIPPED: 'bg-green-100 text-green-800',
            DELIVERED: 'bg-gray-100 text-gray-800',
            CANCELLED: 'bg-red-100 text-red-800'
        };
        return colors[status] || 'bg-gray-100 text-gray-800';
    };

    const filteredOrders = filterStatus
        ? orders.filter(order => order.status === filterStatus)
        : orders;

    if (loading) {
        return <div className="container mx-auto px-4 py-8 text-center dark:text-white">Loading...</div>;
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-6 dark:text-white">Order Management</h1>

            <div className="mb-4">
                <select
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                    className="border rounded px-4 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                    <option value="">All Orders</option>
                    <option value="PENDING">Pending</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="PROCESSING">Processing</option>
                    <option value="SHIPPED">Shipped</option>
                    <option value="DELIVERED">Delivered</option>
                    <option value="CANCELLED">Cancelled</option>
                </select>
            </div>

            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-x-auto">
                <table className="w-full">
                    <thead className="bg-gray-50 dark:bg-gray-700">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Order ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Customer</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Date</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Total</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Status</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                        {filteredOrders.map((order) => (
                            <tr key={order.id}>
                                <td className="px-6 py-4 whitespace-nowrap dark:text-white">#{order.id}</td>
                                <td className="px-6 py-4 whitespace-nowrap dark:text-white">{order.customerName}</td>
                                <td className="px-6 py-4 whitespace-nowrap dark:text-white">
                                    {new Date(order.orderDate).toLocaleDateString()}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap dark:text-white">${order.totalPrice}</td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className={`px-2 py-1 rounded text-xs ${getStatusColor(order.status)}`}>
                                        {order.status}
                                    </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <select
                                        value={order.status}
                                        onChange={(e) => {
                                            const newStatus = e.target.value;
                                            if (newStatus === 'SHIPPED') {
                                                const tracking = prompt('Enter tracking number:');
                                                if (tracking) {
                                                    updateOrderStatus(order.id, newStatus, tracking);
                                                }
                                            } else {
                                                updateOrderStatus(order.id, newStatus);
                                            }
                                        }}
                                        className="border rounded px-2 py-1 text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                                    >
                                        <option value="PENDING">Pending</option>
                                        <option value="CONFIRMED">Confirmed</option>
                                        <option value="PROCESSING">Processing</option>
                                        <option value="SHIPPED">Shipped</option>
                                        <option value="DELIVERED">Delivered</option>
                                        <option value="CANCELLED">Cancelled</option>
                                    </select>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
