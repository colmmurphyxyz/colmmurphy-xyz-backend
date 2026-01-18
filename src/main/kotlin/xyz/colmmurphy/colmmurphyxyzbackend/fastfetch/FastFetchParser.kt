package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.style

import org.slf4j.LoggerFactory
import xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi.AnsiParser

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        val (logoRaw, infoRaw) = input.split("\u001B[?7l")

        return createHTML(prettyPrint = false).div {
            div {
                textSegmentsToHtml(AnsiParser(logoRaw).parseAnsiText())()
            }
            div {
                textSegmentsToHtml(AnsiParser(infoRaw).parseAnsiText())()
            }
        }
    }
}