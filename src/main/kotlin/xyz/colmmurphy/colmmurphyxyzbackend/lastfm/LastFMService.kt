package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

interface LastFMService {
    suspend fun getRecentTracks(limit: Int): RecentTracksResponseDto?
}