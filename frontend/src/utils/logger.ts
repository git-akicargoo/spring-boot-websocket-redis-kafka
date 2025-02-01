interface LogEntry {
    timestamp: string;
    type: 'info' | 'error' | 'warn';
    message: string;
    data?: any;
}

// 로그 최대 개수 설정
const MAX_LOGS = 1000;

class Logger {
    private logs: LogEntry[] = [];
    
    private createEntry(type: 'info' | 'error' | 'warn', args: any[]): LogEntry {
        return {
            timestamp: new Date().toISOString(),
            type,
            message: args[0],
            data: args.slice(1)
        };
    }
    
    private trimLogs() {
        if (this.logs.length > MAX_LOGS) {
            this.logs = this.logs.slice(-MAX_LOGS);
        }
    }
    
    info(...args: any[]) {
        console.log(...args);
        this.logs.push(this.createEntry('info', args));
        this.trimLogs();
    }
    
    error(...args: any[]) {
        console.error(...args);
        this.logs.push(this.createEntry('error', args));
        this.trimLogs();
    }
    
    warn(...args: any[]) {
        console.warn(...args);
        this.logs.push(this.createEntry('warn', args));
        this.trimLogs();
    }
    
    // 로그 내보내기
    export() {
        return JSON.stringify(this.logs, null, 2);
    }
    
    // 로그 복사
    copyToClipboard() {
        const text = this.logs.map(entry => 
            `[${entry.timestamp}] ${entry.type.toUpperCase()}: ${entry.message}`
        ).join('\n');
        
        navigator.clipboard.writeText(text)
            .then(() => console.log('Logs copied to clipboard'))
            .catch(err => console.error('Failed to copy logs:', err));
    }
    
    // 로그 초기화
    clear() {
        this.logs = [];
        console.clear();
    }
}

export const logger = new Logger(); 