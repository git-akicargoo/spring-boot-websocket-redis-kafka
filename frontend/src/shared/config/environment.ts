const isDevelopment = import.meta.env.MODE === 'development';

export const config = {
    wsUrl: `${isDevelopment ? import.meta.env.VITE_DEV_WS_PROTOCOL : import.meta.env.VITE_PROD_WS_PROTOCOL}://${
        isDevelopment 
            ? import.meta.env.VITE_DEV_API_BASE_URL.replace(/^https?:\/\//, '')
            : import.meta.env.VITE_PROD_API_BASE_URL.replace(/^https?:\/\//, '')
    }/ws-connect`,
    apiUrl: isDevelopment ? import.meta.env.VITE_DEV_API_BASE_URL : import.meta.env.VITE_PROD_API_BASE_URL
};