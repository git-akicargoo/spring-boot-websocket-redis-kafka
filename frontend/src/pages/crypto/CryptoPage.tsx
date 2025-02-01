import { CryptoProvider } from './context/CryptoContext';
import { PriceDisplay } from './components/PriceDisplay';
import { useCryptoWebSocket } from './hooks/useCryptoWebSocket';

const CryptoPage = () => {
    const { status } = useCryptoWebSocket({
        onMessage: () => {}  // CryptoContext에서 처리하므로 여기서는 빈 함수
    });

    return (
        <CryptoProvider>
            <div className="crypto-page">
                <PriceDisplay status={status} />
            </div>
        </CryptoProvider>
    );
};

export default CryptoPage; 