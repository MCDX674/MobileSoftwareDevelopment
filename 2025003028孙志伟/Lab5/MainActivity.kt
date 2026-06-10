package com.example.draw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceApp()
        }
    }
}

// 主界面
@Composable
fun ArtSpaceApp() {
    // 状态：记录当前显示的作品编号（1、2、3）
    var currentArtwork by remember { mutableStateOf(1) }

    // 根据当前编号获取对应图片
    val imageRes = when (currentArtwork) {
        1 -> R.drawable.artwork1
        2 -> R.drawable.artwork2
        else -> R.drawable.artwork3
    }

    // 根据当前编号获取作品信息
    val artworkTitle = when (currentArtwork) {
        1 -> "呐喊"
        2 -> "星月夜"
        else -> "戴珍珠耳环的少女"
    }
    val artist = when (currentArtwork) {
        1 -> "爱德华·蒙克"
        2 -> "文森特·梵高"
        else -> "约翰内斯·维米尔"
    }
    val year = when (currentArtwork) {
        1 -> "1893年"
        2 -> "1889年"
        else -> "1665年"
    }

    // 整体布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 作品展示区（带画框）
        ArtworkDisplay(imageRes = imageRes)

        // 2. 作品信息区
        ArtworkInfo(
            title = artworkTitle,
            artist = artist,
            year = year
        )

        // 3. 按钮控制区
        ControlButtons(
            onPrevious = {
               
                currentArtwork = if (currentArtwork == 1) 3 else currentArtwork - 1
            },
            onNext = {
                
                currentArtwork = if (currentArtwork == 3) 1 else currentArtwork + 1
            }
        )
    }
}

// 作品展示组件（画框效果）
@Composable
fun ArtworkDisplay(imageRes: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(
                width = 8.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 16.dp
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "艺术作品",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

// 作品信息组件
@Composable
fun ArtworkInfo(
    title: String,
    artist: String,
    year: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$artist · $year",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}

// 按钮控制组件
@Composable
fun ControlButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.width(140.dp)
        ) {
            Text("Previous")
        }

        Button(
            onClick = onNext,
            modifier = Modifier.width(140.dp)
        ) {
            Text("next")
        }
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceApp()
}