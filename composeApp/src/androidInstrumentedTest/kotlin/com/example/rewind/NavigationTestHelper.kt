package com.example.rewind

import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.MovieRepository
import com.example.rewind.domain.repository.TmdbRepository
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.GetTrendingUseCase
import com.example.rewind.domain.usecase.SaveMovieUseCase
import com.example.rewind.domain.usecase.SearchTmdbUseCase
import com.example.rewind.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

class FakeMovieRepository : MovieRepository {
    private val movieList = mutableListOf<Movie>()

    override fun getAllMovies(): Flow<List<Movie>> = flowOf(movieList)

    override fun getMovieByID(id: Long): Flow<Movie?> =
        flowOf(movieList.find { it.id == id })

    override fun getMoviesByStatus(status: WatchStatus): Flow<List<Movie>> =
        flowOf(movieList.filter { it.status == status })

    override fun getMoviesByType(type: MovieType): Flow<List<Movie>> =
        flowOf(movieList.filter { it.type == type })

    override fun getMoviesByGenre(genre: MovieGenre): Flow<List<Movie>> =
        flowOf(movieList.filter { it.genre == genre })

    override fun searchMovies(query: String): Flow<List<Movie>> =
        flowOf(movieList.filter { it.title.contains(query, ignoreCase = true) })

    override fun getFavoriteMovies(): Flow<List<Movie>> = flowOf(emptyList())

    override suspend fun insertMovie(movie: Movie): Long {
        val nextId = (movieList.size + 1).toLong()
        movieList.add(movie.copy(id = nextId))
        return nextId
    }

    override suspend fun updateMovie(movie: Movie) {
        val index = movieList.indexOfFirst { it.id == movie.id }
        if (index != -1) {
            movieList[index] = movie
        }
    }

    override suspend fun deleteMovie(id: Long) {
        movieList.removeAll { it.id == id }
    }
}

class FakeUiTmdbRepository : TmdbRepository {
    override suspend fun searchMulti(query: String, page: Int): NetworkResult<List<TmdbMovieDto>> =
        NetworkResult.Success(emptyList())
    override suspend fun getMovieDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> =
        NetworkResult.Error("Not implemented")
    override suspend fun getTvDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> =
        NetworkResult.Error("Not implemented")
    override suspend fun getTrending(): NetworkResult<List<TmdbMovieDto>> =
        NetworkResult.Success(emptyList())
}

fun buildNavigationTestModule(fakeMovieRepository: FakeMovieRepository): Module = module {
    single { fakeMovieRepository }
    single<TmdbRepository> { FakeUiTmdbRepository() }
    factory { GetAllMoviesUseCase(get()) }
    factory { DeleteMovieUseCase(get()) }
    factory { SearchTmdbUseCase(get()) }
    factory { SaveMovieUseCase(get()) }
    factory { GetTrendingUseCase(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
}

fun buildTestMovie(title: String = "Parasite"): Movie = Movie(
    id = 0L,
    title = title,
    genre = MovieGenre.THRILLER,
    type = MovieType.MOVIE,
    status = WatchStatus.COMPLETED,
    rating = 4.5f,
    review = "A gripping masterpiece.",
    totalEpisodes = null,
    watchedEpisodes = 0,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    posterUrl = null
)

fun FakeMovieRepository.prepopulate(vararg movies: Movie) = runBlocking {
    movies.forEach { insertMovie(it) }
}