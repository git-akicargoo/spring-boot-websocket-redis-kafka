interface Config {
    api: {
        baseUrl: string;
        endpoints: {
            coins: string;
        };
    };
    ws: {
        url: string;
        endpoints: {
            crypto: string;
        };
    };
}

const getConfig = (): Config => {
    const isDev = import.meta.env.DEV;
    
    return {
        api: {
            baseUrl: isDev 
                ? import.meta.env.VITE_DEV_API_URL 
                : import.meta.env.VITE_PROD_API_URL,
            endpoints: {
                coins: '/api/crypto/coins'
            }
        },
        ws: {
            url: isDev 
                ? import.meta.env.VITE_DEV_WS_URL 
                : import.meta.env.VITE_PROD_WS_URL,
            endpoints: {
                crypto: '/ws/crypto'
            }
        }
    };
};

export const config = getConfig(); 