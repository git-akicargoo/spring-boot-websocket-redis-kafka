import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import { config } from '../../../shared/config/environment';
import { CHAT_SUBSCRIBE_URL, CHAT_PUBLISH_URL } from '../../../shared/constants/websocket';
import { Message } from '../types/chat';

interface UseWebSocketProps {
  onMessage: (message: Message) => void;
}

export const useWebSocket = ({ onMessage }: UseWebSocketProps) => {
  const clientRef = useRef<Client | null>(null);

  const connect = useCallback(() => {
    const client = new Client({
      brokerURL: config.wsUrl,
      onConnect: () => {
        client.subscribe(CHAT_SUBSCRIBE_URL, (message) => {
          const receivedMessage = JSON.parse(message.body);
          onMessage(receivedMessage);
        });
      }
    });

    client.activate();
    clientRef.current = client;
  }, [onMessage]);

  const sendMessage = useCallback((content: string) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: CHAT_PUBLISH_URL,
        body: JSON.stringify({
          content,
          timestamp: new Date().toISOString()
        })
      });
    }
  }, []);

  useEffect(() => {
    connect();
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [connect]);

  return { sendMessage, connect };
};