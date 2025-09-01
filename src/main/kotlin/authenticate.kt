package me.koendev

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import me.koendev.utils.println
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun auth(): String {
    val data: Map<String, String> = mapOf(
        "Email" to dotEnv["EMAIL"],
        "Password" to dotEnv["PASSWORD"],
        "Pin" to "",
        "Role" to ""
    )
    val req = HttpRequest.newBuilder()
        .uri(URI.create("https://www.sshxl.nl/api/portal/ApiLogin"))
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0")
        .header("Accept", "*/*")
        .header("Content-Type", "application/json")
        .header("Accept-Language", "en-US,en;q=0.5")
        .header(
            "Cookie",
            listOf(
                "cookie_consent_analytics=no",
                "cookie_consent=no",
                "SSHContext=${dotEnv["AUTH"]}"
            ).joinToString("; ")
        )
        .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(data)))
        .build()

    val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    val res = client.send(req, HttpResponse.BodyHandlers.ofString())

    if (res.statusCode() != 200) {
        throw Exception("auth failed with status code ${res.statusCode()}")
    }

    return Json.decodeFromString<Auth>(res.body()).session
}

@JsonIgnoreUnknownKeys
@Serializable
private data class Auth(val session: String)