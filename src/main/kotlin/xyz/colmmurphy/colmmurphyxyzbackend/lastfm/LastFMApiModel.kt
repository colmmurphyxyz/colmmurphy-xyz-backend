package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

import com.fasterxml.jackson.annotation.JsonProperty

data class GetRecentTracksResponse(
    @JsonProperty("recenttracks")
    val recentTracks: RecentTracksResponse
)

data class RecentTracksResponse(
    @JsonProperty("track")
    val tracks: List<LastFMTrackModel>,
    @JsonProperty("@attr")
    val attr: RecentTracksAttributes
)

data class LastFMTrackModel(
    val artist: LastFMArtistPreview,
    @JsonProperty("image")
    val images: List<LastFMImagePreview>,
    val album: LastFMAlbumPreview,
    val name: String,
    val url: String,
)

data class RecentTracksAttributes(
    val user: String,
    val totalPages: Int,
    val page: Int,
    val perPage: Int,
    val total: Int,
)

data class LastFMArtistPreview(
    val mbid: String?,
    @JsonProperty("#text")
    val text: String,
)

data class LastFMImagePreview(
    val size: String,
    @JsonProperty("#text")
    val url: String?
)

data class LastFMAlbumPreview(
    val mbid: String?,
    @JsonProperty("#text")
    val text: String
)