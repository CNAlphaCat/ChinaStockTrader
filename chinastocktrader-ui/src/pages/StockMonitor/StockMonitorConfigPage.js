import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const StockMonitorConfigPage = () => {
    const [stockCodes, setStockCodes] = useState(['']);
    const [queryInterval, setBaseInterval] = useState(5);
    const [notifications, setNotifications] = useState({
        title: false,
        alert: false,
        sound: false,
    });

    const [errors, setErrors] = useState({ stockCodes: '', queryInterval: '' });
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        const newErrors = {};
        if (stockCodes.some(code => !code.trim())) {
            newErrors.stockCodes = '股票代码不能为空';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }
        setErrors({});
        const codesArray = stockCodes.map(code => code.trim());
        navigate('/stock-monitor', { state: { stockCodeList: codesArray, notifications } });
    };

    const handleNotificationChange = (type) => {
        setNotifications((prev) => ({
            ...prev,
            [type]: !prev[type],
        }));
    };

    const handleStockCodeChange = (index, value) => {
        const updatedStockCodes = [...stockCodes];
        updatedStockCodes[index] = value;
        setStockCodes(updatedStockCodes);
    };

    const addStockCodeRow = () => {
        setStockCodes([...stockCodes, '']);
    };

    const removeStockCodeRow = (index) => {
        const updatedStockCodes = stockCodes.filter((_, i) => i !== index);
        setStockCodes(updatedStockCodes);
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>股票配置</h2>
            <form onSubmit={handleSubmit}>
            <label>
                    输入股票代码（每行一个）:
                    <div style={{ margin: '10px 0' }}>
                        {stockCodes.map((code, index) => (
                            <div key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
                                <input
                                    type="text"
                                    value={code}
                                    onChange={(e) => handleStockCodeChange(index, e.target.value)}
                                    style={{ flex: 1, padding: '8px', marginRight: '10px' }}
                                />
                                <button
                                    type="button"
                                    onClick={() => removeStockCodeRow(index)}
                                    style={{ padding: '5px 10px', backgroundColor: 'red', color: 'white', border: 'none', cursor: 'pointer' }}
                                >
                                    删除
                                </button>
                            </div>
                        ))}
                        <button
                            type="button"
                            onClick={addStockCodeRow}
                            style={{ padding: '8px 15px', backgroundColor: 'green', color: 'white', border: 'none', cursor: 'pointer' }}
                        >
                            添加股票代码
                        </button>
                    </div>
                    {errors.stockCodes && (
                        <p style={{ color: 'red', margin: '5px 0' }}>{errors.stockCodes}</p>
                    )}
                </label>
                <label>
                    设置每次抓取的请求间隔时间（秒）:
                    <br />
                    如果自己有代理，可以考虑设置一个短的时间，默认是5秒刷新一次数据
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

                <div>
                    <h3>通知设置</h3>
                    <label>
                        <input
                            type="checkbox"
                            checked={notifications.title}
                            onChange={() => handleNotificationChange('title')}
                        />
                        启用标题通知
                    </label>
                    <br />
                    <label>
                        <input
                            type="checkbox"
                            checked={notifications.alert}
                            onChange={() => handleNotificationChange('alert')}
                        />
                        启用弹窗通知
                    </label>
                    <br />
                    <label>
                        <input
                            type="checkbox"
                            checked={notifications.sound}
                            onChange={() => handleNotificationChange('sound')}
                        />
                        启用声音通知
                    </label>
                </div>

                <button type="submit" style={{ padding: '10px 20px' }}>提交</button>
            </form>
        </div>

        
    );
};

export default StockMonitorConfigPage;