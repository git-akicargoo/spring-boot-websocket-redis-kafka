import { useEffect, useRef, useCallback } from 'react';
import { Client, StompHeaders, IMessage } from '@stomp/stompjs';
import { config } from '../../../config';
import { CHAT_SUBSCRIBE_URL, CHAT_PUBLISH_URL } from '../../../shared/constants/websocket';
import { Message } from '../types/chat';

interface MessageHandler {
  onMessage: (message: Message) => void;
}

export const useWebSocket = ({ onMessage }: MessageHandler) => {
  const client = useRef<Client | null>(null);

  const connect = useCallback(() => {
    client.current = new Client({
      brokerURL: `${config.ws.url}/ws`,
      onConnect: () => {
        console.log('Connected to WebSocket');
        client.current?.subscribe(CHAT_SUBSCRIBE_URL, (message: IMessage) => {
          const receivedMessage = JSON.parse(message.body) as Message;
          onMessage(receivedMessage);
        });
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
      }
    });

    client.current.activate();
  }, [onMessage]);

  useEffect(() => {
    connect();
    return () => {
      if (client.current) {
        client.current.deactivate();
      }
    };
  }, [connect]);

  const sendMessage = useCallback((message: string) => {
    if (client.current?.connected) {
      const chatMessage: Message = { content: message };
      client.current.publish({
        destination: CHAT_PUBLISH_URL,
        body: JSON.stringify(chatMessage),
        headers: {} as StompHeaders
      });
    }
  }, []);

  return { sendMessage };
};