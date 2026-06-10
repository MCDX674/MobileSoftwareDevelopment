package java.com.example.diceroller
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
    // 状态：当前显示第几张作品
    var currentArt by remember { mutableStateOf(1) }

    // 切换图片
    val imageRes = when (currentArt) {
        1 -> R.drawable.art1
        2 -> R.drawable.art2
        else -> R.drawable.art3
    }

    // 作品信息
    val title = when (currentArt) {
        1 -> "蒙娜丽莎"
        2 -> "向日葵"
        else -> "星空"
    }

    val artist = when (currentArt) {
        1 -> "文森特·梵高"
        2 -> "克劳德·莫奈"
        else -> "文森特·梵高"
    }

    val year = when (currentArt) {
        1 -> "1889"
        2 -> "1872"
        else -> "1888"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 作品展示区（带阴影画框）
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            shadowElevation = 16.dp,
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 作品信息
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$artist • $year",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 上一个 / 下一个 按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                // 上一张：循环切换
                currentArt = when (currentArt) {
                    1 -> 3
                    else -> currentArt - 1
                }
            }) {
                Text("Previous")
            }

            Button(onClick = {
                // 下一张：循环切换
                currentArt = when (currentArt) {
                    3 -> 1
                    else -> currentArt + 1
                }
            }) {
                Text("Next")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceApp()
}