import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import axios from 'axios';

const EVChargingComponent = () => {
  const { t } = useTranslation();
  const [readings, setReadings] = useState([]);
  const [latestReading, setLatestReading] = useState(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [formData, setFormData] = useState({
    readingDate: new Date().toISOString().split('T')[0],
    openingReading: '',
    closingReading: '',
    ratePerUnit: '',
    notes: ''
  });
  const [loading, setLoading] = useState(false);

  const API_BASE = 'http://localhost:8080/api';

  useEffect(() => {
    fetchReadings();
    fetchLatestReading();
  }, []);

  const fetchReadings = async () => {
    try {
      const response = await axios.get(`${API_BASE}/ev-meter/all`);
      setReadings(response.data);
    } catch (error) {
      console.error('Error fetching readings:', error);
    }
  };

  const fetchLatestReading = async () => {
    try {
      const response = await axios.get(`${API_BASE}/ev-meter/latest`);
      setLatestReading(response.data);
    } catch (error) {
      console.error('Error fetching latest reading:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await axios.post(`${API_BASE}/ev-meter/add`, formData);
      alert(t('common.success'));
      setShowAddForm(false);
      setFormData({
        readingDate: new Date().toISOString().split('T')[0],
        openingReading: '',
        closingReading: '',
        ratePerUnit: '',
        notes: ''
      });
      fetchReadings();
      fetchLatestReading();
    } catch (error) {
      alert(t('common.error') + ': ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const calculateUnits = () => {
    const opening = parseFloat(formData.openingReading) || 0;
    const closing = parseFloat(formData.closingReading) || 0;
    return Math.max(0, closing - opening).toFixed(2);
  };

  const calculateRevenue = () => {
    const units = parseFloat(calculateUnits()) || 0;
    const rate = parseFloat(formData.ratePerUnit) || 0;
    return (units * rate).toFixed(2);
  };

  return (
    <div className="min-h-screen bg-gray-50 p-4 pb-20">
      {/* Header */}
      <div className="bg-green-600 text-white p-6 rounded-lg shadow-lg mb-6">
        <h1 className="text-2xl font-bold">{t('evCharging.title')}</h1>
        <p className="text-green-100 mt-2">{t('evCharging.dailyLog')}</p>
      </div>

      {/* Latest Reading Card */}
      {latestReading && (
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-lg font-semibold mb-4 text-gray-800">
            {t('common.date')}: {new Date(latestReading.readingDate).toLocaleDateString()}
          </h2>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">{t('evCharging.openingReading')}</p>
              <p className="text-xl font-bold text-gray-900">{latestReading.openingReading}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">{t('evCharging.closingReading')}</p>
              <p className="text-xl font-bold text-gray-900">{latestReading.closingReading}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">{t('evCharging.unitsConsumed')}</p>
              <p className="text-xl font-bold text-green-600">{latestReading.unitsConsumed}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">{t('evCharging.totalRevenue')}</p>
              <p className="text-xl font-bold text-blue-600">रू {latestReading.totalRevenue}</p>
            </div>
          </div>
        </div>
      )}

      {/* Add Reading Button */}
      {!showAddForm && (
        <button
          onClick={() => setShowAddForm(true)}
          className="w-full h-16 bg-blue-600 text-white text-lg font-semibold rounded-lg shadow-lg hover:bg-blue-700 transition-colors mb-6"
        >
          + {t('evCharging.addReading')}
        </button>
      )}

      {/* Add Reading Form */}
      {showAddForm && (
        <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <h2 className="text-xl font-bold mb-4">{t('evCharging.addReading')}</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                {t('common.date')}
              </label>
              <input
                type="date"
                name="readingDate"
                value={formData.readingDate}
                onChange={handleInputChange}
                required
                className="w-full h-12 px-4 border border-gray-300 rounded-lg text-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                {t('evCharging.openingReading')}
              </label>
              <input
                type="number"
                name="openingReading"
                value={formData.openingReading}
                onChange={handleInputChange}
                required
                step="0.01"
                placeholder="0.00"
                className="w-full h-12 px-4 border border-gray-300 rounded-lg text-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                {t('evCharging.closingReading')}
              </label>
              <input
                type="number"
                name="closingReading"
                value={formData.closingReading}
                onChange={handleInputChange}
                required
                step="0.01"
                placeholder="0.00"
                className="w-full h-12 px-4 border border-gray-300 rounded-lg text-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                {t('evCharging.ratePerUnit')} (रू)
              </label>
              <input
                type="number"
                name="ratePerUnit"
                value={formData.ratePerUnit}
                onChange={handleInputChange}
                required
                step="0.01"
                placeholder="0.00"
                className="w-full h-12 px-4 border border-gray-300 rounded-lg text-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Calculated Values */}
            {formData.openingReading && formData.closingReading && (
              <div className="bg-blue-50 p-4 rounded-lg space-y-2">
                <div className="flex justify-between">
                  <span className="font-medium">{t('evCharging.unitsConsumed')}:</span>
                  <span className="font-bold">{calculateUnits()} kWh</span>
                </div>
                {formData.ratePerUnit && (
                  <div className="flex justify-between">
                    <span className="font-medium">{t('evCharging.totalRevenue')}:</span>
                    <span className="font-bold text-green-600">रू {calculateRevenue()}</span>
                  </div>
                )}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                {t('common.description')}
              </label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleInputChange}
                rows="3"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg text-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="flex gap-3">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 h-14 bg-green-600 text-white text-lg font-semibold rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50"
              >
                {loading ? t('common.loading') : t('common.save')}
              </button>
              <button
                type="button"
                onClick={() => setShowAddForm(false)}
                className="flex-1 h-14 bg-gray-300 text-gray-800 text-lg font-semibold rounded-lg hover:bg-gray-400 transition-colors"
              >
                {t('common.cancel')}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Readings History */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-xl font-bold mb-4">{t('evCharging.dailyLog')}</h2>
        <div className="space-y-4">
          {readings.map((reading) => (
            <div key={reading.id} className="border-b pb-4 last:border-b-0">
              <div className="flex justify-between items-start mb-2">
                <span className="font-semibold text-gray-900">
                  {new Date(reading.readingDate).toLocaleDateString()}
                </span>
                <span className="text-green-600 font-bold">रू {reading.totalRevenue}</span>
              </div>
              <div className="grid grid-cols-3 gap-2 text-sm text-gray-600">
                <div>
                  <p className="text-xs text-gray-500">{t('evCharging.openingReading')}</p>
                  <p className="font-medium">{reading.openingReading}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500">{t('evCharging.closingReading')}</p>
                  <p className="font-medium">{reading.closingReading}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500">{t('evCharging.unitsConsumed')}</p>
                  <p className="font-medium">{reading.unitsConsumed} kWh</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default EVChargingComponent;
