<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">mind-mqtt</h1>
<h4 align="center">基于SpringBoot-netty的轻量级mqttbroker、mqttClient</h4>
<p align="center">
	<a href="#"><img src="https://img.shields.io/badge/Springboot-2.5.3-blue"></a>
	<a href="#"><img src="https://img.shields.io/badge/license%20-MIT-green"></a>
	<a href="https://gitee.com/liangqiding/springboot-cli"><img src="https://img.shields.io/badge/%E7%A0%81%E4%BA%91-%E5%9B%BD%E5%86%85%E5%9C%B0%E5%9D%80-yellow"></a>
</p>

* [简介](#简介)
* [1\. 项目结构](#1-项目结构)
* [2\. 功能支持](#2-功能支持)
* [3\. 项目启动](#3-项目启动)
    * [3\.1 环境安装](#31-环境安装)
    * [3\.2 项目打包](#32-项目打包)
    * [3\.3 运行](#33-运行)
* [4\. QOS选择建议](#4-qos选择建议)
    * [4\.1 qos服务降级](#41-qos服务降级)

## 简介

- 基于Springboot、netty实现的高性能高可用MQTT broker，支持集群，轻松百万连接，千万级别消息量，支持MQTT3.1、MQTT3.1.1、MQTT5.0协议，支持mqtt/ws连接。

- 代码注释规整，严格按照阿里编程规范。
- 可作为脚手架进行二次业务扩展开发。

- 支持软件互联，如经典的聊天、群聊系统接入。

- IOT 物联网，如智能家电等接入。

## 1. 项目结构

> ```
>mind-mqtt
>  ├── m-common       -- 通用工具及常量
>  ├── m-mqtt-auth    -- MQTT认证及授权
>  ├── m-mqtt-broker  -- MQTT borker服务器启动类
>  ├── m-mqtt-client  -- MQTT客户端示例代码
>  ├── m-mqtt-core    -- 核心功能模块，包括消息解析，业务逻辑处理
>  ├── m-mqtt-cluster -- 集群功能模块
>  ├── m-mqtt-model   -- 实体封装 
>  ├── m-mqtt-monitor -- MQTT健康监控
>  ├── m-mqtt-store   -- MQTT服务器会话信息(redis缓存)
>```

## 2. 功能支持

| #    | 功能              | 是否支持 | 说明                                                         |
| ---- | ----------------- | -------- | ------------------------------------------------------------ |
| 1    | Broker管理界面    | 支持     | 可启动配套的前端UI管理界面，健康监控，统计分析，授权管理     |
| 2    | 集群功能          | 支持     | 连接同一个redis，自动注册节点开启集群                        |
| 3    | 心跳机制          | 支持     | 支持心跳检测                                                 |
| 4    | MQTT授权认证      | 支持     | 支持授权登录，多用户管理，权限管理（发布和订阅权限）         |
| 5    | ws连接            | 支持     | 自动识别连接协议，支持ws连接                                 |
| 6    | mqtt连接          | 支持     | 自动识别连接协议，支持mqtt连接                               |
| 7    | 遗嘱消息          | 支持     | 支持离线自动发送遗嘱                                         |
| 8    | Kafka消息转发功能 | 不支持   | 预留数据持久化接口，可自行实现                               |
| 9    | 授权管理          | 支持     | 可启动配套的前端UI管理界面，支持多用户管理，权限管理（发布和订阅权限） |
| 10   | 消息分发重试      | 支持     | 针对qos1/qos2消息做了优化，尤其qos2消息，确保在网络差的情况下也能准确不重复的送达。 |
| 11   | 黑名单            | 支持     | 禁止该用户连接                                               |
| 12   | 消息持久化        | 不支持   | 预留数据持久化接口，可自行实现                               |
| 13   | MQTT5.0           | 支持     | 已支持5.0协议                                                |
| 14   | 流量监控、限流    | 支持     | 可启动配套的前端UI管理界面查看配置，进行限流                 |
| 15   | 硬件监控          | 支持     | 可启动配套的前端UI管理界面查看                               |

## 3. 项目启动

#### 3.1 环境安装

| #    | 软件  | 版本   | 是否必须 | 说明                                                       |
| ---- | ----- | ------ | -------- | ---------------------------------------------------------- |
| 1    | redis | 6.2.6  | 是       | 有安装就行，版本不做要求                                   |
| 2    | JDK   | Jdk11+ | 是       | 必须JDK11以上，否则无法运行，下载openJDK11即可，商用免费的 |
| 3    | mysql | 8.0.27 | 否       | 版本5.7 以上就行，如不需要管理界面，可不安装mysql          |

#### 3.2 项目打包

```shell
# 项目打包
cd mind-mqtt
mvn clean package
mvn clean install
```

#### 3.3 运行

找到打包后的jar包，路径

```
mind-mqtt/m-mqtt-broker/target/m-mqtt-broker-1.0.0.jar
```

![](\docs\doc-image\package01.png)

- win

  打开cmd

  ```shell
  java -jar m-mqtt-broker-1.0.0.jar
  ```

- linux

  ```shell
  java -jar m-mqtt-broker-1.0.0.jar
  ```

- docker

  ```shell
  略
  ```

## 4. QOS选择建议

#### 4.1 qos服务降级

服务端会选择发布消息和订阅消息中较低的QoS来实现消息传输，这也被称作“服务降级”。

- QoS = 0 占用的网络资源最低，但是接收端可能会出现无法接收消息的情况，所以适用于传输重要性较低的信息。

- QoS = 1 MQTT会确保接收端能够接收到消息，但是有可能出现接收端反复接收同一消息的情况。

- QoS = 2 MQTT会确保接收端只接收到一次消息。但是QoS为2时消息传输最慢，另外消息传输需要多次确认，因此所占用的网络资源也是最多的。此类服务等级适用于重要消息传输。

由于QoS1和QoS2都能确保客户端接收到消息，但是QoS1所占用的资源较QoS2占用资源更小。