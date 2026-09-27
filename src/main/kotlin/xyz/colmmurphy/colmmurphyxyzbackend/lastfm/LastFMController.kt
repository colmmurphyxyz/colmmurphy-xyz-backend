package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@CrossOrigin(origins = ["*"])
@RestController
class LastFMController(
    @Autowired private val service: LastFMService
) {
    @GetMapping("/api/lastfm/recenttracks")
    suspend fun getRecentTracks(
        @RequestParam("limit") limit: Int,
    ): ResponseEntity<RecentTracksResponseDto> {
        val response = service.getRecentTracks(limit) ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
            .body(response)

    }
}
