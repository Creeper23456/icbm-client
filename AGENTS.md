# ICBM Client 开发约定

## 项目范围

- 本仓库是 Minecraft Java Edition 1.21.1 的 NeoForge **纯客户端**诊断模组；主包名为 `cn.cubegarden.icbm.client`，模组 ID 为 `icbm_client`。
- 模组必须可选安装：没有 ICBM 服务端 FML 实现的 NeoForge 或原版服务器仍须能正常加入。不得把专有 payload 标记为必需，也不得在未协商通道时发送它。
- 仅显示服务端提供的 ICBM 状态，不实现游戏规则、服务端模拟或 Region 判定。
- 发布物使用 GPL-3.0-only；保留根目录 `LICENSE`，新增源文件使用 SPDX 标识。

## 构建与验证

- 本机 JDK 21 固定在 `/home/klp/jdk-21`（Temurin 21）；运行 Gradle 前设置 `JAVA_HOME=/home/klp/jdk-21` 并将 `$JAVA_HOME/bin` 加入 `PATH`。不要依赖 `/tmp` 中的 JDK。
- 运行 `./gradlew test` 验证单元测试，运行 `./gradlew build` 产出 jar，运行 `./gradlew runClient` 启动开发客户端。
- 不提交 `.gradle/`、`build/`、`run/`、IDE 文件、下载的 JDK 或 Gradle 缓存。Gradle wrapper 文件例外，必须提交。
- 改动协议或渲染逻辑后，至少运行 `./gradlew test`、`./gradlew build` 与 `git diff --check`。

## 网络协议

- 协议规范的唯一来源是 `docs/protocol-v1.md`。修改 payload ID、字段语义、坐标单位或兼容策略时，必须先同步更新该文档和测试。
- 所有 payload 使用 NeoForge `PayloadRegistrar.optional()`，协议版本为 `1`；客户端发送前使用 `NetworkRegistry.hasChannel` 确认服务端已协商对应通道。
- 服务端是权威数据源。速度使用服务端计算的 m/s；客户端只格式化、缓存和按过期时间隐藏数据。
- Region 坐标是方块边界：`minX/minZ` 含，`maxX/maxZ` 不含。未知字段不得从旧快照继承。

## 客户端行为

- 仅在 F3 调试界面显示 ICBM 指标；不要覆盖 ActionBar、聊天或其他 HUD。
- 仅当 F3 开启、快照未过期、维度匹配且 `simulationClipped=true` 时使用 Minecraft 原版世界边境效果渲染 Region；这只影响渲染，实际边境功能由服务器实现。
- F3 开启时订阅，关闭/断开时取消订阅。断开、维度不匹配或数据过期必须清空状态，不能显示陈旧边界。
