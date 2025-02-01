import styled from 'styled-components';
import { Message } from '../types/chat';

interface Props {
  message: Message;
}

export const ChatMessage = ({ message }: Props) => {
  return (
    <MessageContainer>
      <Sender>{message.sender || 'Anonymous'}</Sender>
      <Content>{message.content}</Content>
      <Time>
        {message.timestamp 
          ? new Date(message.timestamp).toLocaleTimeString()
          : new Date().toLocaleTimeString()}
      </Time>
    </MessageContainer>
  );
};

const MessageContainer = styled.div`
  margin-bottom: 8px;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
`;

const Sender = styled.div`
  font-weight: bold;
  margin-bottom: 4px;
`;

const Content = styled.div`
  margin-bottom: 4px;
`;

const Time = styled.div`
  font-size: 0.8em;
  color: #6c757d;
`; 