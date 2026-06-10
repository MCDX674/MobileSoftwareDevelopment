package com.example.work7

import android.os.Bundle
import androidx.compose.ui.layout.ContentScale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
            ArtSpaceApp()
        }
    }
}

@Composable
fun ArtSpaceApp() {
    data class Artwork(
        val imageRes: Int,
        val title: String,
        val artist: String,
        val year: String
    )

    val artworks = listOf(
        Artwork(
            imageRes = R.drawable.artwork_1,
            title = "Mona Lisa",
            artist = "Leonardo Di Serpiero Da Vinci",
            year = "(1506)"
        ),
        Artwork(
            imageRes = R.drawable.artwork_2,
            title = "Still Life：Vase with Fifteen Sunflowers",
            artist = "Vincent Willem van Gogh",
            year = "(1888)"
        ),
        Artwork(
            imageRes = R.drawable.artwork_3,
            title = "Napoleon Crossing the Alps",
            artist = "Jacques-Louis David",
            year = "(1801)"
        )
    )

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentArtwork = artworks[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtworkImage(imageRes = currentArtwork.imageRes)

        ArtworkInfoCard(
            title = currentArtwork.title,
            artist = currentArtwork.artist,
            year = currentArtwork.year
        )

        ControlButtons(
            onPrevious = {
                currentIndex = if (currentIndex == 0) artworks.size - 1 else currentIndex - 1
            },
            onNext = {
                currentIndex = if (currentIndex == artworks.size - 1) 0 else currentIndex + 1
            }
        )
    }
}

@Composable
fun ArtworkImage(imageRes: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)  // 固定高度，统一大小
            .padding(16.dp),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Artwork",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentScale = ContentScale.Fit  // 保证不变形
        )
    }
}

@Composable
fun ArtworkInfoCard(title: String, artist: String, year: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color(0xFFF0F0F8)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 30.sp,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$artist $year",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ControlButtons(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier
                .width(160.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415E9B)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(text = "Previous", fontSize = 18.sp)
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .width(160.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415E9B)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(text = "Next", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewArtSpace() {
    ArtSpaceApp()
}