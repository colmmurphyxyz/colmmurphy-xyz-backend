package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class SpotifyService : ISpotifyService {
    override fun getRandomNumber(): Int = Random(System.currentTimeMillis()).nextInt()
}