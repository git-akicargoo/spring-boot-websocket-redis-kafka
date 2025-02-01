import { Link } from 'react-router-dom';
import styled from 'styled-components';

const HomePage = () => {
  return (
    <Container>
      <Title>WebSocket Demo</Title>
      <Description>실시간 데모 애플리케이션입니다.</Description>
      <LinkContainer>
        <StyledLink to="/chat">채팅 시작하기</StyledLink>
        <StyledLink to="/crypto">암호화폐 시세보기</StyledLink>
      </LinkContainer>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 20px;
`;

const Title = styled.h1`
  margin-bottom: 20px;
`;

const Description = styled.p`
  margin-bottom: 30px;
  color: #666;
`;

const LinkContainer = styled.div`
  display: flex;
  gap: 20px;
`;

const StyledLink = styled(Link)`
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  
  &:hover {
    background-color: #0056b3;
  }
`;

export default HomePage; 