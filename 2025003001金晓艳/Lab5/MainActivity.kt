package com.example.zuoye5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 导入自己项目的 R 类
import com.example.zuoye5.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceApp()
        }
    }
}

@Composable
fun ArtSpaceApp() {
    // 状态：当前显示的作品编号（1、2、3）
    var currentArtwork by remember { mutableStateOf(1) }

    // 根据状态分别获取数据，避免解构类型问题
    val imageRes = when (currentArtwork) {
        1 -> R.drawable.art1
        2 -> R.drawable.art2
        else -> R.drawable.art3
    }

    val title = when (currentArtwork) {
        1 -> "Still Life of Blue Rose and Other Flowers"
        2 -> "向日葵"
        else -> "星月夜"
    }

    val artist = when (currentArtwork) {
        1 -> "Owen Scott"
        2 -> "文森特·梵高"
        else -> "文森特·梵高"
    }

    val year = when (currentArtwork) {
        1 -> "2021"
        2 -> "1888"
        else -> "1889"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 带阴影的画框效果（和图一完全一致）
        ArtworkDisplay(imageRes = imageRes)

        // 2. 作品信息卡片
        ArtworkInfoCard(
            title = title,
            artist = artist,
            year = year
        )

        // 3. 蓝色按钮控制区
        ControlButtons(
            onPreviousClick = {
                currentArtwork = when (currentArtwork) {
                    1 -> 3
                    else -> currentArtwork - 1
                }
            },
            onNextClick = {
                currentArtwork = when (currentArtwork) {
                    3 -> 1
                    else -> currentArtwork + 1
                }
            }
        )
    }
}

// 画框组件：带白色背景 + 阴影，和示例效果一致
@Composable
fun ArtworkDisplay(imageRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(450.dp)
            .shadow(8.dp, RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "艺术作品",
            modifier = Modifier.fillMaxSize()
        )
    }
}

// 作品信息卡片：浅紫色背景，和示例排版一致
@Composable
fun ArtworkInfoCard(title: String, artist: String, year: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(Color(0xFFF0F0F8))
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$artist ($year)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// 按钮组件：深蓝色背景 + 白色文字，和示例一致
@Composable
fun ControlButtons(onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPreviousClick,
            modifier = Modifier.width(160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A67A5),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Previous", fontSize = 18.sp)
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier.width(160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A67A5),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Next", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceApp()
}