package com.example.rewind

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rewind.presentation.navigation.Route
import com.example.rewind.presentation.screens.home.HomeScreen
import com.example.rewind.presentation.theme.RewindTheme
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeMovieRepository = FakeMovieRepository()

    @Before
    fun setup() {
        fakeMovieRepository.prepopulate(buildTestMovie("Parasite"))
        try { stopKoin() } catch (e: Exception) { }
        startKoin {
            modules(buildNavigationTestModule(fakeMovieRepository))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun clickingMovieInList_navigatesToDetailScreen() {
        var navigatedToMovieId: Long? = null

        composeTestRule.setContent {
            val navController = rememberNavController()
            RewindTheme {
                NavHost(navController = navController, startDestination = Route.Home) {
                    composable<Route.Home> {
                        HomeScreen(
                            onAddClick = {},
                            onMovieClick = { id ->
                                navigatedToMovieId = id
                                navController.navigate(Route.MovieDetail(id))
                            }
                        )
                    }
                    composable<Route.MovieDetail> {
                        Text(text = "DETAIL FILM")
                    }
                }
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Parasite")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Parasite").performClick()

        assertNotNull(navigatedToMovieId)
        composeTestRule.onNodeWithText("DETAIL FILM").assertIsDisplayed()
    }
}