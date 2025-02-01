export interface PriceData {
    exchange: string;    // 거래소 이름
    symbol: string;      // 거래 쌍
    price: number;       // 현재 가격
    changeRate: number;  // 변동률
    timestamp: number;   // 타임스탬프
}

export interface CoinInfo {
    symbol: string;      // 거래 쌍 (예: KRW-BTC)
    koreanName: string;  // 한글 이름 (예: 비트코인)
} 