package com.example.artspaceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: Int
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
    val artworks = listOf(
        Artwork(R.drawable.artwork_1, "樱花风景", "艺术家", 2024),
        Artwork(R.drawable.artwork_2, "日落西山", "摄影师", 2025),
        Artwork(R.drawable.artwork_3, "城市风光", "创作者", 2022)
    )

    var current by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier
                .size(350.dp, 400.dp)
                .shadow(12.dp)
                .background(Color.White),
            color = Color.White
        ) {
            Image(
                painter = painterResource(artworks[current].imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFFF5F5F5)),
            color = Color(0xFFF5F5F5)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = artworks[current].title, fontSize = 22.sp, fontWeight = FontWeight.Medium)
                Text(text = "${artworks[current].artist} · ${artworks[current].year}", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Button(onClick = {
                current = if (current > 0) current - 1 else artworks.size - 1
            }) {
                Text("Previous")
            }
            Button(onClick = {
                current = if (current < artworks.size - 1) current + 1 else 0
            }) {
                Text("Next")
            }
        }
    }
}