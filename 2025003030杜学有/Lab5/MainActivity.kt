package com.example.artspace

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
import androidx.compose.ui.graphics.RectangleShape
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

/**
 * 主应用界面
 */
@Composable
fun ArtSpaceApp() {
    // 状态管理：记录当前展示的作品索引
    var currentArtwork by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 区块1：艺术作品墙（图片展示）
        ArtworkWall(currentArtwork = currentArtwork)

        // 区块2：艺术作品说明（文字信息）
        ArtworkDescriptor(currentArtwork = currentArtwork)

        // 区块3：显示控制器（切换按钮）
        DisplayController(
            onPreviousClick = {
                // Previous 逻辑：第一张切换到最后一张，其余减1
                currentArtwork = when (currentArtwork) {
                    1 -> 3
                    else -> currentArtwork - 1
                }
            },
            onNextClick = {
                // Next 逻辑：最后一张切换到第一张，其余加1
                currentArtwork = when (currentArtwork) {
                    3 -> 1
                    else -> currentArtwork + 1
                }
            }
        )
    }
}

/**
 * 区块1：艺术作品墙 - 展示图片，带画框效果
 */
@Composable
fun ArtworkWall(currentArtwork: Int) {
    // 根据状态获取对应图片
    val imageResource = when (currentArtwork) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .border(5.dp, MaterialTheme.colorScheme.onBackground, RectangleShape)
            .clip(RectangleShape),
        shadowElevation = 8.dp
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = "艺术作品展示",
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * 区块2：艺术作品说明 - 展示标题、艺术家、年份
 */
@Composable
fun ArtworkDescriptor(currentArtwork: Int) {
    // 根据状态获取作品信息
    val (title, artist, year) = when (currentArtwork) {
        1 -> Triple("星月夜", "文森特·威廉·梵高", "1889年")
        2 -> Triple("维纳河畔", "巴勃罗·毕加索", "1804年")
        3 -> Triple("拈花圣母", "列奥纳多·达芬奇", "1645年")
        else -> Triple("未知作品", "未知艺术家", "未知年份")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$artist | $year",
            fontSize = 20.sp
        )
    }
}

/**
 * 区块3：显示控制器 - 上一张/下一张按钮
 */
@Composable
fun DisplayController(
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPreviousClick,
            modifier = Modifier.width(150.dp)
        ) {
            Text(text = "Previous", fontSize = 18.sp)
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier.width(150.dp)
        ) {
            Text(text = "Next", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}