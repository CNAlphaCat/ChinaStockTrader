import React, { useState, useEffect, useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { monitorStock } from '../../services/stockMonitorService';

const StockMonitorPage = () => {
    const [stocks, setStocks] = useState([]);

    const location = useLocation();
    const stockCodeList = useMemo(() => location.state?.stockCodeList || [], [location.state]);
    const queryInterval = location.state?.queryInterval || 5;
    const notifications = location.state?.notifications || { title: false, alert: false, sound: false };
    console.log('notifications', notifications);

    useEffect(() => {
        let isFetching = false;
        const fetchStockData = async () => {
            if (isFetching || stockCodeList.length === 0) return;
            isFetching = true;
            try {
                const data = await monitorStock(stockCodeList);
                setStocks(data.filter(stock => stock));
            } finally {
                isFetching = false;
            }
        };

        fetchStockData();

        const getRandomInterval = () => {
            const fluctuation = queryInterval * 1000 * 0.2;
            return queryInterval * 1000 + Math.random() * fluctuation * 2 - fluctuation;
        };

        const intervalId = setInterval(fetchStockData, getRandomInterval());
    
        return () => {
            clearInterval(intervalId);
        };
    }, [stockCodeList, queryInterval]);

    useEffect(() => {
        if (!notifications.title) return;

        const originalTitle = document.title;
    
        const updateTitle = () => {
            const alertStock = stocks.find(stock => parseFloat(stock.changePercentInThreeMinutes) >= 1);
            if (alertStock) {
                document.title = `⚠️ ${alertStock.stockCode} 涨速超标！`;
            } else {
                document.title = originalTitle;
            }
        };
    
        updateTitle();
    
        return () => {
            document.title = originalTitle;
        };
    }, [stocks]);

    useEffect(() => {
        if (!notifications.sound) return;
        const playAlertSound = () => {
            const audio = new Audio('/notification.mp3');
            audio.play();
        };
    
        const checkForAlerts = () => {
            stocks.forEach(stock => {
                if (parseFloat(stock.changePercentInThreeMinutes) >= 1) {
                    playAlertSound();
                }
            });
        };
    
        checkForAlerts();
    }, [stocks]);

    useEffect(() => {
        if (!notifications.alert) return;
        const sendNotification = () => {
            if (Notification.permission === 'granted') {
                stocks.forEach(stock => {
                    if (parseFloat(stock.changePercentInThreeMinutes) >= 1) {
                        new Notification('股票提醒', {
                            body: `股票 ${stock.stockCode} 涨速超过阈值！`,
                        });
                    }
                });
            }
        };
    
        if (Notification.permission === 'default') {
            Notification.requestPermission();
        }
    
        sendNotification();
    }, [stocks]);

    return (
        <div style={{ padding: '20px' }}>
            <h2>股票监控</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'center' }}>
                <thead>
                    <tr style={{ backgroundColor: '#f2f2f2' }}>
                        <th style={{ border: '1px solid #ddd', padding: '8px' }}>股票代码</th>
                        <th style={{ border: '1px solid #ddd', padding: '8px' }}>价格</th>
                        <th style={{ border: '1px solid #ddd', padding: '8px' }}>涨幅</th>
                        <th style={{ border: '1px solid #ddd', padding: '8px' }}>三分钟涨速</th>
                        <th style={{ border: '1px solid #ddd', padding: '8px' }}>当前成交额</th>
                    </tr>
                </thead>
                <tbody>
                    {stocks.filter(stock => stock).map((stock, index) => (
                        <tr
                            key={index}
                            style={{
                                backgroundColor: parseFloat(stock.changePercentInThreeMinutes) >= 1 ? '#ffe6e6' : 'inherit',
                            }}
                        >
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.stockCode}</td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.closePrice}</td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.changePercent}</td>
                            <td
                                style={{
                                    border: '1px solid #ddd',
                                    padding: '8px',
                                    color: parseFloat(stock.changePercentInThreeMinutes) >= 1 ? 'red' : 'inherit',
                                    fontWeight: parseFloat(stock.changePercentInThreeMinutes) >= 1 ? 'bold' : 'normal',
                                }}
                            >
                                {stock.changePercentInThreeMinutes}
                            </td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.amount}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StockMonitorPage;