package me.koendev

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Image(
    @SerialName("FilePathNL") val filePathNL: String?,
    @SerialName("FilePathEN") val filePathEN: String?
)

@Serializable
data class Room(
    @SerialName("ExpireBy") val expireBy: String,
    @SerialName("RemainingTime") val remainingTime: String,
    @SerialName("ApplicantCount") val applicantCount: Int,
    @SerialName("CurrentPosition") val currentPosition: Int,
    @SerialName("CurrentAdjustedPosition") val currentAdjustedPosition: Int,
    @SerialName("PotentialPosition") val potentialPosition: Int,
    @SerialName("ViewingDate") val viewingDate: String,
    @SerialName("FloorInformation") val floorInformation: String?,
    @SerialName("SubsidiabeleHuur") val subsidiabeleHuur: Double,
    @SerialName("BruttoHuur") val brutoHuur: Double,
    @SerialName("IsPublished") val isPublished: Boolean,
    @SerialName("PublishedOn") val publishedOn: String,
    @SerialName("ContractStartDate") val contractStartDate: String,
    @SerialName("UnitType") val unitType: String,
    @SerialName("NumberOfRooms") val numberOfRooms: Int,
    @SerialName("Image") val image: Image?,
    @SerialName("ContractType") val contractType: String,
    @SerialName("FlowId") val flowId: Int,
    @SerialName("WocasId") val wocasId: String,
    @SerialName("Kind") val kind: String,
)

fun getRooms(): List<Room> {
    val response = getEndpoint("offer")

    return Json.decodeFromString<List<Room>>(response)
}
