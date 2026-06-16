package com.filmelist.data.remote.api

import com.filmelist.data.remote.dto.TmdbMovieDto
import com.filmelist.data.remote.dto.TmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "pt-BR",
        @Query("page") page: Int = 1,
    ): TmdbSearchResponse

    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("language") language: String = "pt-BR",
        @Query("page") page: Int = 1,
    ): TmdbSearchResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "pt-BR",
    ): TmdbMovieDto

    @GET("tv/{id}")
    suspend fun getTvDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "pt-BR",
    ): TmdbMovieDto
}
