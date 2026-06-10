package com.yxyn.homework7
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val artworkTitle = when (currentArtwork) {
        1 -> "Starry Night"
        2 -> "Mona Lisa"
        else -> "The Persistence of Memory"
    }

    val artworkArtist = when (currentArtwork) {
        1 -> "Vincent van Gogh"
        2 -> "Leonardo da Vinci"
        else -> "Salvador Dalí"
    }

    val artworkYear = when (currentArtwork) {
        1 -> "1889"
        2 -> "1503"
        else -> "1931"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 艺术作品墙
        ArtworkWall(imageResource = imageResource)

        Spacer(modifier = Modifier.height(32.dp))

        // 艺术作品说明
        ArtworkDescriptor(title = artworkTitle, artist = artworkArtist, year = artworkYear)

        Spacer(modifier = Modifier.height(32.dp))

        // 显示控制器
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

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ArtworkWall(imageResource: Int) {
    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(400.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp)), // 把 elevation 换成 Modifier.shadow
        shape = RoundedCornerShape(size = 8.dp),
        border = BorderStroke(width = 2.dp, Color.Gray)
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ArtworkDescriptor(title: String, artist: String, year: String) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$artist, $year",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DisplayController(onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .width(300.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.width(140.dp)
        ) {
            Text(text = "Previous")
        }
        Button(
            onClick = onNext,
            modifier = Modifier.width(140.dp)
        ) {
            Text(text = "Next")
        }
    }
}
