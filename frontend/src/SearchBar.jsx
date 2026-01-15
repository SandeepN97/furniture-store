import { useState } from 'react';

export default function SearchBar({ onSearch }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [sortBy, setSortBy] = useState('');
    const [inStockOnly, setInStockOnly] = useState(false);

    const handleSearch = () => {
        onSearch({
            search: searchTerm,
            minPrice: minPrice || null,
            maxPrice: maxPrice || null,
            sortBy,
            inStock: inStockOnly
        });
    };

    return (
        <div className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow-md mb-6">
            <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                <input
                    type="text"
                    placeholder="Search products..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                    className="border rounded px-4 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                />
                <input
                    type="number"
                    placeholder="Min Price"
                    value={minPrice}
                    onChange={(e) => setMinPrice(e.target.value)}
                    className="border rounded px-4 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                />
                <input
                    type="number"
                    placeholder="Max Price"
                    value={maxPrice}
                    onChange={(e) => setMaxPrice(e.target.value)}
                    className="border rounded px-4 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                />
                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="border rounded px-4 py-2 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                    <option value="">Sort By</option>
                    <option value="price_asc">Price: Low to High</option>
                    <option value="price_desc">Price: High to Low</option>
                    <option value="rating">Rating</option>
                    <option value="newest">Newest</option>
                </select>
                <button
                    onClick={handleSearch}
                    className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
                >
                    Search
                </button>
            </div>
            <div className="mt-3">
                <label className="flex items-center text-gray-700 dark:text-gray-300">
                    <input
                        type="checkbox"
                        checked={inStockOnly}
                        onChange={(e) => setInStockOnly(e.target.checked)}
                        className="mr-2"
                    />
                    In Stock Only
                </label>
            </div>
        </div>
    );
}
