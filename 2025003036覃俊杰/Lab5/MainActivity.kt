package com.example.hwang

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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

@Composable
fun ArtSpaceApp() {
    var currentArtwork by remember { mutableStateOf(1) }

    val imageResource = when (currentArtwork) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtworkWall(imageResource = imageResource)
        ArtworkDescriptor(artwork = currentArtwork)
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

@Composable
fun ArtworkWall(imageResource: Int) {

    Surface(
        modifier = Modifier.size(300.dp),
        border = BorderStroke(4.dp, Color.Gray),
        shadowElevation = 8.dp // 修复 Material3 阴影参数
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "艺术作品",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ArtworkDescriptor(artwork: Int) {
    val title = when (artwork) {
        1 -> "星月夜"
        2 -> "蒙娜丽莎"
        else -> "向日葵"
    }
    val artist = when (artwork) {
        1 -> "文森特·梵高"
        2 -> "列奥纳多·达·芬奇"
        else -> "文森特·梵高"
    }
    val year = when (artwork) {
        1 -> "1889年"
        2 -> "1503-1519年"
        else -> "1888年"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "艺术家：$artist", fontSize = 18.sp)
        Text(text = "年份：$year", fontSize = 16.sp)
    }
}

@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        Button(onClick = onPrevious) {
            Text("Previous")
        }
        Spacer(modifier = Modifier.width(32.dp))
        Button(onClick = onNext) {
            Text("Next")
        }
    }
}