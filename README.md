# 실시간 암호화폐 가격 추적 시스템

업비트 API를 활용한 실시간 코인 가격 모니터링 시스템
([Upbit API Docs](https://docs.upbit.com/))

## 시스템 구조
- Backend: Spring Boot + WebSocket (port: 8080)
- Frontend: React + TypeScript (port: 5173)

## 페이지 경로
- 메인: `http://localhost:5173/`
- 코인 모니터링: `http://localhost:5173/crypto`
  - 실시간 코인 가격 목록
  - WebSocket 연결 상태 표시
  - 로그 모니터링

## 주요 기능
- 업비트 실시간 코인 가격 업데이트
- 연결 상태 모니터링
  - 🟢 Connected (00:05:23)
  - 🟡 Connecting
  - 🔴 Error
  - ⚫ Disconnected
- 자동 재연결 기능
- 로그 시스템 (최근 1000개 유지)

## API
- WebSocket: ws://localhost:8080/ws-connect
- REST API: http://localhost:8080/api/v1/prices
