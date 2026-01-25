package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import org.springframework.stereotype.Service

@Service
class FastFetchService : IFastFetchService {
    override suspend fun getFastFetchLogo(): String {
        val process = ProcessBuilder("script", "-I", "/dev/null", "-q", "-c", "fastfetch --structure logo")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use {
            it.readText()
        }
        process.waitFor()
        return output.substringBefore("\u001b[1G")
    }

    override suspend fun getFastFetchText(): String {
        val process = ProcessBuilder("script", "-I", "/dev/null", "-q", "-c", "fastfetch --logo none")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use {
            it.readText()
        }
        process.waitFor()
        return output
    }
}
