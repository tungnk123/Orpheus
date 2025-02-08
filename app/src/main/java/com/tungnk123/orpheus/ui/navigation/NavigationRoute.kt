package com.tungnk123.orpheus.ui.navigation

sealed class NavigationRoute(val route: String) {
    data object ForYou : NavigationRoute("for_you")
    data object Songs : NavigationRoute("songs")
    data object Artists : NavigationRoute("artists")
    data object Albums : NavigationRoute("albums")
    data object AlbumArtists : NavigationRoute("album_artists")
    data object Genres : NavigationRoute("genres")
    data object Playlists : NavigationRoute("playlists")
    data object Browser : NavigationRoute("browser")
    data object Folders : NavigationRoute("folders")

    data class AlbumDetails(val albumId: String) : NavigationRoute("album_details/$albumId") {
        companion object {
            const val baseRoute = "album_details"
            fun createRoute(albumId: String) = "$baseRoute/$albumId"
        }
    }
}
