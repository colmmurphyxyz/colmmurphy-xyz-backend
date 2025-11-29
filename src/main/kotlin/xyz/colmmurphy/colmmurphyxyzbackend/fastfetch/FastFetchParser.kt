package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import org.slf4j.LoggerFactory

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        val lines = input.lines()
        val separatorIndex = lines.indexOfFirst { it.startsWith("----") }

        val logoRaw = lines.take(separatorIndex).joinToString("\n")
        val infoRaw = lines.drop(separatorIndex + 1).joinToString("\n")

        return "Not Implemented"
    }
}