package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.CurrentlyPlayingType
import com.adamratzman.spotify.models.Track
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.colmmurphy.colmmurphyxyzbackend.AppConfiguration

@Service
class SpotifyService(private val appConfiguration: AppConfiguration) : ISpotifyService {

    private lateinit var api: SpotifyClientApi

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        val url: String = getSpotifyAuthorizationUrl(
            SpotifyScope.UserTopRead,
            SpotifyScope.UserReadRecentlyPlayed,
            SpotifyScope.UserReadCurrentlyPlaying,
            clientId = appConfiguration.clientId!!,
            redirectUri = appConfiguration.redirectUri!!,
        )

        log.info("Spotify auth URL: $url")
    }

    override fun getStatus(): SpotifyStatusResponseEntity {
        if (!::api.isInitialized) {
            return SpotifyStatusResponseEntity(
                available = false,
                reason = "Spotify client not initialized"
            )
        }
        if (api.token.shouldRefresh()) {
            return SpotifyStatusResponseEntity(
                available = false,
                reason = "Spotify token expired"
            )
        }
        return SpotifyStatusResponseEntity(true)
    }

    override suspend fun createClientApi(authCode: String): Result<String> {
        api = spotifyClientApi(
            clientId = appConfiguration.clientId,
            clientSecret = appConfiguration.clientSecret,
            redirectUri = appConfiguration.redirectUri,
            authorization = SpotifyUserAuthorization(authorizationCode = authCode)
        ).build()
        log.info("Created spotify client")
        return Result.success("Created spotify client. Token expires in ${api.token.expiresIn} seconds")
    }

    override suspend fun getTopTracks(): List<String> {
        val foo = api.personalization.getTopTracks(limit = 5).items.map { it.name }
        log.info(foo.joinToString("\n"))
        return foo
    }

    override suspend fun getRecentTracks(limit: Int): List<PlayHistory> {
        val response = api.player.getRecentlyPlayed(limit)
        return response.items.map { PlayHistory(it.playedAt, it.track) }
    }

    override suspend fun getCurrentlyPlayingTrack(): Track? {
        val response = api.player.getCurrentlyPlaying(additionalTypes = listOf(CurrentlyPlayingType.Track))
        if (response?.currentlyPlayingType != CurrentlyPlayingType.Track) {
            return null
        }
        return response.item as Track?
    }
}