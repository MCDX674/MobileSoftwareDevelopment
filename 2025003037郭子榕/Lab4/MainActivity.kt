package com.example.myapplication4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication4.ui.theme.MyApplication4Theme
import kotlin.random.Random



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplication4Theme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF0F8FF)
                ) {
                    DiceRollerApp()
                }
            }
        }
    }
}


@Composable
fun DiceRollerApp() {
    // 保存骰子结果状态
    var diceResult by remember { mutableStateOf(6) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val imageResource = getDiceImageResource(diceResult)

        // 显示骰子图片
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Dice showing number ${getDiceDescription(diceResult)}",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))


        // 掷骰子按钮
        Button(
            onClick = {
                // 断点3
                val newValue = rollDice()
                diceResult = newValue
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Rolls", fontSize = 18.sp)
        }
    }
}


@Composable
fun getDiceImageResource(result: Int): Int {

    //断点
    return when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}

fun getDiceDescription(result: Int): String {
    return when (result) {
        1 -> "one"
        2 -> "two"
        3 -> "three"
        4 -> "four"
        5 -> "five"
        6 -> "six"
        else -> "one"
    }
}

fun rollDice(): Int {
    val randomValue = Random.nextInt(1, 7)  // 生成1-6的随机数

    return randomValue
}


@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    MyApplication4Theme {
        MyApplication4Theme {

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF0F8FF)
            ) {
                DiceRollerApp()
            }
        }
    }
}