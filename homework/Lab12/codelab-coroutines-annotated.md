# Kotlin 协程入门 

> 本版本**严格按照官方 Codelab 的原始结构、步骤编号和措辞**呈现全部内容。官方原文在每一节中以正常格式展示，而我的解说以 **▸ 解说** 标注插入，帮助你"边读边理解"。
>
> 来源：[Android Developers — Kotlin 园地中的协程简介](https://developer.android.com/codelabs/basic-android-kotlin-compose-coroutines-kotlin-playground?hl=zh-cn)

---

## 1. 前言

此 Codelab 介绍**并发**，这是 Android 开发者为提供出色的用户体验而需要掌握的一项重要技能。并发涉及在应用中同时执行多项任务。例如，应用可以一边响应用户输入事件并相应更新界面，一边从网络服务器获取数据或将用户数据保存在设备上。

如需在应用中并发执行工作，需要使用 Kotlin **协程**(coroutines)。使用协程可以挂起某个代码块的执行并于稍后恢复，以便在此期间完成其他工作。借助协程，可以更轻松地编写**异步**代码——无需等到彻底完成一个任务之后再开始下一个任务，多个任务可以并发运行。

> **▸ 解说：这段话定义了三个核心概念的关系：**
> - **并发 (Concurrency)**：应用"同时"处理多件事的能力（注意：不一定是字面意义上的"同时"，后面会详细讲）
> - **异步 (Asynchronous)**：一种编程方式——发起一个操作后不干等它完成，而是继续做其他事
> - **协程 (Coroutine)**：Kotlin 实现异步编程的具体工具
>
> 打个比方：同步代码像在柜台排队——必须等前面的人办完才能轮到你。异步代码像拿号——取了号可以先去干别的，轮到你时再回来。协程就是帮你管理"取号、等待、回来"这套流程的工具。

### 前提条件

- 能够使用 `main()` 函数创建基本的 Kotlin 程序
- 了解 Kotlin 语言的基础知识，包括函数和 lambda

> **▸ 解说：这个 Codelab 不需要 Android Studio！只需要浏览器打开 [Kotlin 园地](https://play.kotlinlang.org/) 就能跟着写代码。这是学习协程基础概念的最佳方式——不会被 Android 的生命周期、Context、ViewModel 等复杂概念干扰，纯粹聚焦协程本身。**

### 学习内容

- Kotlin 协程如何简化异步编程
- 结构化并发的用途及其重要性

### 构建内容

用于学习和实验协程基础知识的简短 Kotlin 程序

### 所需条件

能够通过互联网访问 Kotlin 园地

---

## 2. 同步代码

### 简单的程序

在**同步**代码中，一次只能执行一个概念性任务，可以将其视为一条依序线性路径。必须彻底完成一项任务后才能开始下一项任务。

打开 Kotlin 园地，将代码替换为：

```kotlin
val startTime = System.currentTimeMillis()

fun main() {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}
```

输出：

```
Weather forecast
Sunny
```

`println()` 是一个同步调用，因为要完成输出文本的任务后才能执行下一行代码。由于 `main()` 中的每个函数调用都是同步调用，因此整个 `main()` 函数是同步函数。

> **▸ 解说：同步代码的执行顺序和代码书写顺序完全一致。这是你最熟悉的编程方式——一行执行完，才执行下一行。下面我们要在这个简单的天气程序中逐步引入"等待"和"并发"的概念。**

### 添加延迟

假设获取晴天天气预报需要向远程网络服务器发出网络请求，需要在代码中添加延迟来模拟网络请求。

首先在代码顶部导入协程库：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}
```

`delay()` 是 Kotlin 协程库提供的特殊**挂起函数**。`main()` 函数的执行将在此时挂起（暂停），然后在指定延迟时长结束后恢复。

> **▸ 解说：`delay(1000)` 模拟网络请求耗时 1000 毫秒。注意这里的 `delay()` 和 `Thread.sleep()` 有本质区别：**
> - `Thread.sleep(1000)` — 阻塞当前线程整整一秒钟，线程什么都不能做
> - `delay(1000)` — 挂起协程一秒钟，线程可以去干别的事
>
> 这个区别是理解协程价值的关键，后面会反复体现。

如果此时运行程序会遇到编译错误：`Suspend function 'delay' should be called only from a coroutine or another suspend function`。

> **▸ 解说：这个报错信息非常重要，它揭示了协程的一个核心规则：挂起函数只能在协程或其他挂起函数中调用。`main()` 是普通函数，不是协程也不是挂起函数，所以不能直接调用 `delay()`。要解决这个问题，需要 `runBlocking()`。**

为了学习 Kotlin 园地中的协程，可以调用 `runBlocking()` 函数来封装现有代码。`runBlocking()` 用于运行一个事件循环，该事件循环可在每项任务准备好恢复时从中断处继续执行任务，因此可以同时处理多项任务。

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        delay(1000)
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
    }
}
```

`runBlocking()` 是同步函数；在其 lambda 块中的所有工作完成之前，它不会返回。它会等待 `delay()` 调用中的工作完成，然后继续执行。协程 (coroutine) 中的"co-"是指"协同"。在代码挂起等待时，代码协同工作以共享底层事件循环。在协程处于挂起状态的时间内，可以完成其他工作。

> **▸ 解说：`runBlocking` 这个名字本身就说明了它的行为——"运行并阻塞"。它会阻塞当前线程，直到内部所有协程完成。**
>
> **一个重要提醒**：一般而言，只有出于学习目的才会在 `main()` 函数或单元测试中使用 `runBlocking()`。在真正的 Android 应用代码中，不需要也不应该使用 `runBlocking()`，因为 Android 框架已经为 Activity 和 ViewModel 提供了协程作用域（`lifecycleScope` 和 `viewModelScope`）。如果在主线程上调用 `runBlocking()`，会阻塞 UI 导致 ANR。

### 挂起函数

如果用于执行网络请求的逻辑变得更加复杂，可以将该逻辑提取到其自己的函数中。

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        printForecast()
    }
}

fun printForecast() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}
```

这会导致编译错误。挂起函数只能从协程或其他挂起函数中调用。通过添加 `suspend` 修饰符来修复：

```kotlin
suspend fun printForecast() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}
```

**挂起函数**与常规函数类似，只不过它可以挂起并于稍后恢复。挂起函数只能从提供此功能的其他挂起函数中调用。挂起函数可包含零个或多个**挂起点**——函数内可挂起函数执行的位置。

> **▸ 解说：`suspend` 是 Kotlin 协程体系中最关键的关键字。它的作用相当于给函数贴上了一个标签："我这个函数内部可能会挂起，请在协程或其他挂起函数中调用我"。**
>
> 一个常见的误解是以为 `suspend` 函数一定会在后台线程执行——实际上 `suspend` 和线程无关，它只是标记了"可挂起"。挂起函数默认还是在调用它的线程上执行，除非显式切换调度程序（后面会讲）。

添加另一个挂起函数：

```kotlin
suspend fun printTemperature() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 30\u00b0C")
}
```

从 `main()` 中调用它：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        printForecast()
        printTemperature()
    }
}

suspend fun printForecast() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}

suspend fun printTemperature() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 30\u00b0C")
}
```

输出：

```
Weather forecast
Sunny
30°C
```

在此代码中，`printForecast()` 函数中的延迟挂起协程，然后在一秒后恢复。`printForecast()` 返回后，调用 `printTemperature()` 函数，协程再次挂起一秒后恢复。所有工作在 `runBlocking()` 返回之前完成，程序结束。

> **▸ 解说：注意执行顺序——虽然两个函数都用了 `delay(1000)`，但它们是**顺序执行**的，总耗时约 2 秒。这是因为代码中没有使用 `launch()` 来创建新协程，所以一切都在默认的依序方式下运行。这体现了结构化并发的第一个原则：默认依序执行。**

（可选）使用 `measureTimeMillis()` 测量执行时间：

```kotlin
import kotlin.system.*
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    val time = measureTimeMillis {
        runBlocking {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
            printForecast()
            printTemperature()
        }
    }
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Execution time: ${time / 1000.0} seconds")
}

suspend fun printForecast() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}

suspend fun printTemperature() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 30\u00b0C")
}
```

输出执行时间约为 2.1 秒，这是合理的，因为每个挂起函数都有一秒延迟。代码默认依序执行。

> **▸ 解说：`measureTimeMillis` 是一个实用的调试工具。从现在开始，每次改动代码后都可以用它来验证——如果改成并发执行，时间应该从约 2 秒降到约 1 秒。**

---

## 3. 异步代码

### launch()

使用协程库中的 `launch()` 函数可以启动一个新协程。如需并发执行任务，可以添加多个 `launch()` 函数，让多个协程同时执行。

Kotlin 中的协程遵循名为**结构化并发**的关键概念：除非明确要求并发执行（例如使用 `launch()`），否则代码默认依序执行并与底层事件循环协同工作。此原则假定，调用一个函数时，无论该函数在实现细节中使用了多少协程，都应在彻底完成其工作后再返回。

> **▸ 解说：这是本 Codelab 最重要的概念。结构化并发的核心思想用一句话概括就是：**
>
> **"你不说并发，我就不并发。"**
>
> 换句话说，并发必须显式声明（通过 `launch()` 或 `async()`），默认行为永远是顺序执行。这样做的好处是：调用方不需要关心被调用函数内部是否用了协程——反正函数返回时就代表工作完成了。这跟传统的 callback 或 `CompletableFuture` 风格完全不同。

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        launch {
            printForecast()
        }
        launch {
            printTemperature()
        }
    }
}

suspend fun printForecast() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Sunny")
}

suspend fun printTemperature() {
    delay(1000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 30\u00b0C")
}
```

现在 `printForecast()` 和 `printTemperature()` 可以并发运行，因为它们在各自的协程中。对 `launch { printForecast() }` 的调用不等所有工作完成即可返回。

> **▸ 解说：两个 `launch` 各创建一个新协程，分别执行预报和温度获取。关键行为：**
> - `launch { printForecast() }` 这行代码本身**立即返回**，不会等 `printForecast()` 执行完
> - 两个协程**并发运行**，各自 delay 1 秒，总耗时约 1 秒（而不是顺序的 2 秒）
> - `runBlocking` 会等待它作用域内的**所有**协程完成后才返回

（可选）使用 `measureTimeMillis()` 检查执行时间，结果约为 1.1 秒，添加并发操作后程序更快。

> **▸ 解说：从 2 秒降到 1 秒——这就是并发的威力。两个完全独立的网络请求同时发出，不需要互相等待。**

如果在 `runBlocking()` 末尾再添加一个 print 语句：

```kotlin
val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        launch {
            printForecast()
        }
        launch {
            printTemperature()
        }
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}
```

输出：

```
Weather forecast
Have a good day!
Sunny
30°C
```

这表现了 `launch()` 具有"触发后不理"(fire-and-forget)的性质。启动协程后，无需担心其工作何时完成，继续执行下一个指令即可。当所有协程完成后，`runBlocking()` 返回，程序结束。

> **▸ 解说：输出顺序非常关键！"Have a good day!" 出现在 "Sunny" 和 "30°C" **之前**。这说明：**
> - `launch` 两句之后，代码立即继续执行 `println("Have a good day!")`
> - 两个协程在后台各自 `delay(1000)`，一秒后才输出各自的结果
> - `runBlocking` 在最后等待所有协程完成才退出
>
> `launch` 适用于"不关心返回值，只要做完就行"的场景。但如果你需要协程的返回值呢？这时候就该用 `async()` 了。

### async()

如果关心协程何时完成并需要从中返回的值，请使用 `async()` 函数。`async()` 函数会返回一个类型为 `Deferred` 的对象，就像承诺结果准备就绪后就会出现在其中。可以使用 `await()` 访问 `Deferred` 对象上的结果。

> **▸ 解说：`Deferred` 类似于 JavaScript 的 `Promise` 或 Java 的 `Future`。它代表一个"尚未就绪的值"。调用 `await()` 时：**
> - 如果值已经算好了→ 立即返回
> - 如果还没算好→ 挂起当前协程，等值就绪后再恢复
>
> `async` = 开始异步计算 + 返回未来值；`await` = 等待并取出那个值。

首先，更改挂起函数以返回 `String`：

```kotlin
suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(1000)
    return "30\u00b0C"
}
```

修改 `runBlocking()` 代码，使用 `async()` 而不是 `launch()`：

```kotlin
import kotlinx.coroutines.*

fun main() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")

        val forecast: Deferred<String> = async {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getForecast 开始")
            val r = getForecast()
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getForecast 结束")
            r
        }
        val temperature: Deferred<String> = async {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getTemperature 开始")
            val r = getTemperature()
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getTemperature 结束")
            r
        }

        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 开始 await")
        val f = forecast.await()
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] forecast await 完成: $f")
        val t = temperature.await()
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] temperature await 完成: $t")

        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] $f $t")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(1000)
    return "30\u00b0C"
}
```

输出：

```
Weather forecast
Sunny 30°C
Have a good day!
```

两个协程并发运行来获取天气预报和温度数据。每个协程完成时返回一个值，然后将两个返回值合并输出。

> **▸ 解说：现在执行顺序对了：先输出 "Weather forecast"，两个 `async` 并发执行，各自的 `await()` 挂起等待结果，等都拿到了再拼成字符串输出，最后 "Have a good day!"。**
>
> **`launch` vs `async` 的选择标准：**
> - 需要返回值 → `async`
> - 不需要返回值，只是执行一个副作用（如写日志、更新数据库） → `launch`

### 并行分解

并行分解是将问题分解成可以并行解决的更小的子任务。子任务结果准备好后，将其合并为最终结果。

将天气预报逻辑提取到单个 `getWeatherReport()` 函数中，使用 `coroutineScope{}`：

```kotlin
import kotlinx.coroutines.*

fun main() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")

        // 先调用 getWeatherReport 得到结果，再打印，确保时间戳正确
        val report = getWeatherReport(startTime)
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] $report")

        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getWeatherReport(startTime: Long) = coroutineScope {
    val forecast = async {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getForecast 开始")
        val r = getForecast()
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getForecast 结束")
        r
    }
    val temperature = async {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getTemperature 开始")
        val r = getTemperature()
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] getTemperature 结束")
        r
    }

    // 等待并返回结果
    "${forecast.await()} ${temperature.await()}"
}

suspend fun getForecast(): String {
    delay(1000) // 模拟耗时操作
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(1000) // 模拟耗时操作
    return "30\u00b0C"
}
```

`coroutineScope{}` 用于创建局部作用域。在此作用域内启动的协程会归入此作用域内。`coroutineScope()` 仅在其所有工作（包括其启动的所有协程）完成后才会返回。使用 `coroutineScope()` 时，即使函数内部是并发完成工作的，调用方也会将其视为同步操作。

> **▸ 解说：这是结构化并发最优雅的体现。看 `main()` 中的调用：**
> ```kotlin
> println(getWeatherReport())
> ```
> 从调用方的角度来看，`getWeatherReport()` 就是一个普通的挂起函数——调用它，等它返回，拿到结果。调用方**完全不知道**内部用了两个并发的 `async`。
>
> 并发只是一个**实现细节**。这就是结构化并发的目标：封装异步，对外暴露同步语义。

这里体现了结构化并发的要点：可以将多个并发操作放入单个同步操作中，并发只是一个实现细节。对发起调用的代码只有一个要求——它必须在挂起函数或协程中。发起调用的代码的结构不需要考虑并发细节。

> **▸ 解说：`coroutineScope` vs `runBlocking`：**
> - `runBlocking` — 阻塞线程直到完成（用于 `main` 和测试，不在 Android UI 线程用）
> - `coroutineScope` — 挂起但不阻塞线程，完成后恢复（用于普通协程代码）
>
> 在 Android 开发中，99% 的情况你应该用 `coroutineScope` 而不是 `runBlocking`。

---

## 4. 异常和取消

### 异常简介

异常是指代码执行期间发生的意外事件。以下示例程序因发生异常而提前终止：

```kotlin
val startTime = System.currentTimeMillis()

fun main() {
    val numberOfPeople = 0
    val numberOfPizzas = 20
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Slices per person: ${numberOfPizzas / numberOfPeople}")
}
```

运行时程序因除以零而崩溃：

```
Exception in thread "main" java.lang.ArithmeticException: / by zero
```

> **▸ 解说：在进入协程异常处理之前，先用一个最简单的非协程例子回顾异常是什么。除以零是最直观的例子——程序无法处理这种情况时，直接崩溃。**

### 协程异常

从天气程序入手，在其中一个挂起函数内故意抛出一个异常：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${getWeatherReport()}")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getWeatherReport() = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }
    "${forecast.await()} ${temperature.await()}"
}

suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(500)
    throw AssertionError("Temperature is invalid")
    return "30\u00b0C"
}
```

运行程序后输出：

```
Weather forecast
Exception in thread "main" java.lang.AssertionError: Temperature is invalid
```

> **▸ 解说：注意——不仅温度获取失败了，连天气预报也没有输出。"Sunny" 去哪了？这就是结构化并发的异常传播机制在起作用。**

协程之间存在**父子关系**。执行 `getTemperature()` 和执行 `getForecast()` 的协程是同一父协程的子协程。结构化并发造成如下行为：当某个子协程因发生异常而失败时，异常会向上传播。系统会取消父协程，继而取消所有其他子协程（例如用于运行 `getForecast()` 的协程）。最后，错误向上传播，程序崩溃。

> **▸ 解说：这个行为可以类比为"一人犯错，全班受罚"：**
> 1. `getTemperature()` 抛出 `AssertionError`
> 2. 异常向上传播到父协程（`coroutineScope`）
> 3. 父协程被取消
> 4. 父协程取消会导致所有子协程被取消——包括正在运行的 `getForecast()`
> 5. 异常继续向上传播到 `runBlocking`，最终程序崩溃
>
> 这就是"结构化"的含义——所有协程都在一棵树里，任何一个节点出问题（未处理的异常），整条路径都会被清理。这防止了协程泄漏和资源浪费，但也意味着你**必须**妥善处理异常。

### try-catch 异常

如果知道代码的特定部分可能会抛出异常，可以使用 try-catch 块将相应代码括起来：

```kotlin
try {
    // 可能抛出异常的代码
} catch (e: IllegalArgumentException) {
    // 处理异常
}
```

> **▸ 解说：协程中的 try-catch 和普通 Kotlin 的 try-catch 语法完全一样。关键是**放在哪里**——这决定了能捕获到哪个级别的异常。**

修改天气程序以捕获异常：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        try {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${getWeatherReport()}")
        } catch (e: AssertionError) {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Caught exception in runBlocking(): $e")
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Report unavailable at this time")
        }
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getWeatherReport() = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }
    "${forecast.await()} ${temperature.await()}"
}

suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(500)
    throw AssertionError("Temperature is invalid")
    return "30\u00b0C"
}
```

输出：

```
Weather forecast
Caught exception in runBlocking(): java.lang.AssertionError: Temperature is invalid
Report unavailable at this time
Have a good day!
```

现在系统妥善处理了错误，程序能成功完成执行。

> **▸ 解说：在 `runBlocking` 层捕获异常后，程序没有崩溃，优雅地输出了错误信息。但这种方式的缺点是——整个天气报告都不可用了（包括本该可以正常获取的 "Sunny"）。在实际应用中，你可能希望"部分成功"——温度获取失败了？没关系，至少把天气预报给我。**

还可以将错误处理移到 `async()` 启动的协程内部，这样即使未提取到温度信息，仍可输出预报信息：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${getWeatherReport()}")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getWeatherReport() = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async {
        try {
            getTemperature()
        } catch (e: AssertionError) {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Caught exception $e")
            "{ No temperature found }"
        }
    }

    "${forecast.await()} ${temperature.await()}"
}

suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(500)
    throw AssertionError("Temperature is invalid")
    return "30\u00b0C"
}
```

输出：

```
Weather forecast
Caught exception java.lang.AssertionError: Temperature is invalid
Sunny { No temperature found }
Have a good day!
```

> **▸ 解说：这就是**局部异常处理**——把 try-catch 放在 `async` 块内部而不是外层。结果：**
> - `getForecast()` 正常返回 "Sunny"
> - `getTemperature()` 抛出异常，被内部的 try-catch 捕获，返回 fallback 值 `"{ No temperature found }"`
> - 父协程不会感知到异常（因为子协程已经自己处理了）
>
> 这个模式在实际开发中非常有用：比如从网络获取数据失败时，可以返回缓存数据或显示"加载失败"占位符。

这里 `async()` 是**提供方**，`await()` 是**使用方**。当提供方存在异常时，如果提供方能够捕获并处理异常，使用方不会看到异常，并会收到有效的结果。

> **▸ 解说："提供方-使用方"的比喻非常直观：**
> - **提供方 (Producer)** = `async { ... }` 内部的代码，负责计算结果
> - **使用方 (Consumer)** = `await()` 的调用方，消费计算结果
> - 提供方自己处理了异常 → 使用方拿到的就是一个正常的 fallback 值
> - 提供方没处理异常 → 异常传播到使用方的 `await()` 调用处

> **重要警告**：在协程代码中的 try-catch 语句中，请**避免捕获常规 `Exception`**。可能会无意中捕获并抑制某个 bug。另外，协程的取消依赖于 `CancellationException`，如果捕获任何类型的 `Exception` 而不重新抛出，取消行为便可能与预期不同。
>
> **▸ 解说：这是一个非常容易犯的错误。正确的做法是：只捕获你**知道怎么处理**的特定异常类型（如 `IOException`、`HttpException`），而不是大而化之地 `catch (e: Exception)`。特别是 `CancellationException`，它用于协程的正常取消流程，不应该被 suppress。**

### 取消

"协程的取消"与异常类似。当某个事件导致应用取消其先前启动的工作时，这种场景通常是由用户驱动的。例如用户不想再看到温度值，只想看天气预报信息。

从初始代码入手：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Weather forecast")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${getWeatherReport()}")
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Have a good day!")
    }
}

suspend fun getWeatherReport() = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }
    "${forecast.await()} ${temperature.await()}"
}

suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(1000)
    return "30\u00b0C"
}
```

延迟一段时间后，取消用于获取温度信息的协程：

```kotlin
suspend fun getWeatherReport() = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }

    delay(200)
    temperature.cancel()

    "${forecast.await()}"
}
```

输出：

```
Weather forecast
Sunny
Have a good day!
```

协程可以取消，但不会影响同一作用域内的其他协程，并且父协程不会被取消。

> **▸ 解说：取消和异常的关键区别：**
>
> | | 异常 | 取消 |
> |---|---|------|
> | **传播方向** | 向上（子 → 父） | 向下（父 → 子） |
> | **触发方式** | 意外发生 | 主动调用 `cancel()` |
> | **影响范围** | 默认取消所有兄弟协程 | 只取消被取消的协程 |
>
> 取消一个协程时：
> - 该协程被终止
> - 它的子协程也被取消（向下传播）
> - 它的**父协程不受影响**
> - 它的**兄弟协程不受影响**
>
> 而异常发生时：
> - 父协程被取消
> - 父协程的所有其他子协程也被取消
> - 异常继续向上冒泡

取消操作必须协同工作，因此应实现协程，这样才能将其取消。

> **▸ 解说：协程的取消是**协作式**的。`cancel()` 只是发了一个信号，协程代码必须主动检查这个信号才能真正停止。挂起函数（如 `delay()`）在被调用时自动检查取消状态——如果协程已被取消，`delay()` 会抛出 `CancellationException` 来终止协程。但如果协程在做一个纯 CPU 计算（没有挂起点），它就需要手动检查 `isActive` 才知道自己被取消了。**

---

## 5. 协程概念

这一节详细介绍了协程背后的正式概念，帮助理解所有重要部分如何协同工作。

> **▸ 解说：前面四节是"会用"，第五节是"理解原理"。这一节涉及的概念——Job、CoroutineScope、CoroutineContext、Dispatcher——是面试和实际开发中经常遇到的知识点。理解了这层关系图，协程就不再是黑魔法。**

### 作业 (Job)

使用 `launch()` 函数启动协程时，它会返回一个 `Job` 实例。作业包含协程的句柄（即对协程的引用），可以管理其生命周期。

```kotlin
val job = launch { ... }
```

> **▸ 解说：`Job` 是协程的"遥控器"。通过它你可以：**
> - 取消协程：`job.cancel()`
> - 等待完成：`job.join()`
> - 查询状态：`job.isActive` / `job.isCancelled` / `job.isCompleted`

> **注意**：从以 `async()` 函数开头的协程返回的 `Deferred` 对象也是 `Job`，并且包含相应协程未来的结果。

此作业可用于控制协程的生命周期，例如在不再需要相应任务时取消协程：

```kotlin
job.cancel()
```

对于作业，可以检查它是处于活跃状态、已取消还是已完成。如果协程及其启动的所有协程均已完成所有工作，那么该作业就已经完成。协程可能会因被取消或失败并引发异常而完成，但届时作业仍会被视为已完成。作业还会跟踪协程之间的父子关系。

> **▸ 解说：一个 Job 的生命周期状态转换：**
>
> ```
> New → Active → Completing → Completed
>           ↓
>        Cancelling → Cancelled
> ```
>
> - `isActive` = true → 协程正在运行
> - `isCompleted` = true → 协程已经结束（可能是因为正常完成、被取消、或异常终止）
> - `isCancelled` = true → 协程是被取消的
>
> 注意：`isCompleted` 为 true 不一定是正常结束——抛异常或被取消也算 Completed。

### 作业层次结构

当一个协程启动另一个协程时，从新协程返回的作业称为原始父作业的子级。

```kotlin
val job = launch {
    ...
    val childJob = launch { ... }
    ...
}
```

这些父级与子级关系形成了作业层次结构，其中的每个作业都可以启动其他作业，依此类推。这种关系很重要，因为它规定了子级和父级的行为：

- **如果某个父作业被取消，那么其子作业也会被取消。**
- **使用 `job.cancel()` 取消某个子作业时，该子作业会终止，但其父级不会被取消。**
- **如果某个作业失败并引发异常，其父级中具有该异常的作业也会被取消。这称为错误向上传播（到父级、父级的父级，依此类推）。**

> **▸ 解说：这三条规则是协程结构化并发的核心契约，总结起来就是：**
>
> | 事件 | 传播方向 | 影响 |
> |------|----------|------|
> | 父被取消 | ↓ 向下 | 所有子都被取消 |
> | 子被取消 | — 不传播 | 只影响该子 |
> | 子抛异常 | ↑ 向上 | 父被取消，所有其他子也被取消 |
>
> 这就是为什么前面异常示例中 `getForecast()` 也被取消了——因为它的兄弟 `getTemperature()` 抛出了异常，导致父协程取消，进而取消了所有子协程。

### CoroutineScope

协程通常启动到 `CoroutineScope` 中。这确保没有不受管理而不知所踪的协程，不会浪费资源。

`launch()` 和 `async()` 是 `CoroutineScope` 的扩展函数。对该作用域调用 `launch()` 或 `async()`，以在该作用域内创建新协程。

`CoroutineScope` 与生命周期相关联，对该作用域内的协程存留时长设置了界限：
- 如果某个作用域被取消，那么其作业也会被取消，而且取消会传播到其子作业。
- 如果作用域内的某个子作业失败并引发异常，那么其他子作业会被取消，父作业也会被取消，而且会向调用方重新抛出异常。

> **▸ 解说：`CoroutineScope` 可以理解为"协程的容器"——协程只能从这个容器里启动，容器的生命周期决定了里面所有协程的生命周期。没有作用域的协程是"野协程"，不允许存在。**

#### Kotlin 园地中的 CoroutineScope

此 Codelab 中使用了 `runBlocking()`，它为程序提供了 `CoroutineScope`。还使用了 `coroutineScope { }` 在 `getWeatherReport()` 函数中创建新的作用域。

#### Android 应用中的 CoroutineScope

在具有定义明确的生命周期的实体中，Android 支持协程作用域：
- `Activity` 中使用 `lifecycleScope`
- `ViewModel` 中使用 `viewModelScope`

在这些作用域内启动的协程将遵循相应实体的生命周期。如果 Activity 被销毁，`lifecycleScope` 会被取消，其所有子协程也会被自动取消。

> **▸ 解说：这是协程在 Android 中最实用的特性。当你写：**
> ```kotlin
> viewModelScope.launch {
>     val data = repository.fetchData()
>     _uiState.value = data
> }
> ```
> 你不需要手动取消这个协程——当 ViewModel 被清除时，`viewModelScope` 自动取消，协程随之停止。这就是"结构化并发"在实际项目中的直接体现，避免了协程泄漏和内存泄漏。

#### CoroutineScope 的实现细节

`CoroutineScope` 被声明为一个接口并包含一个 `CoroutineContext` 作为变量。`launch()` 和 `async()` 函数会在该作用域内创建一个新的子协程，此子级还会从该作用域继承上下文。

### CoroutineContext

`CoroutineContext` 提供将在其中运行协程的上下文的相关信息。本质上是一个用于存储元素的映射，每个元素都有一个唯一的键。上下文中可能包含的字段：

| 字段 | 说明 | 默认值 |
|------|------|--------|
| name | 协程名称，用于唯一标识 | "coroutine" |
| job | 控制协程的生命周期 | 无默认值 |
| dispatcher | 将工作分派到适当的线程 | `Dispatchers.Default` |
| exception handler | 处理协程中代码所抛出的异常 | 无默认值 |

上下文中的每个元素可以通过 `+` 运算符加到一起：

```kotlin
Job() + Dispatchers.Main + exceptionHandler
```

> **▸ 解说：`CoroutineContext` 是协程的"身份证 + 工作证"，包含了运行协程所需要的全部环境信息。名字、生命周期管理器、线程、异常处理器，全都存在 Context 里。**
>
> `+` 运算符重载是 Kotlin 协程的设计亮点——你可以像搭积木一样组合不同的 Context 元素。如果加了重复的元素，后者覆盖前者。

如果在协程中启动一个新协程，子协程将从父协程继承 `CoroutineContext`，但只会为刚创建的协程替换作业。也可以替换从父上下文继承的任何元素，只需向 `launch()` 或 `async()` 函数传入参数：

```kotlin
scope.launch(Dispatchers.Default) {
    ...
}
```

> **▸ 解说：子协程继承父协程的 Context，但 Job 永远是新创建的（每个协程必须有自己的 Job）。看这个例子：**
> ```kotlin
> scope.launch(Dispatchers.Main) {          // 在主线程
>     launch(Dispatchers.Default) {         // 子协程继承父的 name 等，但用 Default 替换 Main
>         // 在 Default 线程池执行
>     }
> }
> ```

### 调度程序 (Dispatcher)

协程使用调度程序来确定用于执行协程的**线程**。线程可以启动、执行一些工作（执行一些代码），然后在没有更多工作要完成时终止。

当用户启动应用时，Android 系统会为应用创建一个新进程和一个**主线程**。主线程负责处理重要的操作：Android 系统事件、屏幕界面绘制、用户输入事件等。大多数代码都在主线程上运行。

关于代码的线程行为有两个术语：

- **阻塞**：常规函数会阻塞发起调用的线程，直到其工作完成。在此期间无法执行任何其他工作。
- **非阻塞**：非阻塞代码会让出发起调用的线程，直到满足特定条件为止，因此在此期间可以执行其他工作。异步函数执行非阻塞工作。

对于 Android 应用，主线程（界面线程）上应只执行快速工作，目的是保持主线程未阻塞状态。长时间运行的工作项应移出主线程，在**工作器线程**中处理。

> **▸ 解说：Android 开发的一条黄金法则：**
> - **主线程（UI 线程）= 神圣不可阻塞**
> - 网络请求、数据库读写、文件操作、图片处理 → 移出主线程
> - UI 更新 → 必须在主线程
>
> 协程的调度程序就是帮你管理"在哪里执行"的工具。

Kotlin 提供的内置调度程序：

| 调度程序 | 用途 |
|----------|------|
| **Dispatchers.Main** | 在 Android 主线程上运行协程。用于界面更新和互动以及快速工作。 |
| **Dispatchers.IO** | 经过专门优化，适合在主线程之外执行磁盘或网络 I/O。如读取/写入文件、执行网络操作。 |
| **Dispatchers.Default** | 默认调度程序（未指定时使用）。在主线程之外执行计算密集型工作。如处理位图图片文件。 |

> **▸ 解说：三个调度器的使用决策树：**
> ```
> 你的工作是：
> ├── UI 更新？→ Dispatchers.Main
> ├── 网络/磁盘 I/O？→ Dispatchers.IO
> └── 计算密集型（排序、解析 JSON 等）？→ Dispatchers.Default
> ```
>
> 注意：Room（数据库）和 Retrofit（网络）已经内部切换到了合适的调度器。如果你调用 `@Dao` 的 `suspend` 函数，它自动在主线程安全，你不需要手动写 `withContext(Dispatchers.IO)`。

调度程序示例：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        launch {
            delay(1000)
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 10 results found.")
        }
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Loading...")
    }
}
```

使用 `withContext()` 切换调度程序：

```kotlin
val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        launch {
            withContext(Dispatchers.Default) {
                delay(1000)
                println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 10 results found.")
            }
        }
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Loading...")
    }
}
```

> **▸ 解说：`withContext` 是最常用的切换调度程序的方法。它的行为是：**
> 1. 挂起当前协程
> 2. 在指定的调度程序上执行代码块
> 3. 代码块完成后，恢复到原来的上下文继续执行
>
> 所以 `withContext` 是一个"往返"操作——你从 Main 进去，执行完又回到 Main。典型用法：
> ```kotlin
> // 在 Main 线程
> val result = withContext(Dispatchers.IO) {
>     // 在 IO 线程读文件
>     file.readText()
> }
> // 回到 Main 线程，更新 UI
> textView.text = result
> ```

添加 print 语句查看线程信息：

```kotlin
import kotlinx.coroutines.*

val startTime = System.currentTimeMillis()

fun main() {
    runBlocking {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - runBlocking function")
        launch {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - launch function")
            withContext(Dispatchers.Default) {
                println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - withContext function")
                delay(1000)
                println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 10 results found.")
            }
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - end of launch function")
        }
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] Loading...")
    }
}
```

输出：

```
main @coroutine#1 - runBlocking function
Loading...
main @coroutine#2 - launch function
DefaultDispatcher-worker-1 @coroutine#2 - withContext function
10 results found.
main @coroutine#2 - end of launch function
```

大部分代码在主线程上的协程中执行，但 `withContext(Dispatchers.Default)` 块中的代码在默认调度程序工作器线程上执行。在 `withContext()` 返回后，协程返回到主线程上运行。

> **▸ 解说：注意输出中 `@coroutine#2` 的踪迹：它在 `main` 线程启动 → 在 `DefaultDispatcher-worker-1` 执行 `withContext` 块 → 执行完后又回到 `main` 线程。这就是协程调度程序切换的核心行为。**
>
> 另外注意 `coroutine#1`（runBlocking 创建的）和 `coroutine#2`（launch 创建的）是不同的协程。协程编号帮助你区分不同的协程实例。

> **注意**：使用 Room 和 Retrofit 等热门库时，如果库代码已对使用其他协程调度程序（如 `Dispatchers.IO`）做出相应处理，就不必自己明确切换调度程序。这些库显示的 `suspend` 函数可能已经具有**主线程安全性**。
>
> **▸ 解说：主线程安全性 (main-safety) 是 Android 协程的最佳实践目标。一个 `suspend` 函数具有主线程安全性意味着——从主线程调用它也是安全的，不会阻塞 UI。Room DAO 的 `suspend` 函数和 Retrofit 的 `suspend` 函数都已经是主线程安全的。**

---

## 6. 总结

### 核心要点回顾

通过此 Codelab 学习了以下内容：

协程因为可以**挂起协程的执行**，腾出底层线程来执行其他工作，稍后再恢复协程，所以非常实用。这使得可以在代码中运行并发操作。

Kotlin 中的协程代码遵循**结构化并发**的原则：
- **默认依序执行**——如果想实现并发，需要明确指定（如使用 `launch()` 或 `async()`）
- 可以将多个并发操作放入单个同步操作中，并发是一个实现细节
- 对发起调用的代码只有一个要求——它必须在挂起函数或协程中
- 结构化并发使异步代码更易于阅读和推断

结构化并发会跟踪应用中每个已启动的协程，确保其不会不知所踪。协程可以具有层次结构——任务可以启动子任务，而子任务也可以继续启动子任务。

> **▸ 解说：用一句话总结结构化并发的价值：你的协程永远不会"跑丢"。每个协程都有明确的归属（作用域）、明确的生命周期边界、以及明确的异常处理路径。这和传统的手动线程管理（经常导致线程泄漏）形成了鲜明的对比。**

### 四个常见操作的原则

| 操作 | 原则 |
|------|------|
| **启动 (Launch)** | 将协程启动到一个限定了协程存留时长的作用域中。 |
| **完成 (Complete)** | 只有在子作业完成后，作业才会完成。 |
| **取消 (Cancel)** | 需要向下传播。如果取消某个协程，那么也需要取消子协程。 |
| **失败 (Fail)** | 应向上传播。如果协程抛出异常，父级会取消其所有子级，取消本身，并将异常向上传播到其父级，直到捕获失败并进行处理为止。 |

> **▸ 解说：这四个原则构成了一张"协程行为地图"：**
> ```
>      异常向上传播 ↑
>                    │
>   父 ──Cancel──→ 子 ──Cancel──→ 孙
>                    │
>       取消向下传播 ↓
> ```
> - 启动：必须在作用域内
> - 完成：等所有孩子完成才算完成
> - 取消：向下传播（父取消 → 所有子取消）
> - 失败：向上传播（子异常 → 父取消 → 父的所有子取消）

### 知识点摘要

- 借助协程，无需学习新的编程方式即可编写以并发方式长时间运行的代码。协程采用依序执行的设计。
- 协程遵循结构化并发的原则，有助于确保工作不会不知所踪，并将工作与限定了工作存留时长的作用域相关联。
- `suspend` 修饰符用于标记函数，表示可以挂起并于稍后恢复该函数的执行。
- `suspend` 函数只能从其他挂起函数或协程中调用。
- 使用 `CoroutineScope` 的 `launch()` 或 `async()` 扩展函数启动新协程。
- `Job` 可管理协程的生命周期并维护父级与子级关系，在确保结构化并发方面发挥重要作用。
- `CoroutineScope` 通过其 `Job` 来控制协程的生命周期，并以递归方式对其子级执行取消和其他规则。
- `CoroutineContext` 定义协程的行为，并可包含对作业和协程调度程序的引用。
- 协程使用 `CoroutineDispatcher` 来确定用于其执行的线程（`Main`、`Default`、`IO`）。

### 了解更多内容

- [Android 上的 Kotlin 协程](https://developer.android.com/kotlin/coroutines)
- [有关 Kotlin 协程和数据流的其他资源](https://developer.android.com/kotlin/coroutines/additional-resources)
- [协程指南](https://kotlinlang.org/docs/coroutines-guide.html)
- [协程上下文和调度程序](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
- [协程中的取消和异常](https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21)
- [Android 上的协程](https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb)
- [Kotlin 协程 101 (YouTube)](https://www.youtube.com/watch?v=ZTDXo0-SKuU)
- [KotlinConf 2019：协程：取消和异常处理全解 (YouTube)](https://www.youtube.com/watch?v=w0kfnydnFWI)

> **▸ 解说：恭喜！你已经完成了协程入门的学习。这里总结一下后续的学习路径：**
>
> 1. **马上可以做的**：在 Android Studio 中创建新项目，把 Codelab 中的代码模式用 `viewModelScope.launch` 的方式在真实应用中试一遍
> 2. **下一步该学的**：Kotlin Flow（协程的响应式流扩展），它和协程一起构成了 Android 现代异步编程的核心
> 3. **面试常考**：Job 层次结构、异常传播方向（向上/向下）、`launch` vs `async` 的区别、调度器的三种类型
>
> **最重要的提醒**：不要在生产代码的 UI 线程中使用 `runBlocking()`。在 Activity 中用 `lifecycleScope`，在 ViewModel 中用 `viewModelScope`。
