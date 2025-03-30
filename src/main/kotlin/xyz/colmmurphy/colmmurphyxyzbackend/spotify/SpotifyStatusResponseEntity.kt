package xyz.colmmurphy.colmmurphyxyzbackend.spotify

data class SpotifyStatusResponseEntity(
    val available: Boolean,
    val reason: String = ""
)
