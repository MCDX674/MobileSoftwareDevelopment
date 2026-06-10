package com.example.a05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var currentArtwork by remember { mutableStateOf(1) }

    // 确保这里的 R 是你自己项目的 R
    val imageRes = when (currentArtwork) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtworkWall(imageRes = imageRes)
        ArtworkInfo(artworkIndex = currentArtwork)
        ControlButtons(
            onPrevious = {
                currentArtwork = if (currentArtwork == 1) 3 else currentArtwork - 1
            },
            onNext = {
                currentArtwork = if (currentArtwork == 3) 1 else currentArtwork + 1
            }
        )
    }
}

@Composable
fun ArtworkWall(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Artwork",
        modifier = Modifier
            .size(300.dp, 400.dp)
            .border(4.dp, Color.Gray, RoundedCornerShape(8.dp))
    )
}

@Composable
fun ArtworkInfo(artworkIndex: Int) {
    val (title, artist, year) = when (artworkIndex) {
        1 -> Triple("Van Gogh's The Starry Night", "Vincent van Gogh", "1889")
        2 -> Triple("A maiden among a sea of flowers", "Unknown Artist", "2025")
        else -> Triple("Munch's The Scream", "Edvard Munch", "1893")
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "$artist ($year)", fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun ControlButtons(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onPrevious) {
            Text("Previous")
        }
        Button(onClick = onNext) {
            Text("Next")
        }
    }
}