import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const StockConfigPage = () => {
    const [stockCodes, setStockCodes] = useState('');
    const [queryInterval, setBaseInterval] = useState(5);
    const [errors, setErrors] = useState({ stockCodes: '', queryInterval: '' });
    const navigate = useNavigate();


    const handleSubmit = (e) => {
        e.preventDefault();

        const newErrors = {};
        if (!stockCodes.trim()) {
            newErrors.stockCodes = '股票代码是必填项';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }
        setErrors({});
        const codesArray = stockCodes.split(',').map(code => code.trim());
        navigate('/stock-monitor', { state: { stockCodeList: codesArray } });
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>股票配置</h2>
            <form onSubmit={handleSubmit}>
                <label>
                    输入股票代码（用英文逗号分隔）:
                    <input
                        type="text"
                        value={stockCodes}
                        onChange={(e) => setStockCodes(e.target.value)}
                        style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                    />
                    {errors.stockCodes && (
                        <p style={{ color: 'red', margin: '5px 0' }}>{errors.stockCodes}</p>
                    )}
                </label>
                <label>
                    设置基础间隔时间（秒）:
                    <br />
                    目前正在测试中，建议不设小于5的值
                    <input
                        type="number"
                        value={queryInterval}
                        onChange={(e) => setBaseInterval(Number(e.target.value))}
                        style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                    />
                    {errors.queryInterval && (
                        <p style={{ color: 'red', margin: '5px 0' }}>{errors.queryInterval}</p>
                    )}
                </label>

                <button type="submit" style={{ padding: '10px 20px' }}>提交</button>
            </form>
        </div>

        
    );
};

export default StockConfigPage;