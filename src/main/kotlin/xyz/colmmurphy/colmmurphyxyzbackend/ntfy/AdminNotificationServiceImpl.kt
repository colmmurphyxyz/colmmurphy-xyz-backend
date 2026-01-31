package xyz.colmmurphy.colmmurphyxyzbackend.ntfy

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.URI

@Service
class AdminNotificationServiceImpl(private val config: NtfyConfiguration) : AdminNotificationService {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val restClient = RestClient
        .builder()
        .baseUrl(URI.create("https://ntfy.sh/"))
        .defaultHeader("Authorization", "Bearer ${config.apiKey}")
        .build()

    override fun sendNotification(body: String): Result<Unit> {
        log.info("Sending notification with body: {}.", body)
        val response = restClient
            .post()
            .uri(config.topicName)
            .body(body)
            .retrieve()
            .body(String::class.java)
        log.info("Ntfy: {}", response)
        return Result.success(Unit)
    }
}
