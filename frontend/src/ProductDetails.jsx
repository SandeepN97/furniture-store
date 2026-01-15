import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import { useCart } from './CartContext';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';
import Spinner from './Spinner';
import Reviews from './Reviews';

export default function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [relatedProducts, setRelatedProducts] = useState([]);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [inWishlist, setInWishlist] = useState(false);
  const { addItem } = useCart();
  const { user } = useAuth();
  const { showToast } = useToast();

  useEffect(() => {
    axios.get(`/api/products/${id}`)
      .then(res => {
        setProduct(res.data);
        fetchRelatedProducts(id);
        if (user) {
          checkWishlist(id);
        }
      })
      .catch(() => {});
  }, [id, user]);

  const fetchRelatedProducts = async (productId) => {
    try {
      const response = await axios.get(`/api/products/${productId}/related`);
      setRelatedProducts(response.data);
    } catch (error) {
      console.error('Failed to fetch related products');
    }
  };

  const checkWishlist = async (productId) => {
    try {
      const response = await axios.get(`/api/wishlist/check/${productId}`);
      setInWishlist(response.data.inWishlist);
    } catch (error) {
      console.error('Failed to check wishlist');
    }
  };

  const toggleWishlist = async () => {
    if (!user) {
      showToast('Please login to add to wishlist', 'error');
      return;
    }

    try {
      if (inWishlist) {
        await axios.delete(`/api/wishlist/${id}`);
        setInWishlist(false);
        showToast('Removed from wishlist', 'success');
      } else {
        await axios.post('/api/wishlist', { productId: id });
        setInWishlist(true);
        showToast('Added to wishlist', 'success');
      }
    } catch (error) {
      showToast('Failed to update wishlist', 'error');
    }
  };

  if (!product) {
    return <Spinner />;
  }

  const allImages = [product.imageUrl, ...(product.additionalImages || [])].filter(Boolean);

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
        {/* Product Images */}
        <div>
          <div className="mb-4">
            <img
              src={allImages[currentImageIndex] || '/placeholder.jpg'}
              alt={product.name}
              className="w-full h-96 object-cover rounded-lg shadow-md"
            />
          </div>
          {allImages.length > 1 && (
            <div className="flex gap-2 overflow-x-auto">
              {allImages.map((img, idx) => (
                <img
                  key={idx}
                  src={img}
                  alt={`${product.name} ${idx + 1}`}
                  onClick={() => setCurrentImageIndex(idx)}
                  className={`w-20 h-20 object-cover rounded cursor-pointer ${
                    idx === currentImageIndex ? 'ring-2 ring-blue-500' : ''
                  }`}
                />
              ))}
            </div>
          )}
        </div>

        {/* Product Info */}
        <div>
          <h1 className="text-3xl font-bold mb-2 dark:text-white">{product.name}</h1>

          {product.averageRating > 0 && (
            <div className="flex items-center mb-3">
              <span className="text-yellow-500 text-lg">
                {'★'.repeat(Math.round(product.averageRating))}
                {'☆'.repeat(5 - Math.round(product.averageRating))}
              </span>
              <span className="ml-2 text-gray-600 dark:text-gray-400">
                ({product.reviewCount} reviews)
              </span>
            </div>
          )}

          <p className="text-3xl font-bold text-blue-600 dark:text-blue-400 mb-4">
            ${product.price}
          </p>

          {product.category && (
            <p className="text-gray-600 dark:text-gray-400 mb-2">
              Category: <span className="font-semibold">{product.category.name}</span>
            </p>
          )}

          {product.sku && (
            <p className="text-gray-600 dark:text-gray-400 mb-2">SKU: {product.sku}</p>
          )}

          {product.stockQuantity !== null && (
            <div className="mb-4">
              {product.inStock ? (
                <p className="text-green-600 dark:text-green-400">
                  In Stock ({product.stockQuantity} available)
                  {product.stockQuantity <= product.lowStockThreshold && ' - Low Stock!'}
                </p>
              ) : (
                <p className="text-red-600 dark:text-red-400">Out of Stock</p>
              )}
            </div>
          )}

          <p className="text-gray-700 dark:text-gray-300 mb-6">{product.description}</p>

          {product.material && (
            <p className="text-gray-600 dark:text-gray-400 mb-2">Material: {product.material}</p>
          )}
          {product.color && (
            <p className="text-gray-600 dark:text-gray-400 mb-2">Color: {product.color}</p>
          )}
          {product.dimensions && (
            <p className="text-gray-600 dark:text-gray-400 mb-2">Dimensions: {product.dimensions}</p>
          )}
          {product.weight && (
            <p className="text-gray-600 dark:text-gray-400 mb-4">Weight: {product.weight} kg</p>
          )}

          <div className="flex gap-3">
            {product.inStock && (
              <button
                onClick={() => {
                  addItem(product);
                  showToast('Added to cart!', 'success');
                }}
                className="bg-blue-500 text-white px-6 py-3 rounded hover:bg-blue-600 flex-1"
              >
                Add to Cart
              </button>
            )}
            <button
              onClick={toggleWishlist}
              className={`${inWishlist ? 'bg-red-500 hover:bg-red-600' : 'bg-gray-500 hover:bg-gray-600'} text-white px-6 py-3 rounded`}
            >
              {inWishlist ? '♥ In Wishlist' : '♡ Add to Wishlist'}
            </button>
          </div>
        </div>
      </div>

      {/* Reviews Section */}
      <Reviews productId={id} />

      {/* Related Products */}
      {relatedProducts.length > 0 && (
        <div className="mt-12">
          <h3 className="text-2xl font-bold mb-6 dark:text-white">Related Products</h3>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            {relatedProducts.map((relProduct) => (
              <Link
                key={relProduct.id}
                to={`/products/${relProduct.id}`}
                className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 hover:shadow-lg transition"
              >
                <img
                  src={relProduct.imageUrl || '/placeholder.jpg'}
                  alt={relProduct.name}
                  className="w-full h-48 object-cover rounded mb-3"
                />
                <h4 className="font-semibold dark:text-white mb-2">{relProduct.name}</h4>
                <p className="text-blue-600 dark:text-blue-400 font-bold">${relProduct.price}</p>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
