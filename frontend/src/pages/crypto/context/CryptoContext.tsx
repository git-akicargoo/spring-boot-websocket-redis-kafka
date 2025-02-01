import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { PriceData, CoinInfo } from '../types/price';
import { useCryptoWebSocket } from '../hooks/useCryptoWebSocket';
import axios from 'axios';
import { logger } from '../../../utils/logger';
import { config } from '../../../config';

interface CryptoContextType {
    priceDataMap: Record<string, PriceData>;
    coinInfoMap: Record<string, CoinInfo>;
    isLoading: boolean;
}

const CryptoContext = createContext<CryptoContextType | undefined>(undefined);

export const CryptoProvider = ({ children }: { children: ReactNode }) => {
    const [priceDataMap, setPriceDataMap] = useState<Record<string, PriceData>>({});
    const [coinInfoMap, setCoinInfoMap] = useState<Record<string, CoinInfo>>({});
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const loadCoinInfo = async () => {
            try {
                logger.info('Loading coin information...');
                const response = await axios.get<CoinInfo[]>(
                    `${config.api.baseUrl}${config.api.endpoints.coins}`
                );
                logger.info('Coin information API response:', response.data);
                
                if (response.data && Array.isArray(response.data)) {
                    const coinMap = response.data.reduce((acc, coin) => ({
                        ...acc,
                        [coin.symbol]: coin
                    }), {} as Record<string, CoinInfo>);
                    
                    logger.info('Processed coin map:', coinMap);
                    setCoinInfoMap(coinMap);
                } else {
                    logger.warn('Received invalid coin information format:', response.data);
                }
                setIsLoading(false);
            } catch (error: any) {
                logger.error('Failed to load coin information:', {
                    message: error?.message,
                    status: error?.response?.status,
                    data: error?.response?.data,
                    config: error?.config
                });
                setCoinInfoMap({
                    'KRW-BTC': { symbol: 'KRW-BTC', koreanName: '비트코인' },
                    'KRW-DOGE': { symbol: 'KRW-DOGE', koreanName: '도지코인' }
                });
                setIsLoading(false);
            }
        };

        loadCoinInfo();
    }, []);

    const handleWebSocketMessage = useCallback((price: PriceData) => {
        logger.info('Received price update:', price);
        setPriceDataMap(prev => {
            const newMap = {
                ...prev,
                [price.symbol]: price
            };
            logger.info('Updated price map:', newMap);
            return newMap;
        });
    }, []);

    useCryptoWebSocket({
        onMessage: handleWebSocketMessage
    });

    useEffect(() => {
        logger.info('Current price data map:', priceDataMap);
        logger.info('Current coin info map:', coinInfoMap);
    }, [priceDataMap, coinInfoMap]);

    const value = {
        priceDataMap,
        coinInfoMap,
        isLoading
    };

    return (
        <CryptoContext.Provider value={value}>
            {children}
        </CryptoContext.Provider>
    );
};

export const useCrypto = () => {
    const context = useContext(CryptoContext);
    if (!context) {
        throw new Error('useCrypto must be used within a CryptoProvider');
    }
    return context;
}; 