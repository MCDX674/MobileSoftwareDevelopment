package com.example.minghuajs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minghuajs.ui.theme.MingHuajsTheme

private val artwork1 = R.drawable.mhjs1
private val artwork2 = R.drawable.mhjs2
private val artwork3 = R.drawable.mhjs3

// 艺术作品数据类
data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MingHuajsTheme {
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
    var currentArtworkIndex by remember { mutableStateOf(0) }

    val artworkList = listOf(
        Artwork(
            imageRes = artwork1,
            title = "Lady in the Garden",
            artist = "Abbott Fuller Graves",
            year = "Early 20th century"
        ),
        Artwork(
            imageRes = artwork2,
            title = "Woman with a Parasol – Madame Monet and Her Son",
            artist = "Claude Monet",
            year = "1875"
        ),
        Artwork(
            imageRes = artwork3,
            title = "The Milkmaid",
            artist = "Johannes Vermeer",
            year = "1658–1660"
        )
    )

    val currentArtwork = artworkList[currentArtworkIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // 优化后的作品展示区域（长方形）
        ArtworkWall(artwork = currentArtwork)
        ArtworkDescriptor(artwork = currentArtwork)
        DisplayController(
            currentIndex = currentArtworkIndex,
            totalCount = artworkList.size,
            onPrevious = {
                currentArtworkIndex = if (currentArtworkIndex == 0) artworkList.size - 1 else currentArtworkIndex - 1
            },
            onNext = {
                currentArtworkIndex = if (currentArtworkIndex == artworkList.size - 1) 0 else currentArtworkIndex + 1
            }
        )
    }
}

/**
 * 优化后的作品展示：长方形 + 柔和边框 + 更大显示区域
 */
@Composable
fun ArtworkWall(artwork: Artwork, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            // 改为 宽360.dp × 高480.dp 的长方形（更大、更美观）
            .width(360.dp)
            .height(480.dp),
        // 柔和圆角 + 极细边框
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        shadowElevation = 10.dp
    ) {
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp) // 内边距更舒适
        )
    }
}

@Composable
fun ArtworkDescriptor(artwork: Artwork, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = artwork.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${artwork.artist} · ${artwork.year}",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DisplayController(
    currentIndex: Int,
    totalCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
        ) {
            Text(text = "Previous", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
        ) {
            Text(text = "Next", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtSpaceAppPreview() {
    MingHuajsTheme {
        ArtSpaceApp()
    }
}