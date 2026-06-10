package com.example.drawable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
            ArtSpaceLayout()
        }
    }
}

@Composable
fun ArtSpaceLayout() {
    var currentNumber by remember { mutableStateOf(1) }

    // 图片资源
    val imageResource = when (currentNumber) {
        1 -> R.drawable.photo1
        2 -> R.drawable.photo2
        else -> R.drawable.photo3
    }

    val title = when (currentNumber) {
        1 -> "Nature Scene"
        2 -> "Flower Blossom"
        3 -> "Sunset View"
        else -> ""
    }

    val artist = when (currentNumber) {
        1 -> "Photographer: Alex • 2024"
        2 -> "Photographer: Bella • 2024"
        3 -> "Photographer: Charlie • 2024"
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 垂直居中
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "Artwork",
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.7f)
                    .aspectRatio(0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD)) 
                    .padding(vertical = 16.dp), 
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 作者信息
                Text(
                    text = artist,
                    fontSize = 20.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp), 
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    currentNumber = if (currentNumber > 1) currentNumber - 1 else 3
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text("Previous", fontSize = 18.sp)
            }

            Button(
                onClick = {
                    currentNumber = if (currentNumber < 3) currentNumber + 1 else 1
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text("Next", fontSize = 18.sp)
            }
        }
    }
}