package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

interface IFastFetchService {
    suspend fun getFastFetchLogo(): String
    suspend fun getFastFetchText(): String
}
