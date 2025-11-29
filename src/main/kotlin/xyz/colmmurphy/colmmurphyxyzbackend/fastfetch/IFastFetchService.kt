package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

interface IFastFetchService {
    suspend fun getFastFetch(): String

}