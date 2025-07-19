package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.models.*
import com.adamratzman.spotify.utils.ExternalUrls

data class PlayHistoryDto(
    val playedAt: String,
    val track: TrackDto
)

data class SpotifyImageDto(
    val url: String,
    val height: Int,
    val width: Int
) {
    companion object {
        fun fromModel(model: SpotifyImage): SpotifyImageDto =
            SpotifyImageDto(
                url = model.url,
                height = model.height?.toInt() ?: 0,
                width = model.width?.toInt() ?: 0,
            )
    }
}

data class SimpleAlbumDto(
    val albumType: String,
    val totalTracks: Int,
    val externalUrls: ExternalSpotifyUrlsDto,
    val href: String,
    val id: String,
    val images: List<SpotifyImageDto>,
    val name: String,
    val releaseDate: String,
    val releaseDatePrecision: String,
    val type: String,
    val uri: String,
    val artists: List<SimpleArtistDto>
) {
    companion object {
        fun fromModel(model: SimpleAlbum): SimpleAlbumDto =
            SimpleAlbumDto(
                albumType = model.albumType.toString(),
                totalTracks = model.totalTracks ?: 0,
                externalUrls = ExternalSpotifyUrlsDto.fromModel(model.externalUrls),
                href = model.href,
                id = model.id,
                images = model.images?.map(SpotifyImageDto::fromModel) ?: listOf(),
                name = model.name,
                releaseDate = model.releaseDate.toString(),
                releaseDatePrecision = model.releaseDatePrecisionString ?: "",
                type = model.type,
                uri = model.uri.uri,
                artists = model.artists.map(SimpleArtistDto::fromModel)
            )
    }
}

data class ExternalSpotifyUrlsDto(
    val spotify: String
) {
    companion object {
        fun fromModel(model: ExternalUrls) =
            ExternalSpotifyUrlsDto(model.spotify ?: "")
    }
}

data class SimpleArtistDto(
    val externalUrls: ExternalSpotifyUrlsDto,
    val href: String,
    val id: String,
    val name: String?,
    val type: String,
    val uri: String
) {
    companion object {
        fun fromModel(model: SimpleArtist): SimpleArtistDto =
            SimpleArtistDto(
                externalUrls = ExternalSpotifyUrlsDto.fromModel(model.externalUrls),
                id = model.id,
                href = model.href,
                name = model.name,
                type = model.type,
                uri = model.uri.toString()
            )
    }
}

data class TrackDto(
    val album: SimpleAlbumDto,
    val artists: List<SimpleArtistDto>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val href: String,
    val id: String,
    val isPlayable: Boolean,
    val name: String,
    val popularity: Double,
    val trackNumber: Int,
    val type: String,
    val uri: String,
    val isLocal: Boolean
) {
    companion object {
        fun fromModel(model: Track): TrackDto =
            TrackDto(
                id = model.id,
                href = model.href,
                name = model.name,
                type = model.type,
                uri = model.uri.uri,
                trackNumber = model.trackNumber,
                discNumber = model.discNumber,
                album = SimpleAlbumDto.fromModel(model.album),
                explicit = model.explicit,
                artists = model.artists.map(SimpleArtistDto::fromModel),
                durationMs = model.durationMs,
                isLocal = model.isLocal ?: false,
                isPlayable = model.isPlayable,
                popularity = model.popularity
            )
    }
}

