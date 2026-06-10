package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.artspace.R
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
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
    var currentArtIndex by remember { mutableStateOf(1) }

    val imageRes = when (currentArtIndex) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    val title = when (currentArtIndex) {
        1 -> "仙山楼阁图"
        2 -> "千里江山图"
        else -> "百鸟朝凤图"
    }

    val artist = when (currentArtIndex) {
        1 -> "赵伯驹"
        2 -> "王希孟"
        else -> "沈铨"
    }

    val year = when (currentArtIndex) {
        1 -> "南宋"
        2 -> "北宋政和三年"
        else -> "清乾隆五年"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 顶部留白，避免贴顶
        Spacer(modifier = Modifier.height(20.dp))

        ArtworkDisplay(imageRes = imageRes)

        ArtworkInfo(title = title, artist = artist, year = year)

        ControlButtons(
            onPreviousClick = {
                currentArtIndex = if (currentArtIndex == 1) 3 else currentArtIndex - 1
            },
            onNextClick = {
                currentArtIndex = if (currentArtIndex == 3) 1 else currentArtIndex + 1
            }
        )

        // 底部留白，适配全面屏手势
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ArtworkDisplay(imageRes: Int) {
    // 使用 Box 包裹，将边框画在最外层，避免被内部白色背景遮挡
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(
                width = 8.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(4.dp) // 稍微圆角一点会更好看
            )
            .background(Color.White) // 背景色放在 Box 上
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "艺术作品",
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp) // 图片内边距，制造留白效果
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun ArtworkInfo(title: String, artist: String, year: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp), // 增加垂直间距
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$artist · $year",
            fontSize = 16.sp, // 稍微调小字体以区分层级
            color = Color.Gray
        )
    }
}

@Composable
fun ControlButtons(onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center // 改为居中排列，视觉更平衡
    ) {
        Button(
            onClick = onPreviousClick,
            modifier = Modifier.width(130.dp)
        ) {
            Text("上一个")
        }
        Spacer(modifier = Modifier.width(16.dp)) // 两个按钮中间加点空隙
        Button(
            onClick = onNextClick,
            modifier = Modifier.width(130.dp)
        ) {
            Text("下一个")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}