import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import BottomNav from './components/BottomNav';
import LanguageToggle from './components/LanguageToggle';
import GlobalDashboard from './components/GlobalDashboard';
import EVChargingComponent from './components/EVChargingComponent';

// Placeholder components for other pages
const FurniturePage = () => {
  const { t } = useTranslation();
  return (
    <div className="min-h-screen bg-gray-50 p-4 pb-20">
      <div className="bg-amber-700 text-white p-6 rounded-lg shadow-lg mb-6">
        <h1 className="text-2xl font-bold">{t('furniture.title')}</h1>
      </div>
      <div className="bg-white rounded-lg shadow-md p-6">
        <p className="text-gray-600">Furniture inventory and sales module coming soon...</p>
      </div>
    </div>
  );
};

const PetrolPage = () => {
  const { t } = useTranslation();
  return (
    <div className="min-h-screen bg-gray-50 p-4 pb-20">
      <div className="bg-orange-600 text-white p-6 rounded-lg shadow-lg mb-6">
        <h1 className="text-2xl font-bold">{t('petrol.title')}</h1>
      </div>
      <div className="bg-white rounded-lg shadow-md p-6">
        <p className="text-gray-600">Petrol pump management module coming soon...</p>
        <p className="text-sm text-gray-500 mt-2">
          Features: Dynamic pricing, fuel batch tracking, profit calculation
        </p>
      </div>
    </div>
  );
};

const RentalPage = () => {
  const { t } = useTranslation();
  return (
    <div className="min-h-screen bg-gray-50 p-4 pb-20">
      <div className="bg-purple-600 text-white p-6 rounded-lg shadow-lg mb-6">
        <h1 className="text-2xl font-bold">{t('rental.title')}</h1>
      </div>
      <div className="bg-white rounded-lg shadow-md p-6">
        <p className="text-gray-600">House rental management module coming soon...</p>
      </div>
    </div>
  );
};

function App() {
  return (
    <Router>
      <div className="App">
        <LanguageToggle />
        <Routes>
          <Route path="/" element={<GlobalDashboard />} />
          <Route path="/furniture" element={<FurniturePage />} />
          <Route path="/petrol" element={<PetrolPage />} />
          <Route path="/ev-charging" element={<EVChargingComponent />} />
          <Route path="/rental" element={<RentalPage />} />
        </Routes>
        <BottomNav />
      </div>
    </Router>
  );
}

export default App;
