package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

data class RecentTracksResponseDto(
    val recentTracks: List<LastFMRecentTrackDto>
)

data class LastFMRecentTrackDto(
    val artistName: String,
    val images: List<LastFMImageDto>,
    val albumName: String,
    val trackName: String,
)

data class LastFMImageDto(
    val size: String,
    val url: String?
)