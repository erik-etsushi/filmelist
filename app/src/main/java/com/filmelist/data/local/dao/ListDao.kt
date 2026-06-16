package com.filmelist.data.local.dao

import androidx.room.*
import com.filmelist.data.local.entity.ListEntity
import com.filmelist.data.local.entity.ListMovieCrossRef
import com.filmelist.data.local.entity.ListWithMovies
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {

    @Transaction
    @Query("SELECT * FROM lists ORDER BY isDefault DESC, createdAt ASC")
    fun getAllListsWithMovies(): Flow<List<ListWithMovies>>

    @Transaction
    @Query("SELECT * FROM lists WHERE id = :listId")
    fun getListWithMovies(listId: Long): Flow<ListWithMovies?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ListEntity): Long

    @Query("DELETE FROM lists WHERE id = :listId AND isDefault = 0")
    suspend fun deleteList(listId: Long)

    @Query("UPDATE lists SET name = :name WHERE id = :listId")
    suspend fun renameList(listId: Long, name: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovieToList(crossRef: ListMovieCrossRef)

    @Query("DELETE FROM list_movie_cross_ref WHERE listId = :listId AND movieId = :movieId")
    suspend fun removeMovieFromList(listId: Long, movieId: Int)

    @Query("SELECT COUNT(*) FROM list_movie_cross_ref WHERE listId = :listId AND movieId = :movieId")
    suspend fun isMovieInList(listId: Long, movieId: Int): Int

    @Query("SELECT COUNT(*) FROM lists WHERE isDefault = 1")
    suspend fun countDefaultLists(): Int
}
