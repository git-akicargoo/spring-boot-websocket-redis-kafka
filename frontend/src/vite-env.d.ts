/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_DEV_API_URL: string
    readonly VITE_PROD_API_URL: string
    readonly VITE_DEV_WS_URL: string
    readonly VITE_PROD_WS_URL: string
    readonly VITE_WS_SUBSCRIBE_PREFIX: string
    readonly VITE_WS_PUBLISH_PREFIX: string
    readonly VITE_WS_MESSAGES_ENDPOINT: string
    readonly VITE_WS_CHAT_ENDPOINT: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
