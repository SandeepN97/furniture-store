import React, { useState } from 'react';
import axios from 'axios';
import { useTranslation } from 'react-i18next';

export default function AIAssistant() {
  const { t } = useTranslation();
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([]);

  const send = () => {
    if (!input) return;
    const userMsg = { role: 'user', text: input };
    setMessages((m) => [...m, userMsg]);
    axios
      .post('/api/chat', { message: input })
      .then((res) => {
        setMessages((m) => [...m, { role: 'ai', text: res.data.reply }]);
      })
      .catch(() => {
        setMessages((m) => [...m, { role: 'ai', text: t('aiError') }]);
      });
    setInput('');
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">{t('aiAssistant')}</h2>
      <div className="border p-2 h-64 overflow-y-auto mb-4">
        {messages.map((m, i) => (
          <div key={i} className={m.role === 'ai' ? 'text-blue-600' : 'text-black'}>
            <strong>{m.role === 'ai' ? 'AI: ' : 'You: '}</strong>
            {m.text}
          </div>
        ))}
      </div>
      <div className="flex gap-2">
        <input
          className="border flex-grow p-2"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder={t('askSomething')}
        />
        <button onClick={send} className="bg-blue-500 text-white px-4 py-2 rounded">
          {t('send')}
        </button>
      </div>
    </div>
  );
}
