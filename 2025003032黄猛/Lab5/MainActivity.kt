package com.example.myapplication1223

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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF1EEF3)
                ) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

@Composable
fun ArtSpaceApp() {
    val artworks = listOf(
        Artwork(
            imageRes = R.drawable.artwork_1,
            title = "Water Lilies",
            artist = "Claude Monet",
            year = 1916
        ),
        Artwork(
            imageRes = R.drawable.artwork_2,
            title = "Les Demoiselles d’Avignon",
            artist = "Pablo Picasso",
            year = 1907
        ),
        Artwork(
            imageRes = R.drawable.artwork_3,
            title = "The Starry Night",
            artist = "Van Gogh",
            year = 1889
        )
    )

    var currentArtworkIndex by remember { mutableStateOf(0) }
    val currentArtwork = artworks[currentArtworkIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        ArtworkWall(artwork = currentArtwork)

        ArtworkDescription(artwork = currentArtwork)

        DisplayController(
            onPrevious = {
                currentArtworkIndex = when (currentArtworkIndex) {
                    0 -> artworks.lastIndex
                    else -> currentArtworkIndex - 1
                }
            },
            onNext = {
                currentArtworkIndex = when (currentArtworkIndex) {
                    artworks.lastIndex -> 0
                    else -> currentArtworkIndex + 1
                }
            }
        )
    }
}

@Composable
fun ArtworkWall(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        color = Color(0xFFEBE8ED),
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, Color(0xFFD7D3DA))
    ) {
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = artwork.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
        )
    }
}

@Composable
fun ArtworkDescription(artwork: Artwork) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        color = Color(0xFFE5E2EA),
        border = BorderStroke(1.dp, Color(0xFFDDD9E2))
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = artwork.title,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = Color(0xFF202124)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(artwork.artist)
                    }
                    append(" (")
                    append(artwork.year.toString())
                    append(")")
                },
                fontSize = 17.sp,
                color = Color(0xFF202124)
            )
        }
    }
}

@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E66A3)),
            modifier = Modifier.width(150.dp)
        ) {
            Text(text = "Previous")
        }

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E66A3)),
            modifier = Modifier.width(150.dp)
        ) {
            Text(text = "Next")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtSpacePreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF1EEF3)
        ) {
            ArtSpaceApp()
        }
    }
}
