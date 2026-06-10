package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val artworks = listOf(
            Artwork(
                imageRes = R.drawable.artwork_1, // 第一张图
                title = "Still Life with Blue Roses",
                artist = "Unknown Artist",
                year = "2021"
            ),
            Artwork(
                imageRes = R.drawable.artwork_2, // 第二张图
                title = "Mountain Landscape",
                artist = "Landscape Painter",
                year = "1890"
            ),
            Artwork(
                imageRes = R.drawable.artwork_3, // 第三张图
                title = "Abstract Composition",
                artist = "Abstract Artist",
                year = "2005"
            )
        )

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                ArtSpaceApp(artworks)
            }
        }
    }
}

@Composable
fun ArtSpaceApp(artworks: List<Artwork>) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        ArtworkWall(artwork = artworks[currentIndex])
        ArtworkDescriptor(artwork = artworks[currentIndex])
        DisplayController(
            onPreviousClick = {
                currentIndex = if (currentIndex == 0) artworks.size - 1 else currentIndex - 1
            },
            onNextClick = {
                currentIndex = if (currentIndex == artworks.size - 1) 0 else currentIndex + 1
            }
        )
    }
}

@Composable
fun ArtworkWall(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(4.dp)),
        shape = RoundedCornerShape(4.dp),
        color = Color.White
    ) {
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = artwork.title,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ArtworkDescriptor(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFFF0F0F5)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = artwork.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${artwork.artist} (${artwork.year})",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun DisplayController(
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.85f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPreviousClick,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6599))
        ) {
            Text("Previous", color = Color.White)
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6599))
        ) {
            Text("Next", color = Color.White)
        }
    }
}