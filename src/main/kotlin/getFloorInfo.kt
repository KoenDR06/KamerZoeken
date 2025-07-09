package me.koendev

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FloorInfo(
    @SerialName("WocasId") val wocasId: String,
    @SerialName("Description") val description: String?,
    @SerialName("HospiteerDate") val hospiteerDate: String?,
    @SerialName("PreferenceSmokingAllowed") val smokingAllowed: Boolean?,
    @SerialName("PreferencePetsAllowed") val petsAllowed: Boolean,
    @SerialName("PreferenceGender") val genderPreference: String,
    @SerialName("NumberOfUnits") val numberOfUnits: Int,
)

@Serializable
data class Floor(
    @SerialName("ExpireBy") val expireBy: String,
    @SerialName("RemainingTime") val remainingTime: String,
    @SerialName("ApplicantCount") val applicantCount: Int,
    @SerialName("CurrentPosition") val currentPosition: Int,
    @SerialName("CurrentAdjustedPosition") val currentAdjustedPosition: Int,
    @SerialName("PotentialPosition") val potentialPosition: Int,
    @SerialName("ViewingDate") val viewingDate: String,

    @SerialName("FloorInformation") val floorInfo: FloorInfo,

    @SerialName("SubsidiabeleHuur") val subsidiabeleHuur: Double,
    @SerialName("BruttoHuur") val brutoHuur: Double,
    @SerialName("IsPublished") val isPublished: Boolean,
    @SerialName("PublishedOn") val publishedOn: String?,
    @SerialName("ContractStartDate") val contractStartDate: String,
    @SerialName("UnitType") val unitType: String,
    @SerialName("NumberOfRooms") val numberOfRooms: Int,
    @SerialName("Image") val image: String?,
    @SerialName("ContractType") val contractType: String,
    @SerialName("FlowId") val flowId: Int,
    @SerialName("WocasId") val wocasId: String,
    @SerialName("Kind") val kind: String,
)

fun getFloorInfo(room: Room): Floor {
    val response = getEndpoint("offer/${room.flowId}")

    return Json.decodeFromString<Floor>(response)
}
