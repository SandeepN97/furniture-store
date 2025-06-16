import React, { createContext, useContext, useState } from 'react';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [items, setItems] = useState([]);

  const addItem = (item) => {
    setItems((prev) => [...prev, item]);
  };

  const removeItem = (id) => {
    setItems((prev) => prev.filter((it) => it.id !== id));
  };

  const getTotalPrice = () => {
    return items.reduce((sum, it) => sum + Number(it.price || 0), 0);
  };

  return (
    <CartContext.Provider value={{ items, addItem, removeItem, getTotalPrice }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
