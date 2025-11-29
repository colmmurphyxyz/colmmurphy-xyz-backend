package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class FastFetchService : IFastFetchService {
    override suspend fun getFastFetch(): String {
        val process = ProcessBuilder("script", "-q", "-c", "fastfetch")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use {
            it.readText()
        }
        process.waitFor()
        println(output)
        return output
    }
}