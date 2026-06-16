package com.filmelist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmelist.data.local.dao.ListDao
import com.filmelist.data.local.dao.MovieDao
import com.filmelist.data.local.entity.ListEntity
import com.filmelist.data.local.entity.ListMovieCrossRef
import com.filmelist.data.local.entity.MovieEntity

@Database(
    entities = [ListEntity::class, MovieEntity::class, ListMovieCrossRef::class],
    version = 1,
    exportSchema = false,
)
abstract class FilmeListDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao
    abstract fun movieDao(): MovieDao
}
