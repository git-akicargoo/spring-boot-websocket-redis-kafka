import { BrowserRouter, Routes, Route } from 'react-router-dom';
import HomePage from './pages/home/HomePage';
import ChatPage from './pages/chat/ChatPage';
import CryptoPage from './pages/crypto/CryptoPage';

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/crypto" element={<CryptoPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;