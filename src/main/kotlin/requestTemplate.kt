package me.koendev

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * @param path the endpoint to GET. `sshxl.nl/api/v1/<PATH>`
 *
 * @return a HttpRequest with all the necessary properties to GET the endpoint.
 */
fun buildRequest(path: String): HttpRequest {
    return HttpRequest.newBuilder()
        .uri(URI("https://www.sshxl.nl/api/v1/$path"))
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0")
        .header("Accept", "*/*")
        .header("Accept-Language", "en-US,en;q=0.5")
        .header(
            "Cookie",
            listOf(
                "cookie_consent_analytics=no",
                "cookie_consent=no",
                "SSHContext=${dotEnv["AUTH"]}"
            ).joinToString("; ")
        )
        .GET()
        .build()
}

fun getEndpoint(endpoint: String): String {
    val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    val request = buildRequest(endpoint)

    return client.send(request, HttpResponse.BodyHandlers.ofString()).body()
}