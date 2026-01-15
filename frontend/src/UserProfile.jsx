import { useState } from 'react';
import { useAuth } from './AuthContext';
import AddressManagement from './AddressManagement';

export default function UserProfile() {
    const { user } = useAuth();
    const [activeTab, setActiveTab] = useState('profile');

    if (!user) {
        return (
            <div className="container mx-auto px-4 py-8 text-center">
                <h2 className="text-2xl font-bold dark:text-white">Please Login</h2>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-6 dark:text-white">My Profile</h1>

            <div className="flex border-b dark:border-gray-700 mb-6">
                <button
                    onClick={() => setActiveTab('profile')}
                    className={`px-6 py-3 ${activeTab === 'profile' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600 dark:text-gray-400'}`}
                >
                    Profile Info
                </button>
                <button
                    onClick={() => setActiveTab('addresses')}
                    className={`px-6 py-3 ${activeTab === 'addresses' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600 dark:text-gray-400'}`}
                >
                    Addresses
                </button>
            </div>

            {activeTab === 'profile' && (
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md max-w-2xl">
                    <h2 className="text-xl font-semibold mb-4 dark:text-white">Account Information</h2>
                    <div className="space-y-3">
                        <div>
                            <label className="text-gray-600 dark:text-gray-400 block mb-1">Name</label>
                            <p className="font-semibold dark:text-white">{user.name}</p>
                        </div>
                        <div>
                            <label className="text-gray-600 dark:text-gray-400 block mb-1">Email</label>
                            <p className="font-semibold dark:text-white">{user.email}</p>
                        </div>
                        <div>
                            <label className="text-gray-600 dark:text-gray-400 block mb-1">Role</label>
                            <p className="font-semibold dark:text-white">{user.role}</p>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === 'addresses' && <AddressManagement />}
        </div>
    );
}
