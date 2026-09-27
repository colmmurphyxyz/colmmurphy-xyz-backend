package xyz.colmmurphy.colmmurphyxyzbackend.lastfm

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "lastfm")
class LastFMConfiguration(
    @param:Value("username")
    var username: String,
    @param:Value("apikey")
    var apiKey: String
)