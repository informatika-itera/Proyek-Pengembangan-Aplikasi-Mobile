package com.itera.news.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itera.news.app.R
import com.itera.news.domain.model.Article
import com.itera.news.presentation.viewmodel.NewsUiState
import com.itera.news.presentation.viewmodel.NewsViewModel
import com.itera.news.presentation.viewmodel.SortOrder
import com.itera.news.ui.theme.neumorphicShadow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDetail: (String) -> Unit,
    viewModel: NewsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with refresh button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEWS MBG AI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Refresh button
                    IconButton(
                        onClick = { viewModel.refreshNews() },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar + Filter Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search bar (lebih kecil, pakai weight agar sisa ruang diisi filter icon)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Cari berita...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .neumorphicShadow(offset = 4.dp, blur = 8.dp, cornerRadius = 14.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    // Filter / Sort icon button
                    Box {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .neumorphicShadow(
                                    offset = if (sortOrder != SortOrder.NEWEST) 0.dp else 4.dp,
                                    blur = if (sortOrder != SortOrder.NEWEST) 0.dp else 8.dp,
                                    cornerRadius = 14.dp
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (sortOrder != SortOrder.NEWEST) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { showSortMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter_list),
                                contentDescription = "Filter urutan",
                                tint = if (sortOrder != SortOrder.NEWEST) MaterialTheme.colorScheme.onPrimary
                                       else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Dropdown menu
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            val sortOptions = listOf(
                                SortOrder.NEWEST to "Terbaru",
                                SortOrder.OLDEST to "Terlama",
                                SortOrder.A_Z    to "A → Z",
                                SortOrder.Z_A    to "Z → A"
                            )
                            sortOptions.forEach { (order, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal,
                                            color = if (sortOrder == order) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSortOrderSelected(order)
                                        showSortMenu = false
                                    },
                                    trailingIcon = if (sortOrder == order) ({
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                    }) else null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category Tabs
                val categories = listOf("Semua", "Pro", "Kontra", "Netral")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .neumorphicShadow(
                                    offset = if (isSelected) 0.dp else 3.dp,
                                    blur = if (isSelected) 0.dp else 6.dp,
                                    cornerRadius = 12.dp
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { viewModel.onCategorySelected(category) }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is NewsUiState.Loading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(5) { ShimmerItem() }
                    }
                }
                is NewsUiState.Success -> {
                    val articles = (uiState as NewsUiState.Success).articles
                    if (articles.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📰",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tidak ada berita ditemukan",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            items(articles, key = { it.url }) { article ->
                                ArticleCard(
                                    article = article,
                                    onClick = { navigateToDetail(article.url) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
                is NewsUiState.Error -> {
                    val errorMsg = (uiState as NewsUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ups! Terjadi kendala",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.fetchNews() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            // Loading indicator overlay when refreshing
            if (isRefreshing && uiState is NewsUiState.Success) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun ShimmerItem() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.3f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .neumorphicShadow(offset = 4.dp, blur = 8.dp, cornerRadius = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(brush)
    )
}

@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit,
    viewModel: NewsViewModel
) {
    val isBookmarked by viewModel.isArticleBookmarked(article.url).collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neumorphicShadow(
                offset = 6.dp,
                blur = 12.dp,
                cornerRadius = 20.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        Column {
            if (article.imageUrl.isNotEmpty()) {
                Box {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = "Gambar Berita",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    // Bookmark button overlay
                    IconButton(
                        onClick = {
                            viewModel.toggleBookmark(article, isBookmarked)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (isBookmarked) "Hapus bookmark" else "Simpan bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    lineHeight = 22.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.sourceName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    val glowColor = when (article.category) {
                        "Pro" -> Color(0xFF4CAF50)
                        "Kontra" -> Color(0xFFF44336)
                        else -> Color(0xFFFFEB3B)
                    }

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .neumorphicShadow(
                                offset = 0.dp,
                                blur = 10.dp,
                                lightShadowColor = glowColor,
                                darkShadowColor = glowColor,
                                cornerRadius = 5.dp
                            )
                            .background(glowColor, CircleShape)
                    )
                }
            }
        }
    }
}
