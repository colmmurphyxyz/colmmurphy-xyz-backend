package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.div
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe

import org.slf4j.LoggerFactory

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        val lines = input.lines()
        val separatorIndex = lines.indexOfFirst { it.contains("--------") }

        val logoRaw = lines.take(separatorIndex).joinToString("\n")
        val infoRaw = lines.drop(separatorIndex + 1).joinToString("\n")

        return createHTML(prettyPrint = true).div {
            unsafe { raw(parseLogo(logoRaw)) }
            unsafe { raw(parseInfo(infoRaw)) }
        }
    }

    private fun parseLogo(logoRaw: String): String {

        return createHTML().div {
            p { +"LOGO" }
        }
    }

    private fun parseInfo(infoRaw: String): String {
        return createHTML().div {
            p { +"INFO" }
        }
    }
}