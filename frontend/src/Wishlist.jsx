import { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';
import { useCart } from './CartContext';

export default function Wishlist() {
    const [wishlist, setWishlist] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuth();
    const { showToast } = useToast();
    const { addToCart } = useCart();

    useEffect(() => {
        if (user) {
            fetchWishlist();
        } else {
            setLoading(false);
        }
    }, [user]);

    const fetchWishlist = async () => {
        try {
            const response = await axios.get('/api/wishlist');
            setWishlist(response.data);
        } catch (error) {
            showToast('Failed to load wishlist', 'error');
        } finally {
            setLoading(false);
        }
    };

    const removeFromWishlist = async (productId) => {
        try {
            await axios.delete(`/api/wishlist/${productId}`);
            setWishlist(wishlist.filter(item => item.product.id !== productId));
            showToast('Removed from wishlist', 'success');
        } catch (error) {
            showToast('Failed to remove item', 'error');
        }
    };

    const moveToCart = (product) => {
        addToCart(product);
        removeFromWishlist(product.id);
        showToast('Added to cart!', 'success');
    };

    if (!user) {
        return (
            <div className="container mx-auto px-4 py-8 text-center">
                <h2 className="text-2xl font-bold mb-4 dark:text-white">Please Login</h2>
                <p className="dark:text-gray-300 mb-4">You need to be logged in to view your wishlist.</p>
                <Link to="/login" className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600">
                    Login
                </Link>
            </div>
        );
    }

    if (loading) {
        return <div className="container mx-auto px-4 py-8 text-center dark:text-white">Loading...</div>;
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h2 className="text-3xl font-bold mb-6 dark:text-white">My Wishlist</h2>

            {wishlist.length === 0 ? (
                <div className="text-center py-12">
                    <p className="text-gray-500 dark:text-gray-400 mb-4">Your wishlist is empty</p>
                    <Link to="/" className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600">
                        Continue Shopping
                    </Link>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {wishlist.map((item) => (
                        <div key={item.id} className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
                            <Link to={`/products/${item.product.id}`}>
                                <img
                                    src={item.product.imageUrl || '/placeholder.jpg'}
                                    alt={item.product.name}
                                    className="w-full h-48 object-cover rounded mb-3"
                                />
                            </Link>
                            <Link to={`/products/${item.product.id}`}>
                                <h3 className="font-semibold text-lg mb-2 dark:text-white hover:text-blue-500">
                                    {item.product.name}
                                </h3>
                            </Link>
                            <p className="text-blue-600 dark:text-blue-400 font-bold text-xl mb-3">
                                ${item.product.price}
                            </p>
                            {item.product.inStock ? (
                                <button
                                    onClick={() => moveToCart(item.product)}
                                    className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600 mb-2"
                                >
                                    Add to Cart
                                </button>
                            ) : (
                                <p className="text-red-500 text-center mb-2">Out of Stock</p>
                            )}
                            <button
                                onClick={() => removeFromWishlist(item.product.id)}
                                className="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600"
                            >
                                Remove
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
