# WeightTrack 体重记录应用

GitHub 仓库地址：https://github.com/MCDX674/2025003024-FinalProject.git

## 1. 项目简介

- 应用名称：WeightTrack（体重记录助手）
- 目标用户：需要记录和管理体重数据的健身爱好者、减重人群、健康管理者
- 核心功能：
  - 体重记录管理（新增、查看、删除）
  - 个人健康档案设置（身高、目标体重、性别、出生日期）
  - 健康数据统计（BMI计算、增重/减重提示）
  - 网络励志语录获取
  - 深色/浅色主题切换

## 2. 技术栈

- UI：Jetpack Compose + Material 3
- 数据库：Room（2张表：weight_record、user_profile）
- 网络：Retrofit / OkHttp（接口来源：https://api.vvhan.com/api/other/yiyan）
- 状态管理：ViewModel + StateFlow
- 持久化偏好：DataStore
- 导航：Navigation Compose
- 异步处理：Kotlin Coroutines
- 其他依赖：Gson（JSON解析）

## 3. 功能清单

### 必做项完成情况

**UI 层**
- [x] Jetpack Compose 构建全部 UI
- [x] 至少 2 个主要页面（首页、统计页、设置页、添加记录页）
- [x] Compose Navigation 导航
- [x] LazyColumn 列表（首页体重记录列表）
- [x] Material 3 组件和主题
- [x] 浅色 / 深色模式支持

**数据层**
- [x] Room 数据库，至少 2 张表（weight_record、user_profile）
- [x] 完整 CRUD 操作（新增、查询、删除体重记录；新增、查询用户档案）
- [x] DAO 查询方法返回 Flow 类型（getAllRecordsFlow、getLatestRecordFlow等）
- [x] 至少一种查询功能（按时间倒序查询、查询最新记录）
- [x] DataStore 保存用户偏好或最近状态（身高、目标体重、性别、出生日期、深色模式）

**网络层**
- [x] 声明并使用 Internet 权限（AndroidManifest.xml中声明）
- [x] 使用网络请求获取真实 API 数据（每日健康励志语录）
- [x] 网络数据在核心页面中展示或参与主要功能流程（统计页展示励志语录）
- [x] 处理 Loading / Success / Error 等网络状态（QuoteUiState密封类）
- [x] Composable 不直接发起网络请求（通过ViewModel调用）

**架构层**
- [x] ViewModel 状态管理（StatisticsViewModel、EditRecordViewModel、SettingsViewModel）
- [x] Repository 模式（WeightRepository、UserPreferencesRepository）
- [x] StateFlow / Flow 数据流（所有数据通过StateFlow暴露给UI）
- [x] Kotlin 协程异步处理（网络请求、数据库操作均使用协程）
- [x] UiState 描述界面状态（QuoteUiState、RecordListUiState密封类）
- [x] Composable 不直接访问数据库或网络（通过ViewModel间接访问）

**功能完整性**
- [x] 新增 / 编辑 / 删除 / 搜索等核心操作（新增、删除体重记录；新增、更新用户档案）
- [x] 输入验证和错误提示（体重输入验证、日期格式验证、未来日期验证）
- [x] 状态展示（空数据提示、加载状态、错误状态）
- [x] 屏幕旋转后状态保持（ViewModel保存状态）

### 选做项完成情况

- [x] 复杂数据库查询（按时间排序、查询最新记录、统计记录总数）
- [x] 输入验证和错误提示（体重范围验证、日期格式验证）

## 4. 数据库设计

### 表 1：weight_record（体重记录表）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，自增 |
| weight | Float | 体重值（kg） |
| date | String | 记录日期（yyyy-MM-dd格式） |

### 表 2：user_profile（用户档案表）

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | Int | 主键，固定值1（全局仅一条记录） |
| height | String | 身高（cm） |
| targetWeight | String | 目标体重（kg） |
| gender | String | 性别（男/女） |

说明：两个表通过WeightRepository统一管理，weight_record表存储所有体重记录，user_profile表存储用户个人档案信息。主要DAO查询方法包括：
- WeightRecordDao：insertRecord、deleteRecord、getAllRecordsFlow、getLatestRecordFlow、getRecordCountFlow
- UserProfileDao：insertProfile、saveProfile、updateProfile、getProfileFlow

## 5. 网络功能设计

- API 来源：韩小韩API（国内直连免费接口）
- 接口地址：https://api.vvhan.com/api/other/yiyan
- 请求方式：GET
- 主要返回字段：
  - success: Boolean（请求是否成功）
  - data: QuoteData（包含content和author）
- App 中使用这些网络数据的页面或功能：统计页面展示每日健康励志语录
- 网络失败时的处理方式：网络请求失败时，使用本地预设的励志语录库随机展示

## 6. 架构设计

本应用采用MVVM架构模式：

1. **UI Layer（UI层）**：由Jetpack Compose构建，包含HomeScreen、StatisticsScreen、SettingsScreen、EditRecordScreen等页面，通过Navigation Compose进行页面导航。

2. **ViewModel层**：包含StatisticsViewModel、EditRecordViewModel、SettingsViewModel，负责管理UI状态、处理用户输入、调用数据层操作。

3. **Repository层**：WeightRepository负责数据库操作，UserPreferencesRepository负责DataStore偏好存储，作为数据访问的单一数据源。

4. **Data Layer（数据层）**：
   - Room数据库：通过AppDatabase、WeightRecordDao、UserProfileDao实现本地数据持久化
   - DataStore：通过UserPreferencesRepository实现用户偏好设置的持久化
   - Network：通过RetrofitClient、HealthQuoteApiService、NetworkDataSource实现网络数据获取

数据流向：UI ← ViewModel ← Repository ← Database/Network

## 7. 核心功能截图

### 首页

<img src="file:///C:/Users/欧梅/Desktop/3/home.png" width="500"/>

说明：展示所有体重记录列表，用户可以长按记录进行删除，点击右下角加号添加新记录。

### 统计页

<img src="file:///C:/Users/欧梅/Desktop/3/statistics.png" width="500"/>

说明：展示用户健康数据统计信息，包括性别、年龄、身高、最新体重、目标体重、BMI指数、记录总数，以及网络获取的每日励志语录。

### 设置页

<img src="file:///C:/Users/欧梅/Desktop/3/settings.png" width="500"/>

说明：用户可以设置个人档案信息（身高、目标体重、性别、出生日期），并支持深色模式切换。

### 添加记录页

<img src="file:///C:/Users/欧梅/Desktop/3/add_record.png" width="500"/>

说明：用户可以输入体重值和日期，系统会进行输入验证（体重范围、日期格式等），保存后返回首页。

## 8. 技术难点与解决方案

### 难点 1：网络请求与本地数据的协调

- 问题描述：需要同时处理网络数据（励志语录）和本地数据库数据（体重记录），且网络请求可能失败。
- 原因分析：网络请求是异步操作，需要处理加载状态、成功状态和错误状态，同时不能阻塞UI。
- 解决方案：
  1. 使用ViewModel + StateFlow管理网络请求状态
  2. 创建QuoteUiState密封类（Loading、Success、Error）描述网络请求状态
  3. 网络失败时回退到本地预设语录库
  4. 通过NetworkDataSource封装网络请求逻辑，隔离业务层与网络层
- 参考资料：Android官方架构指南、Kotlin协程文档

### 难点 2：输入验证与错误处理

- 问题描述：体重输入需要验证数值范围，日期输入需要验证格式和有效性，需要提供友好的错误提示。
- 原因分析：用户输入可能不符合预期，需要在保存前进行多重验证，且验证逻辑需要与UI状态同步。
- 解决方案：
  1. 在EditRecordViewModel中实现完整的验证逻辑
  2. 使用mutableStateOf管理错误提示状态
  3. 验证顺序：非空检查 → 数值范围检查 → 日期格式检查 → 未来日期检查
  4. 错误提示直接显示在输入框下方，用户体验更好
- 参考资料：Android输入验证最佳实践

## 9. AI 使用说明

请在以下选项中勾选，可多选：

- [x] 网页版 AI（如 ChatGPT、Claude、Kimi、豆包、mimo等）
- [ ] AI Agent / 编程代理（如 Claude Code、Codex、OpenCode、Cursor Agent 等）
- [x] 国产大模型服务（如 DeepSeek、GLM、通义千问、文心一言等）
- [ ] IDE 插件或代码补全工具（如 GitHub Copilot、Cursor、CodeGeeX 等）
- [ ] 其他：

具体工具名称：mimo

AI 主要用于哪些环节：代码生成、调试、技术方案设计

说明：是否使用 AI 以及使用了什么 AI 工具不会影响分值，请如实填写。

## 10. 运行说明

- 最低 Android 版本：API 24（Android 7.0）
- 推荐 Android 版本：API 34（Android 14）
- 特殊权限：网络权限（INTERNET）
- 运行步骤：
  1. 克隆仓库：`git clone https://github.com/MCDX674/WeightTrack`
  2. 使用 Android Studio 打开项目
  3. 等待 Gradle 同步完成
  4. 连接模拟器或真机，点击 Run

## 11. 项目亮点（可选）

1. **完整的MVVM架构**：严格遵循MVVM架构模式，UI、ViewModel、Repository、数据层职责清晰分离。
2. **响应式数据流**：使用StateFlow和Flow实现响应式数据更新，UI自动响应数据变化。
3. **多重输入验证**：体重输入支持数值范围验证，日期输入支持格式验证和未来日期验证，提供友好的错误提示。
4. **网络与本地数据协调**：网络请求失败时自动回退到本地数据，保证应用稳定性。
5. **用户体验优化**：支持深色模式、空数据提示、删除二次确认弹窗等细节设计。

## 12. 未来改进方向（可选）

1. **添加搜索功能**：支持按日期范围、体重范围搜索历史记录。
2. **数据可视化**：添加体重变化趋势图表，帮助用户更直观地了解体重变化。
3. **数据导出**：支持将体重记录导出为CSV或Excel格式。
4. **提醒功能**：添加定时提醒，帮助用户养成记录习惯。
5. **多用户支持**：支持多个用户档案，适合家庭使用。