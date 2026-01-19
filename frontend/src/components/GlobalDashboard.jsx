import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Cell } from 'recharts';
import axios from 'axios';

const GlobalDashboard = () => {
  const { t } = useTranslation();
  const [dashboardData, setDashboardData] = useState(null);
  const [period, setPeriod] = useState('month');
  const [loading, setLoading] = useState(false);

  const API_BASE = 'http://localhost:8080/api';

  useEffect(() => {
    fetchDashboardData();
  }, [period]);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE}/dashboard/profit-loss`, {
        params: { period }
      });
      setDashboardData(response.data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  // Transform data for bar chart
  const getChartData = () => {
    if (!dashboardData || !dashboardData.businesses) return [];

    return Object.entries(dashboardData.businesses).map(([name, data]) => ({
      name,
      income: data.income,
      expense: data.expense,
      profit: data.profit
    }));
  };

  // Colors for different businesses
  const businessColors = {
    'SAMJHANA FURNITURE': '#8B4513',
    'PETROL PUMP': '#FF6B35',
    'EV CHARGING': '#10B981',
    'HOUSE RENTAL': '#3B82F6'
  };

  const formatCurrency = (value) => {
    return `रू ${value.toLocaleString('en-IN', { maximumFractionDigits: 0 })}`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl">{t('common.loading')}</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 pb-20">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6 rounded-lg shadow-lg mb-6">
        <h1 className="text-2xl font-bold">{t('dashboard.title')}</h1>
        <p className="text-blue-100 mt-2">{t('app.subtitle')}</p>
      </div>

      {/* Period Selector */}
      <div className="bg-white rounded-lg shadow-md p-4 mb-6">
        <div className="grid grid-cols-3 gap-3">
          {['week', 'month', 'year'].map((p) => (
            <button
              key={p}
              onClick={() => setPeriod(p)}
              className={`h-12 rounded-lg font-semibold transition-colors ${
                period === p
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              {t(`dashboard.period.${p}`)}
            </button>
          ))}
        </div>
      </div>

      {/* Summary Cards */}
      {dashboardData?.totals && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-sm text-gray-500 mb-2">{t('dashboard.totalIncome')}</h3>
            <p className="text-2xl font-bold text-green-600">
              {formatCurrency(dashboardData.totals.income)}
            </p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-sm text-gray-500 mb-2">{t('dashboard.totalExpense')}</h3>
            <p className="text-2xl font-bold text-red-600">
              {formatCurrency(dashboardData.totals.expense)}
            </p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-sm text-gray-500 mb-2">{t('dashboard.totalProfit')}</h3>
            <p className={`text-2xl font-bold ${
              dashboardData.totals.profit >= 0 ? 'text-blue-600' : 'text-red-600'
            }`}>
              {formatCurrency(dashboardData.totals.profit)}
            </p>
          </div>
        </div>
      )}

      {/* Profit/Loss Bar Chart */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h2 className="text-xl font-bold mb-4">{t('petrol.profit')} / Loss by Business</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={getChartData()}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="name"
              angle={-45}
              textAnchor="end"
              height={100}
              style={{ fontSize: '12px' }}
            />
            <YAxis
              tickFormatter={(value) => `रू ${(value / 1000).toFixed(0)}k`}
            />
            <Tooltip
              formatter={(value) => formatCurrency(value)}
              contentStyle={{ fontSize: '14px' }}
            />
            <Legend />
            <Bar dataKey="income" fill="#10B981" name={t('dashboard.totalIncome')} />
            <Bar dataKey="expense" fill="#EF4444" name={t('dashboard.totalExpense')} />
            <Bar dataKey="profit" fill="#3B82F6" name={t('petrol.profit')} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Business Details Cards */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold">{t('dashboard.viewDetails')}</h2>
        {dashboardData?.businesses && Object.entries(dashboardData.businesses).map(([name, data]) => (
          <div key={name} className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-bold" style={{
                color: businessColors[name] || '#374151'
              }}>
                {name}
              </h3>
              <span className={`text-xl font-bold ${
                data.profit >= 0 ? 'text-green-600' : 'text-red-600'
              }`}>
                {formatCurrency(data.profit)}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">{t('dashboard.totalIncome')}</p>
                <p className="text-lg font-semibold text-green-600">
                  {formatCurrency(data.income)}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">{t('dashboard.totalExpense')}</p>
                <p className="text-lg font-semibold text-red-600">
                  {formatCurrency(data.expense)}
                </p>
              </div>
            </div>
            {/* Profit Margin Bar */}
            <div className="mt-4">
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-blue-600 h-2 rounded-full transition-all"
                  style={{
                    width: `${Math.min(100, Math.max(0, (data.profit / data.income) * 100))}%`
                  }}
                />
              </div>
              <p className="text-xs text-gray-500 mt-1">
                Margin: {data.income > 0 ? ((data.profit / data.income) * 100).toFixed(1) : 0}%
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default GlobalDashboard;
