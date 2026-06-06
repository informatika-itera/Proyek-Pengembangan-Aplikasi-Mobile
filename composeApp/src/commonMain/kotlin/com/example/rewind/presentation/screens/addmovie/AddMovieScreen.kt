package com.example.rewind.presentation.screens.addmovie

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.BorderGold
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.GoldAmberDim
import com.example.rewind.presentation.theme.LocalRewindColors
import com.example.rewind.presentation.theme.StatusDropped
import com.example.rewind.presentation.theme.StatusFinished
import com.example.rewind.presentation.theme.StatusOnHold
import com.example.rewind.presentation.theme.StatusWantToWatch
import com.example.rewind.presentation.theme.StatusWatching
import com.example.rewind.presentation.theme.TheaterRed
import com.example.rewind.presentation.theme.VelvetRed
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions

@Composable
fun AddMovieScreen(
    movieId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddMovieViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(MovieGenre.OTHER) }
    var selectedType by remember { mutableStateOf(MovieType.MOVIE) }
    var selectedStatus by remember { mutableStateOf(WatchStatus.PLAN_TO_WATCH) }
    var rating by remember { mutableStateOf(0f) }
    var review by remember { mutableStateOf("") }
    var totalEpisodesText by remember { mutableStateOf("") }
    var watchedEpisodesText by remember { mutableStateOf("") }
    val isEditMode = movieId != null
    var titleError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(movieId) {
        viewModel.loadMovieForEdit(movieId)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddMovieUiState.EditMode -> {
                val movie = state.movie
                title = movie.title
                selectedGenre = movie.genre
                selectedType = movie.type
                selectedStatus = movie.status
                rating = movie.rating ?: 0f
                review = movie.review
                totalEpisodesText = movie.totalEpisodes?.toString() ?: ""
                watchedEpisodesText = movie.watchedEpisodes.toString()
            }
            is AddMovieUiState.Success -> onNavigateBack()
            else -> {}
        }
    }

    val bg = MaterialTheme.colorScheme.background
    val rewindColors = LocalRewindColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val saveSrc = remember { MutableInteractionSource() }
    val savePressed by saveSrc.collectIsPressedAsState()
    val saveScale by animateFloatAsState(
        targetValue = if (savePressed && uiState !is AddMovieUiState.Loading) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "saveScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-30).dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GoldAmber.copy(alpha = glowPulse * 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TheaterRed.copy(alpha = glowPulse * 0.1f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            AddMovieHeader(
                onNavigateBack = onNavigateBack,
                isEditMode = isEditMode
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                SectionLabel("TITLE")
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (titleError && it.isNotBlank()) titleError = false
                    },
                    placeholder = {
                        Text(
                            "Movie or series title...",
                            color = rewindColors.textMuted,
                            fontSize = 14.sp
                        )
                    },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text(
                                text = "Judul tidak boleh kosong",
                                color = TheaterRed,
                                fontSize = 11.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    colors = fieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                )

                SectionLabel("TYPE")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MovieType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        val chipSrc = remember { MutableInteractionSource() }
                        val chipPressed by chipSrc.collectIsPressedAsState()
                        val chipScale by animateFloatAsState(
                            targetValue = if (chipPressed) 0.92f else 1f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                            label = "typeChipScale"
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer(scaleX = chipScale, scaleY = chipScale)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                                    )
                                    .border(
                                        BorderStroke(1.dp, GoldAmber.copy(alpha = 0.6f)),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = chipSrc,
                                        indication = LocalIndication.current
                                    ) { selectedType = type }
                                    .padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = type.displayName,
                                    color = bg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer(scaleX = chipScale, scaleY = chipScale)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(rewindColors.surfaceElevated)
                                    .border(
                                        BorderStroke(1.dp, rewindColors.borderSubtle),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = chipSrc,
                                        indication = LocalIndication.current
                                    ) { selectedType = type }
                                    .padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = type.displayName,
                                    color = rewindColors.textMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                SectionLabel("GENRE")
                GenreDropdown(selected = selectedGenre, onSelect = { selectedGenre = it })

                SectionLabel("STATUS")
                StatusSelector(selected = selectedStatus, onSelect = { selectedStatus = it })

                AnimatedVisibility(
                    visible = selectedType != MovieType.MOVIE,
                    enter = fadeIn(tween(250)) + slideInVertically(
                        animationSpec = tween(300),
                        initialOffsetY = { -it / 2 }
                    ),
                    exit = fadeOut(tween(200)) + slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { -it / 2 }
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        SectionLabel("TOTAL EPISODES")
                        OutlinedTextField(
                            value = totalEpisodesText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) totalEpisodesText = it },
                            placeholder = {
                                Text("Number of episodes...", color = rewindColors.textMuted, fontSize = 14.sp)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = fieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                SectionLabel("RATING")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(rewindColors.surfaceElevated)
                        .border(
                            BorderStroke(1.dp, rewindColors.borderSubtle),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "★".repeat(rating.toInt()) + "☆".repeat(5 - rating.toInt()),
                                color = GoldAmber,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                            AnimatedContent(
                                targetState = rating.toInt(),
                                transitionSpec = {
                                    fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                                },
                                label = "ratingLabel"
                            ) { ratingInt ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            GoldAmber.copy(alpha = 0.12f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            BorderStroke(0.5.dp, rewindColors.borderGold.copy(alpha = 0.5f)),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$ratingInt / 5",
                                        color = GoldAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 0f..5f,
                            steps = 4,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = GoldAmber,
                                activeTrackColor = GoldAmber,
                                inactiveTrackColor = VelvetRed.copy(alpha = 0.25f),
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent
                            )
                        )
                    }
                }

                SectionLabel("NOTES / REVIEW")
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    placeholder = {
                        Text(
                            "Write your thoughts about this title...",
                            color = rewindColors.textMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = fieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                )

                AnimatedVisibility(
                    visible = uiState is AddMovieUiState.Error,
                    enter = fadeIn(tween(250)) + slideInVertically(
                        animationSpec = tween(300),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(tween(200)) + slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { it / 2 }
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TheaterRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(
                                BorderStroke(1.dp, TheaterRed.copy(alpha = 0.3f)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = (uiState as? AddMovieUiState.Error)?.message ?: "",
                            color = TheaterRed,
                            fontSize = 13.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState !is AddMovieUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .blur(40.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(GoldAmber.copy(alpha = glowPulse * 0.18f), Color.Transparent)
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = saveScale, scaleY = saveScale)
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (uiState !is AddMovieUiState.Loading)
                                    Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            GoldAmberDim.copy(alpha = 0.4f),
                                            GoldAmber.copy(alpha = 0.4f)
                                        )
                                    )
                            )
                            .clickable(
                                interactionSource = saveSrc,
                                indication = LocalIndication.current,
                                enabled = uiState !is AddMovieUiState.Loading
                            ) {
                                if (title.isBlank()){
                                    titleError = true
                                } else {
                                    viewModel.saveMovie(
                                        title = title,
                                        genre = selectedGenre,
                                        type = selectedType,
                                        status = selectedStatus,
                                        rating = if (rating > 0f) rating else null,
                                        review = review,
                                        totalEpisodes = totalEpisodesText.toIntOrNull(),
                                        watchedEpisodes = watchedEpisodesText.toIntOrNull() ?: 0
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = uiState is AddMovieUiState.Loading,
                            transitionSpec = {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                            },
                            label = "saveButtonContent"
                        ) { isLoading ->
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = bg,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isEditMode) "Update Collection" else "Save to Collection",
                                    color = bg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun AddMovieHeader(onNavigateBack: () -> Unit, isEditMode: Boolean = false) {
    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val backSrc = remember { MutableInteractionSource() }
    val backPressed by backSrc.collectIsPressedAsState()
    val backScale by animateFloatAsState(
        targetValue = if (backPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "backScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surface)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.3f),
                            GoldAmber.copy(alpha = 0.2f),
                            BorderGold.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer(scaleX = backScale, scaleY = backScale)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(
                        BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)),
                        CircleShape
                    )
                    .clickable(
                        interactionSource = backSrc,
                        indication = LocalIndication.current,
                        onClick = onNavigateBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = GoldAmber, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = if (isEditMode) "EDIT TITLE" else "ADD TITLE",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Text(
                    text = if (isEditMode) "Edit Entry" else "New Entry",
                    color = onBg,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val rewindColors = LocalRewindColors.current
    Text(
        text = text,
        color = rewindColors.textMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.5.sp
    )
}

@Composable
private fun StatusSelector(selected: WatchStatus, onSelect: (WatchStatus) -> Unit) {
    val rewindColors = LocalRewindColors.current
    val statusList = listOf(
        WatchStatus.PLAN_TO_WATCH to "Plan",
        WatchStatus.WATCHING      to "Watch",
        WatchStatus.COMPLETED     to "Done",
        WatchStatus.ON_HOLD       to "Hold",
        WatchStatus.DROPPED       to "Drop"
    )
    val statusColors = mapOf(
        WatchStatus.PLAN_TO_WATCH to StatusWantToWatch,
        WatchStatus.WATCHING      to StatusWatching,
        WatchStatus.COMPLETED     to StatusFinished,
        WatchStatus.ON_HOLD       to StatusOnHold,
        WatchStatus.DROPPED       to StatusDropped
    )
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        statusList.forEach { (status, label) ->
            val isSelected = selected == status
            val color = statusColors[status] ?: GoldAmber
            val chipSrc = remember { MutableInteractionSource() }
            val chipPressed by chipSrc.collectIsPressedAsState()
            val chipScale by animateFloatAsState(
                targetValue = if (chipPressed) 0.88f else 1f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                label = "statusChipScale"
            )
            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = chipScale, scaleY = chipScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) color.copy(alpha = 0.15f) else rewindColors.surfaceElevated
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isSelected) color.copy(alpha = 0.45f) else rewindColors.borderSubtle
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = chipSrc,
                        indication = LocalIndication.current
                    ) { onSelect(status) }
                    .padding(horizontal = 11.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) color else rewindColors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GenreDropdown(selected: MovieGenre, onSelect: (MovieGenre) -> Unit) {
    val rewindColors = LocalRewindColors.current
    val onBg = MaterialTheme.colorScheme.onBackground
    var expanded by remember { mutableStateOf(false) }

    val dropdownSrc = remember { MutableInteractionSource() }
    val dropdownPressed by dropdownSrc.collectIsPressedAsState()
    val dropdownScale by animateFloatAsState(
        targetValue = if (dropdownPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "dropdownScale"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleX = dropdownScale, scaleY = dropdownScale)
                .clip(RoundedCornerShape(12.dp))
                .background(rewindColors.surfaceElevated)
                .border(
                    BorderStroke(
                        1.dp,
                        if (expanded) GoldAmber.copy(alpha = 0.5f) else rewindColors.borderSubtle
                    ),
                    RoundedCornerShape(12.dp)
                )
                .clickable(
                    interactionSource = dropdownSrc,
                    indication = LocalIndication.current
                ) { expanded = true }
                .padding(horizontal = 16.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selected.displayName, color = onBg, fontSize = 14.sp)
                Text("▾", color = GoldAmber, fontSize = 13.sp)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(rewindColors.surfaceElevated)
                .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(8.dp))
        ) {
            MovieGenre.entries.forEach { genre ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = genre.displayName,
                            color = if (genre == selected) GoldAmber else rewindColors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (genre == selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldAmber.copy(alpha = 0.7f),
    unfocusedBorderColor = LocalRewindColors.current.borderSubtle,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    cursorColor = GoldAmber,
    focusedContainerColor = LocalRewindColors.current.surfaceElevated,
    unfocusedContainerColor = LocalRewindColors.current.surfaceElevated,
    focusedLabelColor = GoldAmber
)