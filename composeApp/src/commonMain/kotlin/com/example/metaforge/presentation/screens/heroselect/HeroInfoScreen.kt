package com.example.metaforge.presentation.screens.heroselect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroInfoScreen(heroId: Int, heroName: String, heroRole: String, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis Detail") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(heroName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(heroRole, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("META STATS OVERVIEW", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoStatCard("Win Rate", "54.1%", Color.Green)
                InfoStatCard("Pick Rate", "18.5%", Color.White)
                InfoStatCard("Ban Rate", "62.3%", MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(32.dp))

            Text("MATCHUPS & METAGAME", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            MatchSection("Strong Against (Meta Counters)", listOf("Layla", "Miya", "Pharsa"), Color.Green)
            Spacer(modifier = Modifier.height(12.dp))

            MatchSection("Weak Against (Countered By)", listOf("Khufra", "Chou", "Kaja"), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))

            MatchSection("Perfect Synergy (Team Combo)", listOf("Angela", "Tigreal", "Atlas"), MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun RowScope.InfoStatCard(title: String, value: String, valueColor: Color) {
    Card(modifier = Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun MatchSection(title: String, heroes: List<String>, titleColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = titleColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            heroes.forEach { name -> Text("• $name", color = Color.White, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp)) }
        }
    }
}