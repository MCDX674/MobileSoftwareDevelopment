package com.example.myapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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

    //状态
    var currentArtwork by remember { mutableStateOf(1) }

    //数据映射（图片 + 文本）
    val imageRes = when (currentArtwork) {
        1 -> R.drawable.artwork_1
        2 -> R.drawable.artwork_2
        else -> R.drawable.artwork_3
    }

    val title = when (currentArtwork) {
        1 -> "Blue Rose"
        2 -> "Sunset Memory"
        else -> "City Night"
    }

    val artist = when (currentArtwork) {
        1 -> "Owen Scott"
        2 -> "Emily Stone"
        else -> "Liam Wong"
    }

    val year = when (currentArtwork) {
        1 -> "2021"
        2 -> "2019"
        else -> "2023"
    }

    //整体布局（对应紫色外框）
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ======================
        //  区块1：Artwork Wall
        // ======================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ======================
        //  区块2：Artwork Info
        // ======================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEAEAF0),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = title,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    buildAnnotatedString {
                        append(artist)
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            0,
                            artist.length
                        )
                        append(" ($year)")
                    },
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ======================
        //  区块3：Controller
        // ======================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = {
                    currentArtwork = when (currentArtwork) {
                        1 -> 3
                        else -> currentArtwork - 1
                    }
                },
                shape = RoundedCornerShape(50)
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    currentArtwork = when (currentArtwork) {
                        3 -> 1
                        else -> currentArtwork + 1
                    }
                },
                shape = RoundedCornerShape(50)
            ) {
                Text("Next")
            }
        }
    }
}