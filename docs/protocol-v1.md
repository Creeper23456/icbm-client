# ICBM Client FML 协议 v1

本协议服务于 Minecraft Java Edition 1.21.1 / NeoForge，网络版本字符串为 `1`。两个 payload 都必须使用 NeoForge `PayloadRegistrar.optional()` 注册。客户端模组和服务端实现均为可选：任何一侧未注册这些通道都不能造成登录失败。

ICBM 服务端不是 FML/NeoForge 服务端；它使用原版 `minecraft:register` 通道完成这两个可选 payload 的能力声明。客户端可在配置阶段声明 `icbm_client:state_snapshot`，服务端须把该能力绑定到随后建立的玩家会话；进入 Play 后，服务端声明 `icbm_client:state_subscription`。客户端仍必须以 `NetworkRegistry.hasChannel` 判断 C2S 通道；服务端必须同时收到有效订阅和 `state_snapshot` 接收能力声明后才可发送快照。普通客户端会安全忽略这些标准注册消息，且绝不会收到 ICBM 专有快照。

客户端在发送前必须通过 `NetworkRegistry.hasChannel` 确认服务端接受 `icbm_client:state_subscription`；服务端向玩家发送前也必须确认客户端接受 `icbm_client:state_snapshot`。

## `icbm_client:state_subscription`（C2S）

| 顺序 | 类型 | 字段 | 说明 |
| --- | --- | --- | --- |
| 1 | VarInt | `protocolVersion` | 固定为 `1`。不支持时服务端忽略该请求。 |
| 2 | Boolean | `enabled` | `true` 建立/刷新订阅；`false` 立即取消。 |

客户端仅在 F3 调试界面可见时启用订阅；F3 关闭、退出世界或断开时取消。服务端应把订阅与连接和玩家绑定，不得把客户端安装视为必需条件。

## `icbm_client:state_snapshot`（S2C）

| 顺序 | 类型 | 字段 | 说明 |
| --- | --- | --- | --- |
| 1 | VarInt | `protocolVersion` | 固定为 `1`。客户端忽略其它版本。 |
| 2 | VarLong | `sequence` | 服务端单调递增快照序号；客户端丢弃较旧序号。 |
| 3 | ResourceLocation | `dimension` | 快照所属维度，例如 `minecraft:overworld`。 |
| 4 | Boolean | `simulationClipped` | 当前玩家模拟范围是否已被 Region 实际边界裁切。 |
| 5 | Boolean + 数据 | `region` | 有值时依次写 `minX`、`minZ`、`maxX`、`maxZ` 四个 VarInt。Region 是严格的正方形；坐标是块边界，最小值含、最大值不含。 |
| 6 | Boolean + String | `regionId` | 可选 Region ID，UTF-8，最大 256 字节。 |
| 7 | Boolean + String | `processId` | 可选微服务进程 ID，UTF-8，最大 256 字节；Y>400 的 Core 虚拟会话固定为 Init 主进程 ID `"1"`。普通单进程 live-world bridge 必须留空，不得虚构 RegionService 进程。 |
| 8 | Boolean + Double | `speedMetersPerSecond` | 可选、由服务器计算的玩家速度（m/s）。 |
| 9 | Boolean + Float | `serverTps` | 可选服务器 TPS。 |
| 10 | VarInt | `refreshIntervalTicks` | 服务端预期刷新周期，建议 `20`；客户端将 20–1200 以外数值夹紧，并在两个周期未收到更新后过期。 |

服务端在订阅成功且确认客户端可接收快照后立刻发送一份快照，并随后约每秒推送；Region/裁切状态变化应在下一服务器 tick 推送。每份快照是完整状态，不出现的可选字段代表未知，客户端不得继承旧值。

客户端仅使用当前维度、未过期、且序号不倒退的快照。只有 `simulationClipped=true` 且 Region 存在时可在 F3 界面开启期间使用 Minecraft 原版世界边境效果渲染 Region。该临时边境只提供渲染数据，不修改客户端 Level 的实际世界边境、碰撞或移动判断；实际边境功能由服务端实现。UI 显示 `regionId`，若它缺席则显示 `processId`；速度仅作两位小数格式化。

未来不兼容的字段编码变化必须使用新的 payload 网络版本和/或新的 payload ID；允许的可选字段扩展应追加在末尾并同步升级版本。
