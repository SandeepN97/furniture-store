import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from './ToastContext';

export default function VoiceNavigator() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const start = () => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      showToast('Speech recognition not supported');
      return;
    }
    const rec = new SpeechRecognition();
    rec.lang = 'en-US';
    rec.interimResults = false;
    rec.maxAlternatives = 1;
    rec.onresult = (e) => {
      const text = e.results[0][0].transcript.toLowerCase();
      showToast(`Heard: ${text}`);
      if (text.includes('cart')) navigate('/cart');
      else if (text.includes('checkout')) navigate('/checkout');
      else if (text.includes('orders')) navigate('/orders');
      else if (text.includes('login')) navigate('/login');
      else if (text.includes('admin')) navigate('/admin');
      else if (text.includes('blog')) navigate('/blog');
      else if (text.includes('home')) navigate('/');
    };
    rec.start();
  };

  return (
    <button
      onClick={start}
      className="fixed bottom-4 left-4 bg-blue-500 text-white p-3 rounded-full shadow-lg"
    >
      🎤
    </button>
  );
}
