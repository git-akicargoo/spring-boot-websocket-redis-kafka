import { createContext, useContext, useState, ReactNode } from 'react';
import { Message } from '../types/chat';
import { useWebSocket } from '../hooks/useWebSocket';

interface ChatContextType {
  messages: Message[];
  sendMessage: (content: string) => void;
}

const ChatContext = createContext<ChatContextType | undefined>(undefined);

export const ChatProvider = ({ children }: { children: ReactNode }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const { sendMessage: wssSendMessage } = useWebSocket({
    onMessage: (message: Message) => {
      setMessages(prev => [...prev, message]);
    }
  });

  const sendMessage = (content: string) => {
    wssSendMessage(content);
  };

  return (
    <ChatContext.Provider value={{ messages, sendMessage }}>
      {children}
    </ChatContext.Provider>
  );
};

export const useChat = () => {
  const context = useContext(ChatContext);
  if (!context) {
    throw new Error('useChat must be used within a ChatProvider');
  }
  return context;
}; 