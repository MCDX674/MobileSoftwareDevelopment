package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

@Composable
fun ArtSpaceApp() {
    // 当前作品序号 1-3
    var currentIndex by remember { mutableIntStateOf(1) }

    // 图片资源（已适配你的命名：artwork_1 / artwork_2 / artwork_3）
    val imageRes = when (currentIndex) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    // 作品信息（动漫插图信息，可按需修改）
    val title = when (currentIndex) {
        1 -> "星空下的小镇"
        2 -> "花与少女"
        else -> "未来都市"
    }
    val artist = "匿名插画师"
    val year = "2024"

    // 整体布局：Column 垂直嵌套 Row，完全符合老师要求
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ====================== 区块1：艺术作品墙（上方大图）======================
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        )

        // ====================== 区块2：艺术作品说明（中间文字）======================
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$artist · $year",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ====================== 区块3：按钮控制器（Row横向排列）======================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                currentIndex = if (currentIndex == 1) 3 else currentIndex - 1
            }) {
                Text("Previous")
            }

            Button(onClick = {
                currentIndex = if (currentIndex == 3) 1 else currentIndex + 1
            }) {
                Text("Next")
            }
        }
    }
}