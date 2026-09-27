package xyz.colmmurphy.colmmurphyxyzbackend.spotify

interface ISpotifyService {
    suspend fun createClientApi(authCode: String): Result<String>
    fun getStatus(): SpotifyStatusResponseEntity
    suspend fun getRecentTracks(limit: Int): List<PlayHistoryDto>?
    suspend fun getCurrentlyPlayingTrack(): TrackDto?
}