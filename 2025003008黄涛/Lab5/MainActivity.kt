package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)

@Composable
fun ArtSpaceApp() {
    var currentArtworkIndex by remember { mutableStateOf(0) }


    val artworkList = listOf(
        Artwork(
            imageRes = R.drawable.artwork_1,
            title = "Still Life of Blue Rose and Other Flowers",
            artist = "Owen Scott",
            year = "2021"
        ),
        Artwork(
            imageRes = R.drawable.artwork_2,
            title = "Sunset Landscape",
            artist = "Emma Davis",
            year = "2022"
        ),
        Artwork(
            imageRes = R.drawable.artwork_3,
            title = "Abstract Geometry",
            artist = "Liam Wilson",
            year = "2023"
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        ArtworkWall(artwork = artworkList[currentArtworkIndex])


        ArtworkDescriptor(artwork = artworkList[currentArtworkIndex])


        DisplayController(
            onPrevious = {

                currentArtworkIndex = when (currentArtworkIndex) {
                    0 -> artworkList.size - 1
                    else -> currentArtworkIndex - 1
                }
            },
            onNext = {
                currentArtworkIndex = when (currentArtworkIndex) {
                    artworkList.size - 1 -> 0
                    else -> currentArtworkIndex + 1
                }
            }
        )
    }
}

/**
 * 艺术作品墙组件
 * 包含带边框的图片展示，模拟画框效果
 */
@Composable
fun ArtworkWall(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .size(width = 280.dp, height = 380.dp)
            .border(width = 8.dp, color = Color(0xFF8B4513), shape = RoundedCornerShape(4.dp)),
        shadowElevation = 8.dp
    ) {
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = "Artwork: ${artwork.title}",
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * 艺术作品说明组件
 * 展示作品名称、艺术家和年份
 */
@Composable
fun ArtworkDescriptor(artwork: Artwork) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = artwork.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${artwork.artist} (${artwork.year})",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 显示控制器组件
 * 包含上一个和下一个按钮，水平排列
 */
@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "Previous", fontSize = 16.sp)
        }
        Button(
            onClick = onNext,
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "Next", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, name = "Art Space Preview")
@Composable
fun ArtSpacePreview() {
    MaterialTheme {
        ArtSpaceApp()
    }
}