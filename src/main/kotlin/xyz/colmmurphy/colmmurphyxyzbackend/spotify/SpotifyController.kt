package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SpotifyController(
    @Autowired private val service: ISpotifyService
) {
    @GetMapping("/api/randomNumber")
    fun getRandomNumber(): ResponseEntity<String> {
        return ResponseEntity.ok(service.getRandomNumber().toString())
    }
}