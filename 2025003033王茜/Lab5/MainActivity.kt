package java.com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

    val current = when (currentArtwork) {
        1 -> Artwork(
            imageRes = R.drawable.artwork1,
            title = "Mona Lisa",
            artistYear = "Leonardo da Vinci (1503-1519)"
        )
        2 -> Artwork(
            imageRes = R.drawable.artwork2,
            title = "Sunflowers",
            artistYear = "Vincent van Gogh (1888)"
        )
        else -> Artwork(
            imageRes = R.drawable.artwork3,
            title = "The Starry Night",
            artistYear = "Vincent van Gogh (1889)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 艺术作品展示区
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(current.imageRes),
                contentDescription = "Artwork",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentScale = ContentScale.Fit
            )
        }

        // 作品信息区
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F8))
                .padding(24.dp)
        ) {
            Text(
                text = current.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = current.artistYear,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 按钮控制区
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    currentArtwork = when (currentArtwork) {
                        1 -> 3
                        else -> currentArtwork - 1
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF4A639E)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50)
            ) {
                Text("Previous", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    currentArtwork = when (currentArtwork) {
                        3 -> 1
                        else -> currentArtwork + 1
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF4A639E)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50)
            ) {
                Text("Next", fontSize = 18.sp)
            }
        }
    }
}

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artistYear: String
)