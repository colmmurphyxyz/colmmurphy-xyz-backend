package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.div
import kotlinx.html.stream.createHTML

import org.slf4j.LoggerFactory
import xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi.AnsiParser

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        return createHTML(prettyPrint = false).div {
            textSegmentsToHtml(AnsiParser(input).parseAnsiText())()
        }
    }
}