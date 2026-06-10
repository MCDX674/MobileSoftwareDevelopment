package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

/**
 * 任务二：定义导航枚举类，包含所有页面和对应标题
 */
enum class LunchTrayScreen(@StringRes val title: Int) {
    Start(R.string.app_name),
    Entree(R.string.choose_entree),
    SideDish(R.string.choose_side_dish),
    Accompaniment(R.string.choose_accompaniment),
    Checkout(R.string.order_checkout)
}

/**
 * 任务四：自定义顶部应用栏，支持动态标题和条件化返回按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            // 仅当可以返回时显示返回按钮
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

/**
 * 应用主组件：整合导航、AppBar、页面路由
 */
@Composable
fun LunchTrayApp(
    viewModel: OrderViewModel = viewModel()
) {
    // 任务三：初始化导航控制器
    val navController = rememberNavController()
    // 获取当前返回堆栈条目
    val backStackEntry by navController.currentBackStackEntryAsState()
    // 获取当前屏幕枚举值
    val currentScreen = LunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreen.Start.name
    )

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // 收集ViewModel状态
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // 任务五：配置导航宿主与所有页面路由
        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. 开始点餐页面
            composable(route = LunchTrayScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        // 导航到主菜页，并将Start页面弹出返回栈（按返回直接退出应用）
                        navController.navigate(LunchTrayScreen.Entree.name) {
                            popUpTo(LunchTrayScreen.Start.name) { inclusive = true }
                        }
                    }
                )
            }

            // 2. 主菜选择页面
            composable(route = LunchTrayScreen.Entree.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        // 取消：重置订单 + 返回Start并清空堆栈
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.SideDish.name)
                    },
                    onSelectionChanged = { viewModel.updateEntree(it) }
                )
            }

            // 3. 配菜选择页面
            composable(route = LunchTrayScreen.SideDish.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.Accompaniment.name)
                    },
                    onSelectionChanged = { viewModel.updateSideDish(it) }
                )
            }

            // 4. 佐餐选择页面
            composable(route = LunchTrayScreen.Accompaniment.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.Checkout.name)
                    },
                    onSelectionChanged = { viewModel.updateAccompaniment(it) }
                )
            }

            // 5. 结账页面
            composable(route = LunchTrayScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    },
                    onNextButtonClicked = {
                        // 提交订单：重置订单 + 返回Start
                        cancelOrderAndNavigateToStart(navController, viewModel)
                    }
                )
            }
        }
    }
}

/**
 * 封装公共逻辑：取消订单 + 导航回Start页面 + 清空返回堆栈
 */
private fun cancelOrderAndNavigateToStart(
    navController: androidx.navigation.NavController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.navigate(LunchTrayScreen.Start.name) {
        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
    }
}