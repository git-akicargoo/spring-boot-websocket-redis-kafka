import styled from 'styled-components';
import { PriceData } from '../types/price';
import { useCrypto } from '../context/CryptoContext';
import { logger } from '../../../utils/logger';
import { ConnectionStatus } from './ConnectionStatus';
import { WebSocketStatus } from '../hooks/useCryptoWebSocket';

interface CoinPriceProps {
    priceData: PriceData;
}

const CoinPrice = ({ priceData }: CoinPriceProps) => {
    const { coinInfoMap } = useCrypto();
    const coinInfo = coinInfoMap[priceData.symbol];
    const isPositive = priceData.changeRate >= 0;
    const formattedPrice = priceData.price.toLocaleString();
    
    return (
        <PriceCard>
            <Header>{coinInfo?.koreanName || priceData.symbol}</Header>
            <Price>{formattedPrice} KRW</Price>
            <ChangeRate isPositive={isPositive}>
                {isPositive ? '+' : ''}{priceData.changeRate.toFixed(2)}%
            </ChangeRate>
            <Time>{new Date(priceData.timestamp).toLocaleString()}</Time>
        </PriceCard>
    );
};

interface Props {
    status: WebSocketStatus;
}

export const PriceDisplay = ({ status }: Props) => {
    const { priceDataMap } = useCrypto();

    if (!priceDataMap || Object.keys(priceDataMap).length === 0) {
        return <Container>Loading...</Container>;
    }

    return (
        <Container>
            <ConnectionStatus status={status} />
            <DebugPanel>
                <DebugButton onClick={() => logger.copyToClipboard()}>
                    📋 Copy Logs
                </DebugButton>
                <DebugButton onClick={() => logger.clear()}>
                    🧹 Clear Logs
                </DebugButton>
            </DebugPanel>
            <GridLayout>
                {Object.values(priceDataMap).map((priceData) => (
                    <CoinPrice 
                        key={priceData.symbol} 
                        priceData={priceData} 
                    />
                ))}
            </GridLayout>
        </Container>
    );
};

const Container = styled.div`
    padding: 20px;
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
`;

const GridLayout = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
`;

const PriceCard = styled.div`
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const Header = styled.div`
    font-size: 1.2em;
    font-weight: bold;
    margin-bottom: 10px;
`;

const Price = styled.div`
    font-size: 2em;
    font-weight: bold;
    margin: 10px 0;
`;

const ChangeRate = styled.div<{ isPositive: boolean }>`
    color: ${props => props.isPositive ? '#00b894' : '#d63031'};
    font-weight: bold;
    margin: 5px 0;
`;

const Time = styled.div`
    color: #666;
    font-size: 0.9em;
`;

const DebugPanel = styled.div`
    position: fixed;
    bottom: 20px;
    right: 20px;
    display: flex;
    gap: 10px;
`;

const DebugButton = styled.button`
    padding: 8px 16px;
    border-radius: 4px;
    background: #f1f1f1;
    border: 1px solid #ddd;
    cursor: pointer;
    &:hover {
        background: #e1e1e1;
    }
`; 