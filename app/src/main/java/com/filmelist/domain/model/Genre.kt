package com.filmelist.domain.model

val genreMap = mapOf(
    28 to "Ação",
    12 to "Aventura",
    16 to "Animação",
    35 to "Comédia",
    80 to "Crime",
    99 to "Documentário",
    18 to "Drama",
    10751 to "Família",
    14 to "Fantasia",
    36 to "História",
    27 to "Terror",
    10402 to "Música",
    9648 to "Mistério",
    10749 to "Romance",
    878 to "Ficção Científica",
    10770 to "Cinema TV",
    53 to "Thriller",
    10752 to "Guerra",
    37 to "Faroeste",
    10759 to "Ação e Aventura",
    10762 to "Kids",
    10763 to "Notícias",
    10764 to "Reality",
    10765 to "Sci-Fi e Fantasia",
    10766 to "Soap",
    10767 to "Talk",
    10768 to "Guerra e Política",
)

fun List<Int>.toGenreNames(): String =
    take(3).mapNotNull { genreMap[it] }.joinToString(", ")
