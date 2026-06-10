package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

data class Artwork(
    val id: Int,
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
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
    val artworkList = listOf(
        Artwork(
            id = 1,
            imageRes = R.drawable.artwork_1,
            title = "Still Life of Blue Rose and Other Flowers",
            artist = "Owen Scott",
            year = "2021"
        ),
        Artwork(
            id = 2,
            imageRes = R.drawable.artwork_2,
            title = "山",
            artist = "zyl",
            year = "2025"
        ),
        Artwork(
            id = 3,
            imageRes = R.drawable.artwork_3,
            title = "The Starry Night",
            artist = "Vincent Willem van Gogh",
            year = "1889"
        )
    )

    var currentArtworkIndex by remember { mutableIntStateOf(0) }
    val currentArtwork = artworkList[currentArtworkIndex]
    val totalCount = artworkList.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtworkWall(
            imageRes = currentArtwork.imageRes,
            contentDescription = currentArtwork.title,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ArtworkDescriptor(
            title = currentArtwork.title,
            artist = currentArtwork.artist,
            year = currentArtwork.year,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        DisplayController(
            onPreviousClick = {
                currentArtworkIndex = when (currentArtworkIndex) {
                    0 -> totalCount - 1
                    else -> currentArtworkIndex - 1
                }
            },
            onNextClick = {
                currentArtworkIndex = when (currentArtworkIndex) {
                    totalCount - 1 -> 0
                    else -> currentArtworkIndex + 1
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ArtworkWall(
    imageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(8.dp, shape = RoundedCornerShape(4.dp))
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ArtworkDescriptor(
    title: String,
    artist: String,
    year: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF0F0F5),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$artist ($year)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun DisplayController(
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPreviousClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5A98))
        ) {
            Text(
                text = "Previous",
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5A98))
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtSpaceAppPreview() {
    MyApplicationTheme {
        ArtSpaceApp()
    }
}