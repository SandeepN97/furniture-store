import React, { useEffect } from 'react';
import { useCart } from './CartContext';

export default function CheckoutSuccess() {
  const { clearCart } = useCart();
  useEffect(() => {
    clearCart();
  }, [clearCart]);
  return <div>Payment successful! Thank you for your order.</div>;
}
