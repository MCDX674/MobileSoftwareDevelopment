package com.example.artapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 艺术作品数据类：存储单幅作品的信息（包含年份）
data class Artwork(
    val imageResId: Int,       // 图片资源ID
    val title: String,         // 作品标题
    val artist: String,        // 艺术家
    val year: String           // 创作年份
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // 效果图背景为纯白色
                ) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

// 主应用组合函数
@Composable
fun ArtSpaceApp() {
    // 1. 定义状态：记录当前显示的作品索引（初始为第1幅）
    var currentArtworkIndex by remember { mutableIntStateOf(0) }

    // 2. 准备3幅艺术作品数据
    val artworkList = listOf(
        Artwork(
            imageResId = R.drawable.flower, // 第一张：花
            title = "Still Life of Blue Rose and Other Flowers",
            artist = "Owen Scott",
            year = "2021"
        ),
        Artwork(
            imageResId = R.drawable.scenry_jpg, // 第二张：风景
            title = "Misty River Valley",
            artist = "Mountain Dreamer",
            year = "2022"
        ),
        Artwork(
            imageResId = R.drawable.city, // 第三张：城市
            title = "Urban Twilight Reflection",
            artist = "City Lens",
            year = "2024"
        )
    )

    // 3. 主布局：垂直排列三个区块，调整间距和对齐方式
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp), // 整体内边距
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 区块1：艺术作品墙
        ArtworkWall(artwork = artworkList[currentArtworkIndex])

        // 区块2：作品信息描述
        ArtworkDescriptor(artwork = artworkList[currentArtworkIndex])

        // 区块3：上一页/下一页按钮控制器
        DisplayController(
            onPrevious = {
                // 切换逻辑：索引递减，到第1幅（索引0）时跳转到最后一幅
                currentArtworkIndex = if (currentArtworkIndex == 0) {
                    artworkList.size - 1
                } else {
                    currentArtworkIndex - 1
                }
            },
            onNext = {
                // 切换逻辑：索引递增，到最后一幅时跳转到第1幅
                currentArtworkIndex = if (currentArtworkIndex == artworkList.size - 1) {
                    0
                } else {
                    currentArtworkIndex + 1
                }
            }
        )
    }
}

// 区块1：艺术作品墙
@Composable
fun ArtworkWall(artwork: Artwork) {
    // 外层卡片容器（带阴影效果）
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f) // 宽度占屏幕90%
            .aspectRatio(0.8f) // 宽高比和效果图接近
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(4.dp)
            ),
        color = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        // 图片容器
        Image(
            painter = painterResource(id = artwork.imageResId),
            contentDescription = artwork.title,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 图片与卡片的内边距
            contentScale = ContentScale.Fit // 保持图片比例，完整显示
        )
    }
}

// 区块2：作品信息描述
@Composable
fun ArtworkDescriptor(artwork: Artwork) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF0F0F8), // 浅紫色背景
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start // 文字左对齐
    ) {
        // 作品标题
        Text(
            text = artwork.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // 艺术家+年份
        Text(
            text = "${artwork.artist} (${artwork.year})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// 区块3：按钮控制器
@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 上一页按钮
        Button(
            onClick = onPrevious,
            modifier = Modifier
                .weight(1f) // 两个按钮平分宽度
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A6FA5), // 深蓝色按钮，匹配效果图
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp) // 完全圆角
        ) {
            Text(text = "Previous", fontSize = 18.sp)
        }

        // 下一页按钮
        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(1f) // 两个按钮平分宽度
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A6FA5),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(text = "Next", fontSize = 18.sp)
        }
    }
}