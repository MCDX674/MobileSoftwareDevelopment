package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.ui.theme.ArtspaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtspaceTheme() {
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
    // 当前展示的作品索引：1, 2, 3
    var currentArtwork by remember { mutableIntStateOf(1) }

    // 根据索引动态获取作品信息
    val artwork = when (currentArtwork) {
        1 -> Artwork(
            imageRes = R.drawable.artwork_1,
            title = "安提比斯的松树",
            artist = "克劳德·莫奈",
            year = "1888"
        )
        2 -> Artwork(
            imageRes = R.drawable.artwork_2,
            title = "日出·印象",
            artist = "克劳德·莫奈",
            year = "1872"
        )
        else -> Artwork(
            imageRes = R.drawable.artwork_3,
            title = "吉维尼花园的鸢尾花",
            artist = "克劳德·莫奈",
            year = "1900"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 艺术作品展示区（画框效果）
        ArtworkWall(imageRes = artwork.imageRes)

        // 2. 作品信息说明区
        ArtworkDescription(
            title = artwork.title,
            artist = artwork.artist,
            year = artwork.year
        )

        // 3. 控制按钮区（上一个/下一个）
        DisplayControls(
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

// 作品数据类，统一管理信息
data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)

// 作品展示组件：带边框和阴影的画框效果
@Composable
fun ArtworkWall(imageRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .shadow(8.dp)
            .background(Color.White)
            .border(2.dp, Color.LightGray)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentScale = ContentScale.Crop
        )
    }
}

// 作品信息说明组件
@Composable
fun ArtworkDescription(title: String, artist: String, year: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$artist · $year",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

// 控制按钮组件：Previous / Next
@Composable
fun DisplayControls(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier
                .width(140.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2C6B))
        ) {
            Text("Previous", color = Color.White)
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .width(140.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2C6B))
        ) {
            Text("Next", color = Color.White)
        }
    }
}