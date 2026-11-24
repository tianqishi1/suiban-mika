# M1 第一刀：AdvisorTurn 顾问单次路径

## 目标与证据

M0 实测（2026-11-24，K60 + DeepSeek flash）：2 次顾问提问 = 30 个任务轮次、输入 390k token、
约 ¥0.16/次。原因：屏幕问答进了完整 AgentLoop（观察→决策→动作→校验 + IntentGate + 结局判断）。
目标：顾问场景 1 次调用、≤5k token、≤¥0.02/次、时延 2–4s。

## 设计

触发条件（满足其一）：
- 用户消息带 UserScreen（双击胶囊）且文字命中顾问关键词：怎么回/回复建议/帮我回/如何回复/怎么接话/这条怎么；
- 设置项「顾问模式」开启时，所有带屏幕的消息走单次路径。

新增 core/agent/AdvisorTurn.kt：
1. 输入：UserScreen.lines（捕获时已含无障碍树文本+OCR，无需再观察）、用户附言、前台 App 名；
2. 紧凑提示词：顾问角色 + 事实纪律（只准用屏幕原文）+ 输出 JSON {understanding, suggestions:[{text,tone}]}；
3. 单次 ChatModel 调用，不带工具定义；lines 为空且有截图且模型能看图时附图；
4. 解析 JSON 渲染为「意图一句话 + 3 条候选」；解析失败回退原文；
5. 结果走现有 RunSink/事件总线，作为已完成 run 交出——UI、时间线、用量统计零改动。

分流点：run 创建处（TaskCenterState / run 组装入口，实现时定位），在 AgentLoop 之前拦截。
执行类消息（提取、巡检、委托操作）不受影响，照旧走循环。

UI：P1 先用现有聊天行渲染格式化文本；P2 做建议卡片（候选列表+复制按钮）。

## 验证

- 单元测试：提示词构建、JSON 解析、回退路径；
- 真机 A/B：同样 5 个问题，旧路径 vs AdvisorTurn，用网关 /admin/usage 对比轮次/token/时延；
- 通过标准：1 次调用、≤5k token、建议质量不低于旧路径。

## 构建环境（已就绪）

- JDK 17：tools/jdk17/jdk-17.0.21.1+2
- Android SDK：tools/android-sdk（platforms;android-36 + build-tools;36.0.0）
- Gradle 8.11.1 wrapper（distributionUrl 换腾讯镜像加速）
- 基线：v0.1.50 源码 zip（gh-proxy 下载，关键文件哈希已对官方 API 校验），commit bd6bb44
- 安装：adb -s 7efa58be install（K60 已授权）

## 阶段

P1 AdvisorTurn + 分流（成本修复）→ P2 建议卡片 → P3 换壳/托管登录 → P4 RemoteSync/团队版
