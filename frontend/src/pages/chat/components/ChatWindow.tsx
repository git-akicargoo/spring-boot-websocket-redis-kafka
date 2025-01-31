import { useRef, useEffect } from 'react';
import styled from 'styled-components';
import { ChatMessage } from './ChatMessage';
import { ChatInput } from './ChatInput';
import { useChat } from '../context/ChatContext';

export const ChatWindow = () => {
  const { messages } = useChat();
  const messageContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (messageContainerRef.current) {
      messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
    }
  }, [messages]);

  return (
    <Container>
      <MessageContainer ref={messageContainerRef}>
        {messages.map((msg, index) => (
          <ChatMessage key={index} message={msg} />
        ))}
      </MessageContainer>
      <ChatInput />
    </Container>
  );
};

const Container = styled.div`
  width: 600px;
  margin: 0 auto;
  padding: 20px;
`;

const MessageContainer = styled.div`
  height: 400px;
  border: 1px solid #ccc;
  border-radius: 4px;
  overflow-y: auto;
  margin-bottom: 10px;
  padding: 10px;
`; 