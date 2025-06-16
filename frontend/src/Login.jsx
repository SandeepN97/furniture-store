import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { useTranslation } from 'react-i18next';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();
  const { login } = useAuth();
  const { t } = useTranslation();

  const submit = async () => {
    const errs = {};
    if (!email) errs.email = t('emailRequired');
    if (!password) errs.password = t('passwordRequired');
    if (Object.keys(errs).length) {
      setErrors(errs);
      return;
    }
    setErrors({});
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      setError(t('loginFailed'));
    }
  };

  return (
    <div className="p-4 max-w-sm mx-auto">
      <h2 className="text-xl font-bold mb-4">{t('login')}</h2>
      <div className="mb-2">
        <input
          type="email"
          className="border p-2 w-full"
          placeholder={t('emailPlaceholder')}
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        {errors.email && <p className="text-red-600 text-sm">{errors.email}</p>}
      </div>
      <div className="mb-2">
        <input
          type="password"
          className="border p-2 w-full"
          placeholder={t('passwordPlaceholder')}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {errors.password && (
          <p className="text-red-600 text-sm">{errors.password}</p>
        )}
      </div>
      <button
        onClick={submit}
        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        {t('login')}
      </button>
      {error && <p className="mt-2 text-red-600">{error}</p>}
    </div>
  );
}
