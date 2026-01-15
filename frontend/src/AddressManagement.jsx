import { useState, useEffect } from 'react';
import axios from 'axios';
import { useToast } from './ToastContext';

export default function AddressManagement() {
    const [addresses, setAddresses] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [formData, setFormData] = useState({
        fullName: '',
        addressLine1: '',
        addressLine2: '',
        city: '',
        state: '',
        zipCode: '',
        country: 'USA',
        phoneNumber: '',
        isDefault: false
    });
    const { showToast } = useToast();

    useEffect(() => {
        fetchAddresses();
    }, []);

    const fetchAddresses = async () => {
        try {
            const response = await axios.get('/api/addresses');
            setAddresses(response.data);
        } catch (error) {
            showToast('Failed to load addresses', 'error');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingId) {
                await axios.put(`/api/addresses/${editingId}`, formData);
                showToast('Address updated successfully', 'success');
            } else {
                await axios.post('/api/addresses', formData);
                showToast('Address added successfully', 'success');
            }
            setShowForm(false);
            setEditingId(null);
            resetForm();
            fetchAddresses();
        } catch (error) {
            showToast('Failed to save address', 'error');
        }
    };

    const handleEdit = (address) => {
        setFormData(address);
        setEditingId(address.id);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this address?')) return;

        try {
            await axios.delete(`/api/addresses/${id}`);
            showToast('Address deleted successfully', 'success');
            fetchAddresses();
        } catch (error) {
            showToast('Failed to delete address', 'error');
        }
    };

    const resetForm = () => {
        setFormData({
            fullName: '',
            addressLine1: '',
            addressLine2: '',
            city: '',
            state: '',
            zipCode: '',
            country: 'USA',
            phoneNumber: '',
            isDefault: false
        });
    };

    return (
        <div className="max-w-4xl mx-auto">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold dark:text-white">Shipping Addresses</h2>
                {!showForm && (
                    <button
                        onClick={() => setShowForm(true)}
                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                    >
                        Add New Address
                    </button>
                )}
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md mb-6">
                    <h3 className="text-xl font-semibold mb-4 dark:text-white">
                        {editingId ? 'Edit Address' : 'Add New Address'}
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <input
                            type="text"
                            placeholder="Full Name"
                            value={formData.fullName}
                            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="tel"
                            placeholder="Phone Number"
                            value={formData.phoneNumber}
                            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="Address Line 1"
                            value={formData.addressLine1}
                            onChange={(e) => setFormData({ ...formData, addressLine1: e.target.value })}
                            required
                            className="border rounded px-3 py-2 md:col-span-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="Address Line 2 (Optional)"
                            value={formData.addressLine2}
                            onChange={(e) => setFormData({ ...formData, addressLine2: e.target.value })}
                            className="border rounded px-3 py-2 md:col-span-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="City"
                            value={formData.city}
                            onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="State"
                            value={formData.state}
                            onChange={(e) => setFormData({ ...formData, state: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="ZIP Code"
                            value={formData.zipCode}
                            onChange={(e) => setFormData({ ...formData, zipCode: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                        <input
                            type="text"
                            placeholder="Country"
                            value={formData.country}
                            onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                            required
                            className="border rounded px-3 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        />
                    </div>
                    <label className="flex items-center mt-4 dark:text-white">
                        <input
                            type="checkbox"
                            checked={formData.isDefault}
                            onChange={(e) => setFormData({ ...formData, isDefault: e.target.checked })}
                            className="mr-2"
                        />
                        Set as default address
                    </label>
                    <div className="mt-4">
                        <button type="submit" className="bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600 mr-2">
                            Save
                        </button>
                        <button
                            type="button"
                            onClick={() => {
                                setShowForm(false);
                                setEditingId(null);
                                resetForm();
                            }}
                            className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {addresses.map((address) => (
                    <div key={address.id} className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow-md">
                        {address.isDefault && (
                            <span className="bg-blue-500 text-white text-xs px-2 py-1 rounded mb-2 inline-block">
                                Default
                            </span>
                        )}
                        <p className="font-semibold dark:text-white">{address.fullName}</p>
                        <p className="text-gray-600 dark:text-gray-400">{address.addressLine1}</p>
                        {address.addressLine2 && (
                            <p className="text-gray-600 dark:text-gray-400">{address.addressLine2}</p>
                        )}
                        <p className="text-gray-600 dark:text-gray-400">
                            {address.city}, {address.state} {address.zipCode}
                        </p>
                        <p className="text-gray-600 dark:text-gray-400">{address.country}</p>
                        <p className="text-gray-600 dark:text-gray-400">{address.phoneNumber}</p>
                        <div className="mt-3 flex gap-2">
                            <button
                                onClick={() => handleEdit(address)}
                                className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600 text-sm"
                            >
                                Edit
                            </button>
                            <button
                                onClick={() => handleDelete(address.id)}
                                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
