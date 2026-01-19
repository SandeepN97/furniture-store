import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  FaHome,
  FaCouch,
  FaGasPump,
  FaChargingStation,
  FaHome as FaHouse
} from 'react-icons/fa';

const BottomNav = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { path: '/', icon: FaHome, label: t('nav.dashboard') },
    { path: '/furniture', icon: FaCouch, label: t('nav.furniture') },
    { path: '/petrol', icon: FaGasPump, label: t('nav.petrol') },
    { path: '/ev-charging', icon: FaChargingStation, label: t('nav.evCharging') },
    { path: '/rental', icon: FaHouse, label: t('nav.rental') }
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg z-50">
      <div className="flex justify-around items-center h-20 max-w-screen-xl mx-auto">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;

          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`flex flex-col items-center justify-center flex-1 h-full transition-colors ${
                isActive
                  ? 'text-blue-600'
                  : 'text-gray-600 hover:text-blue-500'
              }`}
            >
              <Icon className="text-2xl mb-1" />
              <span className="text-xs font-medium">{item.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default BottomNav;
