package xyz.colmmurphy.colmmurphyxyzbackend.spotify

import com.adamratzman.spotify.models.Track

data class PlayHistory(
    val playedAt: String,
    val track: Track
)
