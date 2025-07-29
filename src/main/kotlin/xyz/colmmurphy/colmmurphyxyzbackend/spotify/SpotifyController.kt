package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"])
@RestController
class SpotifyController(
    @Autowired private val service: ISpotifyService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var cachedCurrentlyPlayingTrack: TrackDto? = null
    private var cachedTenRecentTracks: List<PlayHistoryDto>? = null

    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected suspend fun updateCurrentlyPlayingCache() {
        cachedCurrentlyPlayingTrack = null
        service.getCurrentlyPlayingTrack()?.let {
            log.info("updated cached value for /currentlyplaying")
            cachedCurrentlyPlayingTrack = it
        }
    }

    @Scheduled(fixedRate = 2 * 60 * 1000, initialDelay = 60 * 1000)
    protected suspend fun updateTenRecentTracks() {
        cachedTenRecentTracks = null
        val tracks = service.getRecentTracks(10)
        cachedTenRecentTracks = tracks
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
        if (limit == 10 && cachedTenRecentTracks != null) {
            log.info("Returning cached value for /recenttracks")
            return ResponseEntity.ok(cachedTenRecentTracks)
        }
        // fall back if no cached value
        val tracks = service.getRecentTracks(limit)
        return ResponseEntity.ok(tracks)
    }

    @GetMapping("/api/spotify/currentlyplaying")
    suspend fun getCurrentlyPlayingTrack(): ResponseEntity<TrackDto?> {
        return ResponseEntity.ok(cachedCurrentlyPlayingTrack)
    }
}