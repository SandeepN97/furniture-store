import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';

export default function RoomDesigner() {
  const { t } = useTranslation();
  const items = [
    { id: 1, name: t('sofa'), color: 'bg-red-300' },
    { id: 2, name: t('table'), color: 'bg-green-300' },
    { id: 3, name: t('lamp'), color: 'bg-yellow-300' },
  ];
  const [placed, setPlaced] = useState([]);

  const startDrag = (e, item) => {
    e.dataTransfer.setData('item', JSON.stringify(item));
  };

  const allowDrop = (e) => {
    e.preventDefault();
  };

  const drop = (e) => {
    e.preventDefault();
    const item = JSON.parse(e.dataTransfer.getData('item'));
    const rect = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    setPlaced([...placed, { ...item, x, y }]);
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">{t('roomDesigner')}</h2>
      <div className="flex gap-2 mb-4">
        {items.map((i) => (
          <div
            key={i.id}
            draggable
            onDragStart={(e) => startDrag(e, i)}
            className={`w-16 h-16 flex items-center justify-center cursor-grab ${i.color}`}
          >
            {i.name}
          </div>
        ))}
      </div>
      <div
        className="border relative w-full h-96"
        onDragOver={allowDrop}
        onDrop={drop}
      >
        {placed.map((p, idx) => (
          <div
            key={idx}
            className={`absolute w-16 h-16 flex items-center justify-center ${p.color}`}
            style={{ left: p.x - 32, top: p.y - 32 }}
          >
            {p.name}
          </div>
        ))}
      </div>
    </div>
  );
}
