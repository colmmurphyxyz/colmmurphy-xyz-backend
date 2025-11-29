package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class FastFetchService : IFastFetchService {
    override suspend fun getFastFetch(): CharArray = runBlocking {
        val output = CharArray(4096)
        val process = ProcessBuilder("script", "-g", "-c", "fastfetch")
            .redirectErrorStream(true)
            .start()

        process.inputStream.bufferedReader().use {
            it.read(output)
        }
        process.waitFor()

        output
    }
}