package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SpotifyController(
    @Autowired private val service: ISpotifyService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/randomNumber")
    fun getRandomNumber(): ResponseEntity<String> {
        return ResponseEntity.ok(service.getRandomNumber().toString())
    }

    @GetMapping("/spotifycallback")
    suspend fun spotifyCallback(@RequestParam("code") code: String): ResponseEntity<String> {
        log.info("Received callback with $code")
        val res = service.createClientApi(code)
        return when (res.isSuccess) {
            true -> ResponseEntity.ok(res.getOrThrow())
            false -> ResponseEntity.badRequest().body(res.exceptionOrNull()?.message)
        }
    }

    @GetMapping("/api/toptracks")
    suspend fun getTopTracks(): ResponseEntity<List<String>> {
        val tracks = service.getTopTracks()
        return ResponseEntity.ok(tracks)
    }
}