## 介绍

- 本软件mqtt-client主要作为连接示例和测试使用

- 提供http接口方便测试调用

## 1. 运行

#### 1.1 项目打包

```shell
# 项目打包
cd mind-mqtt
mvn clean package
mvn clean install
```

#### 1.2 运行

- 找到打包后的jar包，路径：

```
mind-mqtt/m-mqtt-client/target/m-mqtt-client-1.0.0.jar
```

- 版本号根据实际情况修改

```
# linux或win下直接命令行或终端运行
java -jar m-mqtt-client-1.0.0.jar
```

## 2. Api接口

#### 2.1 登录Mqtt-Broker

```json
Request URL:  http://localhost:9999/login
Request Method: POST
Request Headers:
{
    "Content-Type":"application/json"
}
Request Body:
{
    "username": "admin",
    "password": "123",
    "clientId": "00001111",
    "isWillFlag": "true",
    "willTopic": "test/q",
    "willPayload": "离线了...",
    "willQos": 0,
    "retain": false
}
```



#### 2.2 消息发布

```json
Request URL:  http://localhost:9999/send
Request Method: POST
Request Headers:
{
    "Content-Type":"application/json"
}
Request Body:
{
    "isRetain": false,
    "topic": "test/q",
    "qos": 2,
    "messageId": 1013,
    "message": {
        "name": "hhhhhhhh"
    }
}
```

#### 2.3 订阅主题

```json
Request URL:  http://localhost:9999/sub
Request Method: Get
Request Headers:
{}
Request params:
{
    "qos": 0,
    "topic": "test/#",
}
```

#### 2.4 取消订阅

```json
Request URL:  http://localhost:9999/unSub
Request Method: Get
Request Headers:
{}
Request params:
{
    "topic": "test/#",
}
```

