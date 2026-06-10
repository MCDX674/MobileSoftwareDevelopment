package com.example.myapplication

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
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
    var currentArtwork by remember { mutableStateOf(1) }

    val imageResource = when (currentArtwork) {
        1 -> R.drawable.lin_yu_dui
        2 -> R.drawable.wu_t_wu
        else -> R.drawable.gojo_blue_curse
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtworkWall(imageResource = imageResource)

        ArtworkDescriptor(artworkIndex = currentArtwork)

        DisplayController(
            onPrevious = {
                currentArtwork = when (currentArtwork) {
                    1 -> 3
                    else -> currentArtwork - 1
                }
            },
            onNext = {
                currentArtwork = when (currentArtwork) {
                    3 -> 1
                    else -> currentArtwork + 1
                }
            }
        )
    }
}

// 作品展示组件
@Composable
fun ArtworkWall(imageResource: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(400.dp)
            .border(
                width = 8.dp,
                color = Color(0xFF6C757D), // 深灰色边框，更显高级
                shape = RoundedCornerShape(12.dp)
            ),
        shadowElevation = 24.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "五条悟相关作品",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop // 按比例裁剪，填满框体
        )
    }
}

// 作品信息组件
@Composable
fun ArtworkDescriptor(artworkIndex: Int) {
    val artworkInfo = when (artworkIndex) {
        1 -> Triple(
            "领域对轰",
            "五条悟 vs 宿傩",
            "无量空处vs伏魔御厨子"
        )
        2 -> Triple(
            "新宿决战",
            "五条悟",
            "咒术回战 第234话"
        )
        else -> Triple(
            "超频状态",
            "五条悟",
            "黑闪"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = artworkInfo.first,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212529)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = artworkInfo.second,
            fontSize = 20.sp,
            color = Color(0xFF495057)
        )
        Text(
            text = artworkInfo.third,
            fontSize = 16.sp,
            color = Color(0xFF6C757D)
        )
    }
}

// 控制按钮组件
@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp) // 按钮间距
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.width(160.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("上一幅作品", fontSize = 18.sp)
        }
        Button(
            onClick = onNext,
            modifier = Modifier.width(160.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("下一幅作品", fontSize = 18.sp)
        }
    }
}