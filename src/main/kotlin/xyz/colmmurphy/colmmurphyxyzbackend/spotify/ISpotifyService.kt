package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.models.Track

interface ISpotifyService {
    suspend fun createClientApi(authCode: String): Result<String>
    fun getStatus(): SpotifyStatusResponseEntity
    suspend fun getTopTracks(): List<String>
    suspend fun getRecentTracks(limit: Int): List<PlayHistoryDto>
    suspend fun getCurrentlyPlayingTrack(): TrackDto?
}