package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.models.Track
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
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
        val tracks = service.getRecentTracks(limit)
        return ResponseEntity.ok(tracks)
    }

    @GetMapping("/api/spotify/currentlyplaying")
    suspend fun getCurrentlyPlayingTrack(): ResponseEntity<TrackDto?> {
        val track = service.getCurrentlyPlayingTrack()
        return ResponseEntity.ok(track)
    }
}