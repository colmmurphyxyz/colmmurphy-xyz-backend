package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.CurrentlyPlayingType
import com.adamratzman.spotify.models.Track
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.colmmurphy.colmmurphyxyzbackend.AppConfiguration
import xyz.colmmurphy.colmmurphyxyzbackend.ntfy.AdminNotificationService

@Service
class SpotifyService(private val appConfiguration: AppConfiguration, private val notificationService: AdminNotificationService) : ISpotifyService {

    private var spotifyApi: SpotifyClientApi? = null

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
        notificationService.sendNotification("Spotify auth URL: $url")
    }

    override fun getStatus(): SpotifyStatusResponseEntity {
        if (spotifyApi == null) {
            return SpotifyStatusResponseEntity(
                available = false,
                reason = "Spotify client not initialized"
            )
        }
        if (spotifyApi!!.token.shouldRefresh()) {
            return SpotifyStatusResponseEntity(
                available = false,
                reason = "Spotify token expired"
            )
        }
        return SpotifyStatusResponseEntity(true)
    }

    override suspend fun createClientApi(authCode: String): Result<String> {
        spotifyApi = spotifyClientApi(
            clientId = appConfiguration.clientId,
            clientSecret = appConfiguration.clientSecret,
            redirectUri = appConfiguration.redirectUri,
            authorization = SpotifyUserAuthorization(authorizationCode = authCode)
        ).build()
        log.info("Created spotify client")
        return Result.success("Created spotify client. Token expires in ${spotifyApi!!.token.expiresIn} seconds")
    }

    override suspend fun getRecentTracks(limit: Int): List<PlayHistoryDto>? {
        if (spotifyApi == null) {
            return null
        }
        val response = spotifyApi!!.player.getRecentlyPlayed(limit)
        return response.items.map { PlayHistoryDto(it.playedAt, TrackDto.fromModel(it.track)) }
    }

    override suspend fun getCurrentlyPlayingTrack(): TrackDto? {
        if (spotifyApi == null) {
            return null
        }
        val response = spotifyApi!!.player.getCurrentlyPlaying(additionalTypes = listOf(CurrentlyPlayingType.Track))
        if (response?.currentlyPlayingType != CurrentlyPlayingType.Track) {
            return null
        }
        val track = response.item as Track? ?: return null
        return TrackDto.fromModel(track)
    }
}