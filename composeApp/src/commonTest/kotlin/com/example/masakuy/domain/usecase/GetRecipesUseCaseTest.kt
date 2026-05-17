package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.repository.RecipeRepository
import com.example.masakuy.util.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class GetRecipesUseCaseTest {
    @Test
    fun testGetRecipes_Success() = runTest {
        // Arrange
        val mockRepository = mockk<RecipeRepository>()
        val expectedRecipes = listOf(TestData.createRecipe())
        coEvery { mockRepository.getAllRecipes() } returns flowOf(
            Result.Success(expectedRecipes)
        )

        val useCase = GetRecipesUseCase(mockRepository)

        // Act
        var result: Result<*>? = null
        useCase().collect { resultData ->
            result = resultData
        }

        // Assert
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isNotEmpty())
    }
}