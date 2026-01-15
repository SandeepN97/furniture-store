import { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

export default function Reviews({ productId }) {
    const [reviews, setReviews] = useState([]);
    const [newReview, setNewReview] = useState({ rating: 5, comment: '' });
    const [showForm, setShowForm] = useState(false);
    const { user } = useAuth();
    const { showToast } = useToast();

    useEffect(() => {
        fetchReviews();
    }, [productId]);

    const fetchReviews = async () => {
        try {
            const response = await axios.get(`/api/reviews/product/${productId}`);
            setReviews(response.data.content || []);
        } catch (error) {
            console.error('Error fetching reviews:', error);
        }
    };

    const submitReview = async (e) => {
        e.preventDefault();
        if (!user) {
            showToast('Please login to write a review', 'error');
            return;
        }

        try {
            await axios.post('/api/reviews', {
                productId,
                rating: newReview.rating,
                comment: newReview.comment
            });
            showToast('Review submitted successfully!', 'success');
            setNewReview({ rating: 5, comment: '' });
            setShowForm(false);
            fetchReviews();
        } catch (error) {
            showToast(error.response?.data?.error || 'Failed to submit review', 'error');
        }
    };

    return (
        <div className="mt-8">
            <h3 className="text-2xl font-bold mb-4 dark:text-white">Customer Reviews</h3>

            {user && !showForm && (
                <button
                    onClick={() => setShowForm(true)}
                    className="mb-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                    Write a Review
                </button>
            )}

            {showForm && (
                <form onSubmit={submitReview} className="mb-6 p-4 bg-gray-100 dark:bg-gray-800 rounded">
                    <div className="mb-3">
                        <label className="block mb-2 dark:text-white">Rating</label>
                        <select
                            value={newReview.rating}
                            onChange={(e) => setNewReview({ ...newReview, rating: parseInt(e.target.value) })}
                            className="border rounded px-3 py-2 w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                        >
                            {[5, 4, 3, 2, 1].map(r => (
                                <key={r} value={r}>{r} Star{r > 1 ? 's' : ''}</option>
                            ))}
                        </select>
                    </div>
                    <div className="mb-3">
                        <label className="block mb-2 dark:text-white">Comment</label>
                        <textarea
                            value={newReview.comment}
                            onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                            className="border rounded px-3 py-2 w-full dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                            rows="4"
                            required
                        />
                    </div>
                    <button type="submit" className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 mr-2">
                        Submit
                    </button>
                    <button
                        type="button"
                        onClick={() => setShowForm(false)}
                        className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
                    >
                        Cancel
                    </button>
                </form>
            )}

            <div className="space-y-4">
                {reviews.length === 0 ? (
                    <p className="text-gray-500 dark:text-gray-400">No reviews yet. Be the first to review!</p>
                ) : (
                    reviews.map((review) => (
                        <div key={review.id} className="border-b pb-4 dark:border-gray-700">
                            <div className="flex items-center mb-2">
                                <div className="text-yellow-500">
                                    {'★'.repeat(review.rating)}
                                    {'☆'.repeat(5 - review.rating)}
                                </div>
                                <span className="ml-2 text-sm text-gray-600 dark:text-gray-400">
                                    {review.verified && '✓ Verified Purchase'}
                                </span>
                            </div>
                            <p className="text-gray-700 dark:text-gray-300">{review.comment}</p>
                            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                                {new Date(review.createdAt).toLocaleDateString()}
                            </p>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
