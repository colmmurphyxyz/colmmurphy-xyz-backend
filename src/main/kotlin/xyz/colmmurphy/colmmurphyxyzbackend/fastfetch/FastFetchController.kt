package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"])
@RestController
class FastFetchController(
    @Autowired private val service: FastFetchService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/fastfetch")
    suspend fun fastfetch(): ResponseEntity<String> {
        log.info("GET /api/fastfetch/")
        val ffText = service.getFastFetch()
        val parser = FastFetchParser()

        val html = parser.parseFastFetch(ffText)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
            .body(html)
    }
}
