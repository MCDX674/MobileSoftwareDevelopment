package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    val artworks = listOf(
        Artwork(
            imageRes = R.drawable.art3,
            title = "星月夜 (The Starry Night)",
            artistYear = "文森特·梵高 (1889)"
        ),
        Artwork(
            imageRes = R.drawable.art1,
            title = "向日葵 (Sunflowers)",
            artistYear = "文森特·梵高 (1888)"
        ),
        Artwork(
            imageRes = R.drawable.art2,
            title = "戴珍珠耳环的少女",
            artistYear = "约翰内斯·维米尔 (1665)"
        )
    )

    var currentIndex by remember { mutableStateOf(0) }
    val currentArtwork = artworks[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 作品展示区
        Image(
            painter = painterResource(id = currentArtwork.imageRes),
            contentDescription = currentArtwork.title,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        )

        // 作品信息区
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentArtwork.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentArtwork.artistYear,
                fontSize = 18.sp
            )
        }

        // 按钮区
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                currentIndex = if (currentIndex == 0) {
                    artworks.size - 1
                } else {
                    currentIndex - 1
                }
            }) {
                Text("Previous")
            }

            Button(onClick = {
                currentIndex = if (currentIndex == artworks.size - 1) {
                    0
                } else {
                    currentIndex + 1
                }
            }) {
                Text("Next")
            }
        }
    }
}

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artistYear: String
)