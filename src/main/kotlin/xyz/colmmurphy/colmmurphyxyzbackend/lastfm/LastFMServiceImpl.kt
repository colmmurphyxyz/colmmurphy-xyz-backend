package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

import org.springframework.stereotype.Service

@Service
class LastFMServiceImpl(
    private val client: LastFMClient,
) : LastFMService {
    override suspend fun getRecentTracks(limit: Int): RecentTracksResponseDto? {
        val response = client.getRecentTracks(limit)

        return mapToResponseDto(response)
    }

    private fun mapToResponseDto(model: GetRecentTracksResponse): RecentTracksResponseDto =
        RecentTracksResponseDto(
            model.recentTracks.tracks.map { it ->
                LastFMRecentTrackDto(
                    artistName = it.artist.text,
                    trackName = it.name,
                    albumName = it.album.text,
                    images =
                        it.images.map { imageModel ->
                            LastFMImageDto(
                                size = imageModel.size,
                                url = imageModel.url,
                            )
                        },
                )
            },
        )
}
