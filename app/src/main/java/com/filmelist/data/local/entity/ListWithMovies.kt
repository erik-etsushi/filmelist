package com.filmelist.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ListWithMovies(
    @Embedded val list: ListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ListMovieCrossRef::class,
            parentColumn = "listId",
            entityColumn = "movieId",
        ),
    )
    val movies: List<MovieEntity>,
)
