# 运行

## 非开发人员教程

### 1.软件安装

安装Git

[https://git-scm.com/downloads](https://git-scm.com/downloads)

安装Sourcetree

[https://www.sourcetreeapp.com/](https://www.sourcetreeapp.com/)

安装Docker

[https://docker.github.net.cn/get-docker/](https://docker.github.net.cn/get-docker/)

### 2.复制代码

1.新建一个文件夹，如`ChinaStockTrader`，在该文件夹下新建文件夹`chinastockdata`

2.打开Sourcetree，选择文件->克隆/新建
源路径：https://gitee.com/njulyon/china-stock-trader.git
目标路径：步骤1新建的的文件夹地址`ChinaStockTrader`，点击克隆

3.打开Sourcetree，选择文件->克隆/新建
源路径：https://gitee.com/njulyon/china-stock-data.git
目标路径：步骤1新建的的文件夹地址`ChinaStockTrader/chinastockdata`，点击克隆

### 3.软件配置

Docker Desktop->Settings->Docker Engine

加入镜像地址`registry-mirrors`

```
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "experimental": false,
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1ms.run"
  ]
}
```


### 4.编译运行

#### 4.1 Windows

进入`ChinaStockTrader`目录，右键`在终端中打开`。然后依次输入如下命令

```Bash
docker-compose build --no-cache
docker-compose up
```

#### 4.2 mac

复制`ChinaStockTrader`目录完整的地址，如`/Users/ChinaStockTrader`

打开终端，依次输入如下命令

```Bash
cd /Users/ChinaStockTrader

docker-compose build --no-cache
docker-compose up
```

### 5.运行使用

浏览器输入
http://localhost:3000
验证是否能看到网页

#### 5.1 关机后的重复运行

打开DockerDesktop，查看Containers，查看chinastocktrader是否在运行，如果没有则点击运行按钮启动


### 6.保持更新

1.查看Sourcetree上对应的`ChinaStockTrader`和`ChinaStockData`项目，是否有拉取提示，有的话点击`拉取`按钮获取最新代码

2.打开DockerDesktop，展开chinastocktrader，删除chinastocktrader_react（网页程序）和chinastocktrader_java（服务器程序）。

**不要删除chinastocktrader_db**（数据库）

3.重复步骤4和5即可