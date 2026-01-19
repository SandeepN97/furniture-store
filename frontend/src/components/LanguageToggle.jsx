import React from 'react';
import { useTranslation } from 'react-i18next';

const LanguageToggle = () => {
  const { i18n } = useTranslation();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'ne' ? 'en' : 'ne';
    i18n.changeLanguage(newLang);
  };

  return (
    <button
      onClick={toggleLanguage}
      className="fixed top-4 right-4 z-50 bg-white text-gray-800 px-4 py-2 rounded-full shadow-lg hover:shadow-xl transition-shadow font-semibold border-2 border-gray-200"
    >
      {i18n.language === 'ne' ? 'English' : 'नेपाली'}
    </button>
  );
};

export default LanguageToggle;
