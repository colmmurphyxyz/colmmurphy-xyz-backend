package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import xyz.colmmurphy.colmmurphyxyzbackend.AppConfiguration
import kotlin.random.Random

@Service
class SpotifyService(private val appConfiguration: AppConfiguration) : ISpotifyService {

    private lateinit var api: SpotifyClientApi

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        val url: String = getSpotifyAuthorizationUrl(
            SpotifyScope.PlaylistReadPrivate,
            SpotifyScope.PlaylistModifyPrivate,
            SpotifyScope.UserFollowRead,
            SpotifyScope.UserLibraryModify,
            SpotifyScope.UserTopRead,
            clientId = appConfiguration.clientId!!,
            redirectUri = appConfiguration.redirectUri!!,
        )

        log.info("Spotify auth URL: $url")
    }

    override suspend fun createClientApi(authCode: String): Result<String> {
        api = spotifyClientApi(
            clientId = "8226dbf8578543c4bf3b904649d437f3",
            clientSecret = "efbb76a94ba84a68badc70ac58d1f605",
            redirectUri = "http://localhost:8080/spotifycallback",
            authorization = SpotifyUserAuthorization(authorizationCode = authCode)
        ).build()
        log.info("Created spotify client")
        return Result.success("Created spotify client. Token expires in ${api.token.expiresIn} seconds")
    }

    override fun getRandomNumber(): Int = Random(System.currentTimeMillis()).nextInt()

    override suspend fun getTopTracks(): List<String> {
        val foo = api.personalization.getTopTracks(limit = 5).items.map { it.name }
        log.info(foo.joinToString("\n"))
        return foo
    }
}