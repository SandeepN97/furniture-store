import React, { useState } from 'react';
import axios from 'axios';

export default function EmailSignup() {
  const [email, setEmail] = useState('');
  const [msg, setMsg] = useState('');

  const submit = async () => {
    try {
      await axios.post('/api/newsletter/signup', { email });
      setMsg('Subscribed!');
      setEmail('');
    } catch {
      setMsg('Failed');
    }
  };

  return (
    <div className="mt-8">
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Your email"
        className="border p-2 mr-2"
      />
      <button onClick={submit} className="bg-blue-500 text-white px-3 py-1 rounded">
        Subscribe
      </button>
      {msg && <span className="ml-2">{msg}</span>}
    </div>
  );
}
