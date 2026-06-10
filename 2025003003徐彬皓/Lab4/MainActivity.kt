package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 修复：使用项目默认主题 MyApplicationTheme
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerApp()
                }
            }
        }
    }
}

// 主界面组件
@Composable
fun DiceRollerApp() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DiceWithButtonAndImage()
    }
}

// 骰子图片 + 按钮组合
@Composable
fun DiceWithButtonAndImage() {
    // 修复：使用 mutableIntStateOf 替代 mutableStateOf，消除警告
    var result by remember { mutableIntStateOf(1) }

    // 根据点数切换对应图片
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
        contentDescription = "当前骰子点数：$result",
        modifier = Modifier.padding(bottom = 32.dp)
    )

    // 掷骰子按钮
    Button(onClick = {
        // 点击生成 1~6 随机数，更新状态
        result = (1..6).random()
    }) {
        Text(text = "Roll")
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun DiceRollerPreview() {
    MyApplicationTheme {
        DiceRollerApp()
    }
}