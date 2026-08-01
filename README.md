# ICBM Client

ICBM Client 是一个 GPL-3.0-only 的 NeoForge 1.21.1 纯客户端诊断模组。它从支持 ICBM FML 协议的服务器接收 Region 裁切与性能状态；F3 开启时显示服务器指标，并在模拟距离被 Region 边界裁切时绘制红色边界线。

没有 ICBM 服务端扩展时，客户端不会发送专有包，也不会阻止加入服务器。

## 开发

需要 JDK 21：

```bash
./gradlew test
./gradlew build
./gradlew runClient
```

服务端实现和精确 payload 定义见 [协议 v1](docs/protocol-v1.md)。
