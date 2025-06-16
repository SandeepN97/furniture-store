import { useEffect } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

export default function OrderNotifications() {
  const { role } = useAuth();
  const { showToast } = useToast();

  useEffect(() => {
    if (role !== 'ADMIN') return;
    const socket = new SockJS('/ws');
    const client = Stomp.over(socket);
    client.connect({}, () => {
      client.subscribe('/topic/orders', (msg) => {
        const order = JSON.parse(msg.body);
        showToast(`New order #${order.id}`);
      });
    });
    return () => {
      if (client.connected) client.disconnect();
    };
  }, [role, showToast]);

  return null;
}
