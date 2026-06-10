package com.example.artspaceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 数据类：封装艺术作品信息（便于扩展和维护）
data class Artwork(
    val imageResId: Int,
    val title: String,
    val artist: String,
    val year: String
)

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
    // 1. 准备艺术作品数据
    val artworks = listOf(
        Artwork(
            imageResId = R.drawable.artwork_1,
            title = "The Starry Night ",
            artist = "Vincent van Gogh",
            year = "1889"
        ),
        Artwork(
            imageResId = R.drawable.artwork_2,
            title = "Green Wheat Fields, Auvers",
            artist = "Vincent van Gogh",
            year = "1890"
        ),
        Artwork(
            imageResId = R.drawable.artwork_3,
            title = "Harvest in Provence",
            artist = "Vincent van Gogh",
            year = "1888"
        )
    )

    // 2. 状态管理：记住当前展示的作品索引
    var currentArtworkIndex by remember { mutableStateOf(0) }
    val currentArtwork = artworks[currentArtworkIndex]

    // 3. 整体布局：垂直排列所有组件
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // 区块1：艺术作品展示（带画框效果）
            ArtworkWall(artwork = currentArtwork)

            // 区块2：作品信息展示
            ArtworkInfoCard(artwork = currentArtwork)

            // 区块3：切换按钮
            NavigationButtons(
                currentIndex = currentArtworkIndex,
                totalCount = artworks.size,
                onPrevious = {
                    // 上一个：索引减1，到0则回到最后一个
                    currentArtworkIndex = if (currentArtworkIndex == 0) {
                        artworks.size - 1
                    } else {
                        currentArtworkIndex - 1
                    }
                },
                onNext = {
                    // 下一个：索引加1，到最后一个则回到0
                    currentArtworkIndex = if (currentArtworkIndex == artworks.size - 1) {
                        0
                    } else {
                        currentArtworkIndex + 1
                    }
                }
            )
        }
    }
}

/**
 * 艺术作品展示组件（带画框效果）
 */
@Composable
fun ArtworkWall(artwork: Artwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(2.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = artwork.imageResId),
                contentDescription = null,  // 旁边有文字说明，设为null
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

/**
 * 作品信息卡片组件
 */
@Composable
fun ArtworkInfoCard(artwork: Artwork) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 作品标题
            Text(
                text = artwork.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            // 艺术家和年份
            Text(
                text = "${artwork.artist} (${artwork.year})",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 导航按钮组件（上一个/下一个）
 */
@Composable
fun NavigationButtons(
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier
                .width(150.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
        ) {
            Text(text = "Previous", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .width(150.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
        ) {
            Text(text = "Next", fontSize = 16.sp, color = Color.White)
        }
    }
}

// 预览函数
@Preview(showBackground = true)
@Composable
fun ArtSpaceAppPreview() {
    ArtSpaceApp()
}