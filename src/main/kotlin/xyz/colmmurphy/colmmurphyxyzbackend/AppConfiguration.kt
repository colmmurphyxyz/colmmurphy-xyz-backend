package xyz.colmmurphy.colmmurphyxyzbackend

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "spotify")
data class AppConfiguration(
    var clientId: String? = null,
    var clientSecret: String? = null,
    var redirectUri: String? = null,
)