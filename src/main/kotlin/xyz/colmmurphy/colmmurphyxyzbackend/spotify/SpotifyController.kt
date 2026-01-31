package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.concurrent.TimeUnit

@CrossOrigin(origins = ["*"])
@RestController
class SpotifyController(
    @Autowired private val service: ISpotifyService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Volatile
    private var cachedCurrentlyPlayingTrack: TrackDto? = null

    @Volatile
    private var currentlyPlayingTrackTime = Instant.now().toEpochMilli()

    @Volatile
    private var cachedRecentTracks: List<PlayHistoryDto>? = null

    @OptIn(DelicateCoroutinesApi::class)
    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected fun updateCurrentlyPlayingCache() {
        GlobalScope.launch(Dispatchers.IO) {
            cachedCurrentlyPlayingTrack = null
            service.getCurrentlyPlayingTrack()?.let {
                log.info("updated cached value for /currentlyplaying")
                cachedCurrentlyPlayingTrack = it
                currentlyPlayingTrackTime = Instant.now().toEpochMilli()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected fun updateRecentTracks() {
        GlobalScope.launch(Dispatchers.IO) {
            cachedRecentTracks = null
            val tracks = service.getRecentTracks(50)
            if (tracks != null) {
                cachedRecentTracks = tracks
                log.info("Updated cached value for /recenttracks")
            }
        }
    }

    @GetMapping("/api/spotify/status")
    suspend fun getStatus(): ResponseEntity<SpotifyStatusResponseEntity> {
        log.info("get status")
        val status = service.getStatus()
        return if (status.available) {
            ResponseEntity.ok(status)
        } else {
            ResponseEntity.status(503).body(status)
        }
    }

    @GetMapping("/api/spotify/callback")
    suspend fun spotifyCallback(@RequestParam("code") code: String): ResponseEntity<String> {
        log.info("Received callback with $code")

        val res = service.createClientApi(code)
        return when (res.isSuccess) {
            true -> ResponseEntity.ok(res.getOrThrow())
            false -> ResponseEntity.badRequest().body(res.exceptionOrNull()?.message)
        }
    }

    @GetMapping("/api/spotify/recenttracks")
    suspend fun getRecentTracks(@RequestParam("limit") limit: Int): ResponseEntity<List<PlayHistoryDto>> {
        val cache = cachedRecentTracks
        val tracks = if (cache == null || limit > cache.size) {
            service.getRecentTracks(limit)
        } else cache

        return if (tracks == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .body(
                    tracks.slice(0 until limit)
                )
        }
    }

    @GetMapping("/api/spotify/currentlyplaying")
    suspend fun getCurrentlyPlayingTrack(): ResponseEntity<TrackDto?> {
        val cache = cachedCurrentlyPlayingTrack ?: return ResponseEntity.noContent().build()

        val diff = Instant.now().toEpochMilli() - currentlyPlayingTrackTime
        val isStale = diff > 120_000

        if (isStale) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(cache)
    }
}