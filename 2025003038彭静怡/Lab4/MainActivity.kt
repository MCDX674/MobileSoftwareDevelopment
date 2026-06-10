package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerScreen()
                }
            }
        }
    }
}

@Composable
fun DiceRollerScreen() {
    // 状态提升到屏幕级，避免嵌套作用域问题
    var currentDiceNumber by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 骰子显示组件
        DiceDisplayComponent(currentDiceNumber)

        // 间距组件
        Spacer(modifier = Modifier.padding(40.dp))

        // 投掷按钮组件
        RollDiceButtonComponent {
            // 点击回调：更新状态
            currentDiceNumber = Random.nextInt(1, 7)
        }
    }
}

@Composable
fun DiceDisplayComponent(diceNumber: Int) {
    // 图片资源映射函数
    fun getDiceImageRes(number: Int): Int {
        return when (number) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1
        }
    }

    Image(
        painter = painterResource(id = getDiceImageRes(diceNumber)),
        contentDescription = "Dice showing $diceNumber",
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Text(
        text = "当前点数: $diceNumber",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun RollDiceButtonComponent(onRollClick: () -> Unit) {
    Button(
        onClick = onRollClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "Roll The Dice",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiceRollerPreview() {
    MyApplicationTheme {
        DiceRollerScreen()
    }
}