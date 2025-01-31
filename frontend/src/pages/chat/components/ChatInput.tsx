import { useState } from 'react';
import styled from 'styled-components';
import { useChat } from '../context/ChatContext';

export const ChatInput = () => {
  const [message, setMessage] = useState('');
  const { sendMessage } = useChat();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim()) {
      sendMessage(message);
      setMessage('');
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="메시지를 입력하세요..."
      />
      <Button type="submit">전송</Button>
    </Form>
  );
};

const Form = styled.form`
  display: flex;
  gap: 8px;
`;

const Input = styled.input`
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
`;

const Button = styled.button`
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`; 