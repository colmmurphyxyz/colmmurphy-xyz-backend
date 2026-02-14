package xyz.colmmurphy.colmmurphyxyzbackend.visits

data class Visit(
    val ip: String,
    val userAgent: String,
    val url: String,
    val fingerprint: String,
    val timestamp: Long
)
