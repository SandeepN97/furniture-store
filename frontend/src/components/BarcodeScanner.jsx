import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { BarcodeScanner } from 'react-qr-barcode-scanner';

const BarcodeScannerComponent = ({ onScanSuccess, onPhotoCapture }) => {
  const { t } = useTranslation();
  const [scanning, setScanning] = useState(false);
  const [showCamera, setShowCamera] = useState(false);

  const handleScan = (err, result) => {
    if (result) {
      onScanSuccess(result.text);
      setScanning(false);
    }
  };

  const handlePhotoCapture = (e) => {
    const file = e.target.files[0];
    if (file) {
      onPhotoCapture(file);
      setShowCamera(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-xl font-bold mb-4">{t('furniture.scanBarcode')}</h2>

      <div className="space-y-4">
        {/* Scan Barcode Button */}
        <button
          onClick={() => {
            setScanning(!scanning);
            setShowCamera(false);
          }}
          className="w-full h-16 bg-blue-600 text-white text-lg font-semibold rounded-lg shadow-lg hover:bg-blue-700 transition-colors"
        >
          {scanning ? t('common.cancel') : t('furniture.scanBarcode')}
        </button>

        {/* Scanner View */}
        {scanning && (
          <div className="border-4 border-blue-500 rounded-lg overflow-hidden">
            <BarcodeScanner
              onUpdate={handleScan}
              style={{ width: '100%' }}
            />
          </div>
        )}

        {/* OR Divider */}
        <div className="flex items-center gap-4">
          <div className="flex-1 border-t border-gray-300"></div>
          <span className="text-gray-500 font-medium">{t('common.or')}</span>
          <div className="flex-1 border-t border-gray-300"></div>
        </div>

        {/* Take Photo Button */}
        <label className="block">
          <input
            type="file"
            accept="image/*"
            capture="environment"
            onChange={handlePhotoCapture}
            className="hidden"
          />
          <div className="w-full h-16 bg-green-600 text-white text-lg font-semibold rounded-lg shadow-lg hover:bg-green-700 transition-colors cursor-pointer flex items-center justify-center">
            📷 {t('furniture.takePhoto')}
          </div>
        </label>

        <p className="text-sm text-gray-500 text-center">
          If barcode is not recognized, take a photo for later identification
        </p>
      </div>
    </div>
  );
};

export default BarcodeScannerComponent;
