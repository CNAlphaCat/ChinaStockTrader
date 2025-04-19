import React, { useState, useEffect } from 'react';

const StockMonitorPage = () => {
    const [stocks, setStocks] = useState([]);

    useEffect(() => {

        const fetchStockData = async () => {
            const mockData = [
                { code: '600519', price: 1800, change: '+2.5%', speed: '+0.8%', volume: '120亿' },
                { code: '000858', price: 200, change: '-1.2%', speed: '-0.3%', volume: '30亿' },
                { code: '300750', price: 500, change: '+3.1%', speed: '+1.2%', volume: '50亿' },
            ];
            setStocks(mockData);
        };

        fetchStockData();
    }, []);

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
                    {stocks.map((stock, index) => (
                        <tr
                            key={index}
                            style={{
                                backgroundColor: parseFloat(stock.speed) >= 1 ? '#ffe6e6' : 'inherit', // 背景色变红
                            }}
                        >
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.code}</td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.price}</td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.change}</td>
                            <td
                                style={{
                                    border: '1px solid #ddd',
                                    padding: '8px',
                                    color: parseFloat(stock.speed) >= 1 ? 'red' : 'inherit',
                                    fontWeight: parseFloat(stock.speed) >= 1 ? 'bold' : 'normal',
                                }}
                            >
                                {stock.speed}
                            </td>
                            <td style={{ border: '1px solid #ddd', padding: '8px' }}>{stock.volume}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StockMonitorPage;