# Lab10 实验报告
## 一、NavController、NavHost、composable() 关系
1. **NavController**：导航的核心控制器，负责管理页面跳转、返回堆栈、监听页面状态，是所有导航操作的入口。
2. **NavHost**：导航容器，作为页面的承载区域，关联NavController，管理所有路由的展示。
3. **composable()**：为NavHost配置具体的Composable页面路由，定义路由地址和对应的UI组件。
三者关系：NavController指挥跳转，NavHost提供展示容器，composable()注册具体页面。

## 二、LunchTrayScreen 枚举类设计说明
使用枚举类而非硬编码字符串的原因：
1. **类型安全**：避免路由字符串拼写错误，编译期检查。
2. **统一管理**：集中管理所有页面的路由名称和标题资源。
3. **可读性高**：代码语义清晰，便于维护和扩展页面。

## 三、LunchTrayAppBar 设计思路
1. **动态标题**：通过当前页面枚举值获取对应字符串资源，自动更新标题。
2. **返回按钮条件**：判断`navController.previousBackStackEntry != null`，Start页面无返回按钮，其余页面显示。
3. **返回逻辑**：点击返回按钮调用`navigateUp()`，返回上一个页面。

## 四、导航流程与返回堆栈管理
1. **导航流程**：Start → Entree → SideDish → Accompaniment → Checkout → Start。
2. **返回堆栈关键处理**：
   - 从Start进入Entree时，使用`popUpTo(Start) { inclusive = true }`，将Start弹出堆栈，按系统返回键直接退出应用。
   - Cancel/Submit操作：清空订单+导航回Start，同时清空所有中间页面堆栈，保证流程纯净。

## 五、实验问题与解决
1. **问题**：按返回键会回到Start页面，无法退出应用。
   解决：导航到Entree时添加`popUpTo`参数，移除Start页面。
2. **问题**：Cancel后订单数据未清空。
   解决：封装公共方法，导航同时调用`viewModel.resetOrder()`。
3. **问题**：AppBar标题不更新。
   解决：使用`currentBackStackEntryAsState()`监听路由变化，实时更新当前页面。