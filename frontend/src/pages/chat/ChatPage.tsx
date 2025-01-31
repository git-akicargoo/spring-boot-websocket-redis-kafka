import { ChatWindow } from './components/ChatWindow';
import { ChatProvider } from './context/ChatContext';

const ChatPage = () => {
  return (
    <ChatProvider>
      <div className="chat-page">
        <h1>WebSocket 채팅</h1>
        <ChatWindow />
      </div>
    </ChatProvider>
  );
};

export default ChatPage; 