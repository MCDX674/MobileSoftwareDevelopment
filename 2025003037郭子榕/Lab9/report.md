# Lab9 实验报告 — Dessert Clicker 使用 ViewModel 重构

## 一、实验目的

1. 理解将应用逻辑从 UI 层分离的重要性。
2. 掌握使用 `ViewModel` 管理应用状态和业务逻辑的方法。
3. 学会在 Jetpack Compose 中使用 `viewModel()` 获取 ViewModel 实例。
4. 使用 `DessertUiState` 数据类集中管理 UI 所需状态，提高代码可维护性和可测试性。

------

## 二、实验原理与知识点

1. **ViewModel 在 Android 架构中的作用**
    - ViewModel 用于管理界面数据和业务逻辑，生命周期与 Activity 或 Fragment 绑定。
    - 当界面重建（如旋转屏幕）时，ViewModel 保持状态不丢失。
    - UI 层仅负责展示和事件触发，逻辑和状态集中管理在 ViewModel 中。
2. **Compose 状态管理**
    - 使用 `mutableStateOf` 包装 ViewModel 中的状态，Compose 会自动观察并触发重组。
    - `private set` 确保状态只能通过 ViewModel 方法修改，增强封装性。
    - 使用 `copy()` 更新数据类，保证每次状态更新生成新对象，触发 Compose 重组。

------

## 三、UI 状态数据类设计

文件：`DessertUiState.kt`

| 字段                    | 类型 | 默认值               | 含义                   |
| ----------------------- | ---- | -------------------- | ---------------------- |
| `revenue`               | Int  | 0                    | 当前总收入             |
| `dessertsSold`          | Int  | 0                    | 已售甜品数量           |
| `currentDessertIndex`   | Int  | 0                    | 当前甜品在列表中的索引 |
| `currentDessertImageId` | Int  | `R.drawable.cupcake` | 当前甜品图片资源 ID    |
| `currentDessertPrice`   | Int  | 5                    | 当前甜品单价           |

> 设计说明：数据类集中保存所有 UI 所需状态，便于 ViewModel 管理和 Compose 自动重组。

------

## 四、DessertViewModel 设计思路

文件：`DessertViewModel.kt`

1. **状态管理**
    - 使用 `var uiState by mutableStateOf(DessertUiState())`，UI 层通过 `viewModel.uiState` 获取当前状态。
    - `private set` 保证状态只能通过 ViewModel 内部方法修改。
2. **方法设计**
    - `onDessertClicked()`：处理甜品点击事件，更新收入、销量，并根据销量切换甜品。
    - `determineDessertToShow(dessertsSold: Int)`：根据已售数量计算当前应展示的甜品。
3. **逻辑封装**
    - 原本在 MainActivity 中的点击逻辑和 `determineDessertToShow()` 函数移入 ViewModel，UI 仅展示和触发事件。

------

## 五、MainActivity 重构分析

1. **重构前**
    - 所有状态和逻辑直接内联在 `DessertClickerApp()` 中。
    - 使用 `rememberSaveable` 保存状态，代码冗长且耦合严重。
    - `determineDessertToShow()` 和分享逻辑与 UI 混杂。
2. **重构后**
    - 状态变量删除，使用 `DessertViewModel.uiState` 替代。
    - 点击事件回调改为 `viewModel.onDessertClicked()`。
    - UI 只负责显示数据和触发事件，逻辑完全移入 ViewModel。
    - 分享功能仍在 UI 层，但使用 `uiState` 中的数据。

> 优点：界面更简洁、逻辑集中、可维护性和可测试性大幅提升。

------

## 六、重构前后代码结构对比

```
重构前：
MainActivity.kt
 ├─ UI状态
 ├─ 点击逻辑
 ├─ determineDessertToShow()
 └─ 分享功能

重构后：
MainActivity.kt           # UI展示和事件触发
DessertViewModel.kt       # 状态和业务逻辑
ui/DessertUiState.kt      # UI状态数据类
model/Dessert.kt          # 甜品数据类
data/Datasource.kt        # 甜品列表
```

> 总结：状态和逻辑与 UI 层解耦，增强了代码可维护性和可测试性。

------

## 七、运行与验证

1. 点击甜品图片，收入和销量正确更新。
2. 已售数量达到阈值后甜品自动升级。
3. 分享功能正常工作，收入和销量信息正确显示。
4. 旋转屏幕后状态不丢失。
5. 浅色/深色模式下 UI 显示正常。

截图文件：

- `screenshot_clicking.png`
- `screenshot_after.png`

------

## 八、实验总结与体会

1. ViewModel 使 UI 与逻辑彻底分离，重构后代码清晰。
2. `DessertUiState` 数据类集中管理 UI 状态，减少 UI 代码复杂度。
3. 使用 `mutableStateOf` 结合 Compose 可以轻松实现状态观察和界面重组。
4. 遇到的问题与解决：
    - **问题**：Preview 报错 `viewModel()` 无法提供状态
        **解决**：为 Preview 提供 Mock ViewModel 或临时传入状态对象
    - **问题**：Gradle 同步后 `viewModel()` 依赖未导入
        **解决**：添加 `lifecycle-viewmodel-compose:2.8.7` 依赖

> 总结：实验加深了对 Compose + ViewModel 架构模式的理解，为开发大型应用打下基础。