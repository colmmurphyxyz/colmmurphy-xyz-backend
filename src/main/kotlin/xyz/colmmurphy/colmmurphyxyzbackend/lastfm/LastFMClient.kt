package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class LastFMClient(
    private val config: LastFMConfiguration,
) {
    private val webClient = WebClient.builder()
        .baseUrl("https://ws.audioscrobbler.com")
        .build()

    fun getRecentTracks(limit: Int): GetRecentTracksResponse =
        webClient.get().uri { uriBuilder ->
            uriBuilder.path("/2.0/")
                .queryParam("method", "user.getRecentTracks")
                .queryParam("user", config.username)
                .queryParam("api_key", config.apiKey)
                .queryParam("format", "json")
                .queryParam("limit", limit).build()
        }
        .retrieve()
        .bodyToMono(GetRecentTracksResponse::class.java)
        .block()!!
}