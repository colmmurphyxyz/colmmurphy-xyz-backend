package xyz.colmmurphy.colmmurphyxyzbackend.visits

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.UUID

@Service
class VisitsStore {
    private val mapper = jacksonObjectMapper().writerWithDefaultPrettyPrinter()

    fun saveVisit(visit: Visit) {
        saveToDisk(visit)
    }

    private fun fileName(visit: Visit): String {
        // Add random 'slug' to each file on the off chance
        // that two visits occur with the same timestamps.
        // Keep timestamp before slug so that files are in chronological order
        return "visit-${visit.timestamp}-${UUID.randomUUID()}.json"
    }

    private fun saveToDisk(visit: Visit) {
        val name = fileName(visit)
        val json = mapper.writeValueAsString(visit)
        val directory = Path.of("/data/colmmurphyxyz")
        val filePath = directory.resolve(name)

        if (Files.notExists(directory)) {
            Files.createDirectories(directory)
        }

        Files.writeString(
            filePath,
            json,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}