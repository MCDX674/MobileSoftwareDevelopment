package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置 Compose 界面入口
        setContent {
            DiceRollerApp()
        }
    }
}

/**
 * 应用主界面（外层容器）
 */
@Composable
fun DiceRollerApp() {
    // 使用 Column 让内容垂直居中
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,   // 水平居中
        verticalArrangement = Arrangement.Center              // 垂直居中
    ) {
        DiceWithButtonAndImage()
    }
}

/**
 * 核心组件：骰子 + 按钮
 */
@Composable
fun DiceWithButtonAndImage() {

    // 状态：当前骰子点数（Compose 核心）
    var result by remember { mutableStateOf(1) }

    // 根据点数选择图片资源
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    // 显示骰子图片
    Image(
        painter = painterResource(id = imageResource),
        contentDescription = result.toString() // 用于无障碍描述
    )

    Spacer(modifier = Modifier.height(16.dp)) // 间距

    // 按钮：点击后随机生成 1~6
    Button(
        onClick = { result = (1..6).random() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0D475D)
        )
    ) {
        Text("Roll")
    }
}