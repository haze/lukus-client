package pw.haze.client

import java.io.File
import java.nio.file.Files
import java.util.*


data class ClientInformation(val name: String, val authors: Array<String>, val version: Double)

/**
 * |> Author: haze
 * |> Since: 4/7/16
 */
class Client {

    companion object {
        private var inst: Optional<Client> = Optional.empty()

        @JvmStatic fun getInstance(): Client {
            if (!inst.isPresent)
                inst = Optional.of(Client())
            return inst.get()
        }
    }

    constructor() {

    }

    fun startup() {

        if(!folder.exists())
            folder.mkdirs()

        val detailsFile = File(folder, "details.txt")
        if(detailsFile.exists()){
            val line: List<String> = Files.readAllLines(detailsFile.toPath())[0].split(":") // TODO: Less skiddy
            login(line[0], line[1]);
        }

    }

    val info: ClientInformation
        get() = ClientInformation("Anxiety", arrayOf("Haze"), 1.0)

}