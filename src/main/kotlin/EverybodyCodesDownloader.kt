import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class UserMe(val seed: Int)

class EverybodyCodesDownloader(
    private val event: String,
    private val quest: Int,
    private val part: Int
) {
    private val questStr = "%02d".format(quest)
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val cookie = loadCookie()
    private val baseDir = File("src/main/resources/inputfiles").apply { mkdirs() }

    /** Only ensure seed + encrypted input exist (no decryption). */
    fun download() {
        val seed: Int = getCachedSeed()
        getCachedInputs(seed)
        println("📥 Download complete for quest $questStr (encrypted only).")
    }

    /** Force re-download of seed + encrypted input, overwriting cache. */
    fun forceDownload() {
        val seedFile = File(baseDir, "UserMe.json")
        val userMe = fetchSeed()
        seedFile.writeText(json.encodeToString(userMe))
        println("⬇️ Overwrote seed: ${seedFile.name}")

        val encFile = File(baseDir, "everybody_codes_e${event}_q${questStr}_encrypted.json")
        val encJson = fetchInputs(userMe.seed)
        encFile.writeText(encJson)
        println("⬇️ Overwrote encrypted input: ${encFile.name}")
    }

    /** Decrypt and return input text for the configured quest/part. */
    fun getInput(): String {
        val outputFile = File(baseDir, "everybody_codes_e${event}_q${questStr}_p${part}.txt")
        if (outputFile.exists()) {
            println("✅ Using cached decrypted file: ${outputFile.name}")
            return outputFile.readText()
        }

        val seed: Int = getCachedSeed()
        val inputs: JsonObject = getCachedInputs(seed)
        val questInfo: JsonObject = getQuestInfoJson()

        val encrypted = inputs.requireString(part.toString()) {
            "Missing encrypted input for part $part of quest $questStr."
        }
        val key = questInfo.requireString("key$part") {
            "No key found for part $part of quest $questStr — this part may not be unlocked yet."
        }

        println("🔓 Decrypting part $part of quest $questStr...")
        val decrypted = decrypt(key, encrypted)
        outputFile.writeText(decrypted)
        println("💾 Saved decrypted file: ${outputFile.name}")
        return decrypted
    }

    // --- caching helpers ---
    private fun getCachedSeed(): Int {
        val seedFile = File(baseDir, "UserMe.json")
        if (seedFile.exists()) {
            println("✅ Using cached seed from ${seedFile.name}")
            val cached = json.decodeFromString<UserMe>(seedFile.readText())
            return cached.seed
        }
        println("🌐 Fetching new seed from API...")
        val fetched = fetchSeed()
        seedFile.writeText(json.encodeToString(fetched))
        println("💾 Cached seed to ${seedFile.name}")
        return fetched.seed
    }

    private fun getCachedInputs(seed: Int): JsonObject {
        val cacheFile = File(baseDir, "everybody_codes_e${event}_q${questStr}_encrypted.json")
        if (cacheFile.exists()) {
            println("✅ Using cached encrypted input: ${cacheFile.name}")
            return json.parseToJsonElement(cacheFile.readText()).jsonObject
        }
        println("🌐 Downloading encrypted input for quest $questStr...")
        val fetched = fetchInputs(seed)
        cacheFile.writeText(fetched)
        println("💾 Cached encrypted input: ${cacheFile.name}")
        return json.parseToJsonElement(fetched).jsonObject
    }

    // --- network methods ---
    private fun fetchSeed(): UserMe {
        val jsonText = getJson("https://everybody.codes/api/user/me")
        return json.decodeFromString(jsonText)
    }

    private fun fetchInputs(seed: Int): String {
        val url = "https://everybody-codes.b-cdn.net/assets/$event/$quest/input/$seed.json"
        return getJson(url)
    }

    private fun getQuestInfoJson(): JsonObject {
        val url = "https://everybody.codes/api/event/$event/quest/$quest"
        val jsonText = getJson(url)
        return json.parseToJsonElement(jsonText).jsonObject
    }

    private fun getJson(urlStr: String): String {
        val conn = URI(urlStr).toURL().openConnection() as HttpURLConnection
        conn.setRequestProperty("Cookie", cookie)
        conn.inputStream.bufferedReader().use { return it.readText() }
    }

    // --- helpers ---
    private fun loadCookie(): String {
        val props = Properties()
        val gradleProps = File(System.getProperty("user.home"), ".gradle/gradle.properties")
        props.load(gradleProps.inputStream())
        val session = props.getProperty("everybody-codes-session-cookie")
            ?: error("Missing property 'everybody-codes-session-cookie' in ~/.gradle/gradle.properties")
        return "everybody-codes=$session"
    }

    private fun JsonObject.requireString(name: String, errorMessage: () -> String): String {
        val value = this[name]?.jsonPrimitive?.content
        if (value.isNullOrBlank()) throw IllegalStateException(errorMessage())
        return value
    }

    private fun decrypt(key: String, encryptedHex: String): String {
        val encryptedBytes = encryptedHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
        val ivBytes = key.substring(0, 16).toByteArray(StandardCharsets.UTF_8)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keyBytes, "AES"), IvParameterSpec(ivBytes))
        return String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8)
    }
}

fun main() {
    val downloader = EverybodyCodesDownloader("2025", 1, 1)
    downloader.forceDownload()   // overwrite seed + encrypted input
    println(downloader.getInput())
}
