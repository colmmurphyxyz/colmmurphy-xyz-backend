package xyz.colmmurphy.colmmurphyxyzbackend.ntfy

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "ntfy")
data class NtfyConfiguration(
    @param:Value("api-key")
    var apiKey: String,
    @param:Value("topic-name")
    var topicName: String
)
