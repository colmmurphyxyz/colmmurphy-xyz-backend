package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@CrossOrigin(origins = ["*"])
@RestController
class SpotifyController(
    @Autowired private val service: ISpotifyService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var cachedCurrentlyPlayingTrack: TrackDto? = null
    private var cachedRecentTracks: List<PlayHistoryDto>? = null

    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected suspend fun updateCurrentlyPlayingCache() {
        cachedCurrentlyPlayingTrack = null
        service.getCurrentlyPlayingTrack()?.let {
            log.info("updated cached value for /currentlyplaying")
            cachedCurrentlyPlayingTrack = it
        }
    }

    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected suspend fun updateRecentTracks() {
        cachedRecentTracks = null
        val tracks = service.getRecentTracks(50)
        cachedRecentTracks = tracks
        log.info("Updated cached value for /recenttracks")
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
        val responseEntity = ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
        val cache = cachedRecentTracks
        if (cache == null || limit > cache.size) {
            responseEntity.body(service.getRecentTracks(limit))
        } else {
            responseEntity.body(cache.slice(0 until limit))
        }
        return responseEntity.build()
    }

    @GetMapping("/api/spotify/currentlyplaying")
    suspend fun getCurrentlyPlayingTrack(): ResponseEntity<TrackDto?> {
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .body(cachedCurrentlyPlayingTrack)
    }
}