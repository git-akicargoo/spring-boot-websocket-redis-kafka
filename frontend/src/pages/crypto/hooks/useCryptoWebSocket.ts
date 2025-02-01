import { useEffect, useRef, useState } from 'react';
import { logger } from '../../../utils/logger';
import { PriceData } from '../types/price';
import { config } from '../../../config';

interface UseCryptoWebSocketProps {
    onMessage: (price: PriceData) => void;
}

export type WebSocketStatus = 'connecting' | 'connected' | 'disconnected' | 'error';

export const useCryptoWebSocket = ({ onMessage }: UseCryptoWebSocketProps) => {
    const wsRef = useRef<WebSocket | null>(null);
    const reconnectTimeoutRef = useRef<ReturnType<typeof setTimeout>>();
    const [status, setStatus] = useState<WebSocketStatus>('connecting');

    useEffect(() => {
        if (wsRef.current?.readyState === WebSocket.OPEN) {
            logger.info('WebSocket already connected');
            return;
        }

        const connect = () => {
            try {
                const wsUrl = `${config.ws.url}${config.ws.endpoints.crypto}`;
                logger.info('Connecting to WebSocket:', wsUrl);
                
                const ws = new WebSocket(wsUrl);
                wsRef.current = ws;

                ws.onopen = () => {
                    logger.info('WebSocket connected successfully');
                    setStatus('connected');
                    if (reconnectTimeoutRef.current) {
                        clearTimeout(reconnectTimeoutRef.current);
                        reconnectTimeoutRef.current = undefined;
                    }
                };

                ws.onmessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        logger.info('Received WebSocket message:', data);
                        onMessage(data);
                    } catch (error) {
                        logger.error('Failed to parse WebSocket message:', error);
                    }
                };

                ws.onerror = (error) => {
                    setStatus('error');
                    logger.error('WebSocket error:', error);
                };

                ws.onclose = (event) => {
                    setStatus('disconnected');
                    if (event.code !== 1000) {
                        logger.warn(`WebSocket closed unexpectedly. Code: ${event.code}, Reason: ${event.reason}`);
                        if (!reconnectTimeoutRef.current) {
                            reconnectTimeoutRef.current = setTimeout(() => {
                                logger.info('Attempting to reconnect...');
                                connect();
                                reconnectTimeoutRef.current = undefined;
                            }, 5000);
                        }
                    } else {
                        logger.info('WebSocket closed normally');
                    }
                };

            } catch (error) {
                logger.error('Failed to establish WebSocket connection:', error);
            }
        };

        connect();

        return () => {
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
            if (wsRef.current) {
                logger.info('Cleaning up WebSocket connection');
                wsRef.current.close(1000, 'Normal closure');
                wsRef.current = null;
            }
        };
    }, []);

    return { status };
}; 