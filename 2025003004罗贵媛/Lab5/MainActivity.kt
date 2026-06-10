import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.R

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
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. 准备3个作品数据
                    val artworks = listOf(
                        Artwork(
                            id = 1,
                            imageRes = R.drawable.artwork_1,
                            title = "蓝色玫瑰",
                            artist = "梵高",
                            year = "1889"
                        ),
                        Artwork(
                            id = 2,
                            imageRes = R.drawable.artwork_2,
                            title = "向日葵",
                            artist = "梵高",
                            year = "1888"
                        ),
                        Artwork(
                            id = 3,
                            imageRes = R.drawable.artwork_3,
                            title = "星空",
                            artist = "梵高",
                            year = "1889"
                        )
                    )

                    ArtSpaceApp(artworks = artworks)
                }
            }
        }
    }
}

@Composable
fun ArtSpaceApp(artworks: List<Artwork>) {
    // 3. 用mutableStateOf管理当前作品索引（状态管理核心）
    var currentIndex by remember { mutableStateOf(0) }
    val currentArtwork = artworks[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 作品墙
        ArtworkWall(artwork = currentArtwork)

        // 作品信息
        ArtworkDescriptor(artwork = currentArtwork)

        // 控制器（Previous/Next按钮）
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
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
            .border(4.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = "作品：${artwork.title}",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ArtworkDescriptor(artwork: Artwork) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = artwork.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${artwork.artist} (${artwork.year})",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DisplayController(onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onPreviousClick) {
            Text("Previous")
        }
        Button(onClick = onNextClick) {
            Text("Next")
        }
    }
}