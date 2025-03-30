package xyz.colmmurphy.colmmurphyxyzbackend.spotify

interface ISpotifyService {
    fun getRandomNumber(): Int
    suspend fun createClientApi(authCode: String): Result<String>
    suspend fun getTopTracks(): List<String>
}