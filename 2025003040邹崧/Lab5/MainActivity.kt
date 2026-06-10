package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
                ArtSpaceApp()
            }
        }
    }
}

@Composable
fun ArtSpaceApp() {
    // 用状态保存当前展示的作品序号，默认第1个
    var current by remember { mutableStateOf(1) }

    // 根据序号切换图片、标题、作者年份
    val imageRes = when (current) {
        1 -> R.drawable.art1
        2 -> R.drawable.art2
        else -> R.drawable.art3
    }

    val title = when (current) {
        1 -> "星夜"
        2 -> "向日葵"
        else -> "蒙娜丽莎"
    }

    val artistYear = when (current) {
        1 -> "梵高 · 1889"
        2 -> "梵高 · 1888"
        else -> "达·芬奇 · 1503"
    }

    // 整体垂直布局
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 作品展示区（带阴影的画框）
        Surface(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth()
                .weight(1f),
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // 2. 作品信息区
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(artistYear, fontSize = 16.sp)
        }

        // 3. 按钮控制区
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 上一个按钮
            Button(onClick = {
                current = if (current == 1) 3 else current - 1
            }) {
                Text("Previous")
            }

            // 下一个按钮
            Button(onClick = {
                current = if (current == 3) 1 else current + 1
            }) {
                Text("Next")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}