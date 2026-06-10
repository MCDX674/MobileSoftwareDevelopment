package com.example.sports.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sports.R
import com.example.sports.data.LocalSportsDataProvider
import com.example.sports.model.Sport
import com.example.sports.ui.theme.SportsTheme
import com.example.sports.utils.SportsContentType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SportsApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    viewModel: SportsViewModel = viewModel()
) {
    // ✅ 根据屏幕宽度动态判断布局类型，不是硬编码
    val contentType = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> SportsContentType.ListAndDetail
        else -> SportsContentType.ListOnly
    }

    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            SportsAppBar(
                isShowingListPage = uiState.isShowingListPage,
                isListAndDetail = contentType == SportsContentType.ListAndDetail,
                onBackButtonClick = { viewModel.navigateToListPage() }
            )
        }
    ) { innerPadding ->
        if (contentType == SportsContentType.ListAndDetail) {
            // ✅ 实现了双窗格布局 SportsListAndDetails
            SportsListAndDetails(
                sports = LocalSportsDataProvider.allSports,
                currentSport = uiState.currentSport,
                onSportClick = { viewModel.updateCurrentSport(it) },
                contentPadding = innerPadding
            )
        } else {
            if (uiState.isShowingListPage) {
                SportsList(
                    sports = LocalSportsDataProvider.allSports,
                    onClick = {
                        viewModel.updateCurrentSport(it)
                        viewModel.navigateToDetailPage()
                    },
                    contentPadding = innerPadding
                )
            } else {
                SportsDetail(
                    selectedSport = uiState.currentSport,
                    onBackPressed = { viewModel.navigateToListPage() },
                    contentPadding = innerPadding
                )
            }
        }
    }
}

@Composable
private fun SportsListAndDetails(
    sports: List<Sport>,
    currentSport: Sport,
    onSportClick: (Sport) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler {
        (context as Activity).finish()
    }

    // ✅ 使用 Row + Modifier.weight 实现列表与详情的并排显示
    Row(modifier = modifier.fillMaxSize().padding(contentPadding)) {
        SportsList(
            sports = sports,
            onClick = onSportClick,
            modifier = Modifier.weight(1f)
        )
        SportsDetail(
            selectedSport = currentSport,
            onBackPressed = {},
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun SportsList(
    sports: List<Sport>,
    onClick: (Sport) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sports) { sport ->
            Card(
                onClick = { onClick(sport) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(sport.title),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun SportsDetail(
    selectedSport: Sport,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    BackHandler(onBack = onBackPressed)

    Column(
        modifier = modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(selectedSport.title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(selectedSport.detail),
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsAppBar(
    isShowingListPage: Boolean,
    isListAndDetail: Boolean,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            // ✅ 根据状态切换标题：详情页显示 'Sport Info'，列表/大屏显示 'Sports'
            if (isListAndDetail || isShowingListPage) {
                Text(stringResource(R.string.list_fragment_label))
            } else {
                Text(stringResource(R.string.detail_fragment_label))
            }
        },
        navigationIcon = {
            // ✅ 控制返回按钮显隐：仅在小屏详情页显示
            if (!isShowingListPage && !isListAndDetail) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun SportsAppPreview() {
    SportsTheme {
        SportsApp(windowWidthSizeClass = WindowWidthSizeClass.Compact)
    }
}