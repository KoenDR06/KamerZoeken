package me.koendev

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.koendev.utils.println

@Serializable
data class Etages(
    @SerialName("value") val offers: List<Etage>,
    @SerialName("@odata.count") val count: Int,
    @SerialName("isComplete") val isComplete: Boolean
)

@Serializable
data class Etage(
    @SerialName("EtageWocasId") val etageWocasID: String,
    @SerialName("Id") val etageID: Int,
    @SerialName("Etage_EtagePhoto") val photos: List<EtagePhoto>
)

@Serializable
data class EtagePhoto(
    @SerialName("Id") val id: Int,
    @SerialName("EtageId") val etageID: Int,
    @SerialName("EtagePhotoId") val etagePhotoID: Int,
    @SerialName("EtagePhoto") val etagePhoto: List<EtagePhotoItem>,
)

@Serializable
data class EtagePhotoItem(
    @SerialName("Id") val id: Int,
    @SerialName("Photo") val url: String
)

fun getEtage(id: String): Etage? {
    val response = getEndpoint("OData/Etage?\$filter=(EtageWocasId%20eq%20'$id')&\$expand=Etage_EtagePhoto!(\$select=Id,EtagePhotoId,EtageId;\$expand=EtagePhoto!(\$select=Id,Photo))&\$select=Id,EtageWocasId")

    return Json.decodeFromString<Etages>(response).offers.firstOrNull()
}
