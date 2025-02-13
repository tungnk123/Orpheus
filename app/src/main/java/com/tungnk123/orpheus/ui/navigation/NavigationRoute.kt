package com.tungnk123.orpheus.ui.navigation

sealed class NavigationRoute(val route: String, val name: String = "") {
    data object ForYou : NavigationRoute("for_you", "For You")
    data object Songs : NavigationRoute("songs", "Songs")
    data object Artists : NavigationRoute("artists", "Artists")
    data object Albums : NavigationRoute("albums", "Albums")
    data object AlbumArtists : NavigationRoute("album_artists", "Album Artists")
    data object Genres : NavigationRoute("genres", "Genres")
    data object Playlists : NavigationRoute("playlists", "Playlists")
    data object Browser : NavigationRoute("browser", "Browser")
    data object Folders : NavigationRoute("folders", "Folders")

    data class AlbumDetails(val albumId: String) : NavigationRoute("album_details/$albumId") {
        companion object {
            const val baseRoute = "album_details"
            private const val ARG_ALBUM_ID = "albumId"

            fun createRoute(albumId: String) = "$baseRoute/$albumId"

            fun fromRoute(route: String): AlbumDetails? {
                return route.takeIf { it.startsWith(baseRoute) }
                    ?.substringAfterLast("/")
                    ?.let { AlbumDetails(it) }
            }
        }
    }
}
