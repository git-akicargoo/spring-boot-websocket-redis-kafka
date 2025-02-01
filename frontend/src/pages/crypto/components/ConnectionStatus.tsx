import { useState, useEffect } from 'react';
import styled from 'styled-components';

interface Props {
    status: 'connecting' | 'connected' | 'disconnected' | 'error';
}

export const ConnectionStatus = ({ status }: Props) => {
    const [connectedTime, setConnectedTime] = useState<string>('00:00:00');
    
    useEffect(() => {
        let intervalId: number;
        const startTime = new Date();
        
        if (status === 'connected') {
            intervalId = window.setInterval(() => {
                const now = new Date();
                const diff = now.getTime() - startTime.getTime();
                const hours = Math.floor(diff / (1000 * 60 * 60));
                const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((diff % (1000 * 60)) / 1000);
                
                setConnectedTime(
                    `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
                );
            }, 1000);
        }
        
        return () => {
            if (intervalId) {
                window.clearInterval(intervalId);
            }
        };
    }, [status]);
    
    return (
        <Container>
            <StatusDot status={status} />
            <StatusText>
                {status === 'connected' ? `Connected (${connectedTime})` : status}
            </StatusText>
        </Container>
    );
};

const Container = styled.div`
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    position: fixed;
    top: 20px;
    right: 20px;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const StatusDot = styled.div<{ status: string }>`
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: ${({ status }) => {
        switch (status) {
            case 'connected': return '#00b894';
            case 'connecting': return '#fdcb6e';
            case 'error': return '#d63031';
            default: return '#636e72';
        }
    }};
`;

const StatusText = styled.span`
    font-size: 14px;
    color: #2d3436;
`; 