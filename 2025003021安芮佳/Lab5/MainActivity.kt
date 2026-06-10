package com.example.minghuajieshao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minghuajieshao.ui.theme.MingHuaJieShaoTheme
import androidx.compose.ui.text.style.TextAlign


// 定义数据类存储名画信息（优化硬编码，便于扩展）
data class Artwork(
    val imageResId: Int,
    val name: String,
    val artist: String,
    val year: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MingHuaJieShaoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ArtSpaceApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtSpaceApp(modifier: Modifier = Modifier) {
    // 1. 准备名画数据
    val artworks = listOf(
        Artwork(
            imageResId = R.drawable.artwork_1_png, // 需自行添加图片到drawable目录
            name = "《蒙娜丽莎》",
            artist = "列奥纳多·达·芬奇",
            year = "1503-1519"
        ),
        Artwork(
            imageResId = R.drawable.artwork_2_png,
            name = "《感恩祭》",
            artist = "威廉・阿道夫・布格罗",
            year = "1867"
        ),
        Artwork(
            imageResId = R.drawable.artwork_3_png,
            name = "《桌球》",
            artist = "路易斯 - 利奥波德・布瓦伊",
            year = "1807"
        )
    )

    // 2. 状态管理：当前显示的作品索引
    var currentIndex by remember { mutableStateOf(0) }
    val currentArtwork = artworks[currentIndex]

    // 3. 整体布局：垂直排列三大区块
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // 区块1：艺术作品墙（带画框效果）
        ArtworkWall(artwork = currentArtwork)

        // 区块2：艺术作品说明
        ArtworkDescription(artwork = currentArtwork)

        // 区块3：控制按钮（上一个/下一个）
        ControlButtons(
            currentIndex = currentIndex,
            totalCount = artworks.size,
            onPrevious = {
                // 上一个逻辑：到第一个时跳转到最后一个
                currentIndex = if (currentIndex == 0) artworks.size - 1 else currentIndex - 1
            },
            onNext = {
                // 下一个逻辑：到最后一个时跳转到第一个
                currentIndex = if (currentIndex == artworks.size - 1) 0 else currentIndex + 1
            }
        )
    }
}

/**
 * 艺术作品墙：展示名画图片，带画框效果
 */
@Composable
fun ArtworkWall(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .size(300.dp, 400.dp)
            .border(
                width = 8.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            ),
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = artwork.imageResId),
                contentDescription = null, // 旁边有文字说明，设为null
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

/**
 * 艺术作品说明：展示名称、艺术家、年份（多样式文本）
 */
@Composable
fun ArtworkDescription(artwork: Artwork) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 作品名称（加粗、大号字体）
        Text(
            text = artwork.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 艺术家+年份（混合样式）
        Text(
            text = buildAnnotatedString {
                // 统一公共样式，减少重复
                val commonSize = 18.sp

                withStyle(
                    style = SpanStyle(
                        fontSize = commonSize,
                        color = Color(0xFF757575)
                    )
                ) {
                    append("作者：")
                }

                withStyle(
                    style = SpanStyle(
                        fontSize = commonSize,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append(artwork.artist)
                }

                withStyle(
                    style = SpanStyle(
                        fontSize = commonSize,
                        color = Color(0xFF757575)
                    )
                ) {
                    append(" | 创作年份：${artwork.year}")
                }
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 控制按钮：上一个/下一个
 */
@Composable
fun ControlButtons(
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onPrevious,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
        ) {
            Text(
                text = "上一个",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
        ) {
            Text(
                text = "下一个",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtSpacePreview() {
    MingHuaJieShaoTheme {
        ArtSpaceApp()
    }
}