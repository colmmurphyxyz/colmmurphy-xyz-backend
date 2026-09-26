package xyz.colmmurphy.colmmurphyxyzbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class ColmmurphyxyzBackendApplication

@Configuration
@EnableScheduling
class SpringConfig

fun main(args: Array<String>) {
    runApplication<ColmmurphyxyzBackendApplication>(*args)
}
