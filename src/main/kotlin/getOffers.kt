package me.koendev

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.koendev.utils.println
import java.net.http.HttpClient
import java.net.http.HttpResponse

@Serializable
data class Offers(
    @SerialName("value") val offers: List<Offer>,
    @SerialName("@odata.count") val count: Int,
    @SerialName("isComplete") val isComplete: Boolean
)

@Serializable
data class Offer(
    @SerialName("adrheeftenh") val adrHeeftEnh: Double,
    @SerialName("eenheidnummer") val eenheidNummer: Double,
    @SerialName("object") val `object`: Double,
    @SerialName("soort_product") val soortProduct: String,
    @SerialName("ASSSUBJECTPERSK_H") val assSubjectPersk: List<AssSubjectPersk>,
    @SerialName("ASP_TOTAAL_C") val aspTotaal: List<AspTotaal>,
    @SerialName("HUUROVEREENKOMST_H") val huurOvereenkomst: List<HuurOvereenkomst>,
    @SerialName("EENHEID_HUUR") val eenheidHuur: List<EenheidHuur>,
    @SerialName("ADRES_H") val adres: List<Adres>
)

@Serializable
data class AssSubjectPersk(
    @SerialName("object") val `object`: Double,
    @SerialName("pkheeftasp") val pkHeeftAsp: Double,
    @SerialName("enhheeftkenm") val enhHeeftKenm: Double,
    @SerialName("kenwaarde") val kenwaarde: String?,
    @SerialName("waarde") val waarde: String?,
    @SerialName("KENMERK_H") val kenmerk: List<Kenmerk>
)

@Serializable
data class Kenmerk(
    @SerialName("object") val `object`: Double,
    @SerialName("code") val code: String
)

@Serializable
data class AspTotaal(
    @SerialName("eenheidnummer") val eenheidNummer: Double,
    @SerialName("totopp") val totopp: Double,
    @SerialName("totoppgem") val totoppgem: Double
)

@Serializable
data class HuurOvereenkomst(
    @SerialName("object") val `object`: Double,
    @SerialName("dcnnummer") val dcnnummer: Double,
    @SerialName("einddatum") val einddatum: String,
    @SerialName("enhheefthuu") val enhheefthuu: Double
)

@Serializable
data class EenheidHuur(
    @SerialName("enh_object") val enhObject: Double,
    @SerialName("eenheidnummer") val eenheidnummer: Double,
    @SerialName("enh_nettohuur") val nettoHuur: Double,
    @SerialName("bruto_huur") val brutoHuur: Double,
    @SerialName("contract_type") val contractType: String
)

@Serializable
data class Adres(
    @SerialName("oid") val oid: Double,
    @SerialName("straatnaam") val straatnaam: String,
    @SerialName("plaats") val plaats: String,
    @SerialName("nummer") val nummer: String,
    @SerialName("letter") val letter: String?,
    @SerialName("toevoeging") val toevoeging: String?,
    @SerialName("postcode") val postcode: String,
    @SerialName("aanduiding") val aanduiding: String?,
    @SerialName("locatie") val locatie: String?
)


fun getOffers(ids: List<String>): Offers {
    val offerFilter = ids.joinToString("%20or%20") { "eenheidnummer%20eq%20$it" }

    val response = getEndpoint("OData-mv?EENHEID_H?\$filter=${offerFilter}&\$expand=ADRES_H!(\$select=plaats,postcode,straatnaam,nummer,toevoeging,aanduiding,locatie,letter,oid),EENHEID_HUUR!(\$select=eenheidnummer,bruto_huur,enh_nettohuur,enh_object,contract_type),HUUROVEREENKOMST_H!(\$select=dcnnummer,einddatum,enhheefthuu),ASP_TOTAAL_C!(\$select=eenheidnummer,totopp,totoppgem),ASSSUBJECTPERSK_H!(\$select=enhheeftkenm,pkheeftasp,waarde,kenwaarde;\$expand=KENMERK_H!(\$select=code))&\$select=eenheidnummer,adrheeftenh,object,soort_product")

    return Json.decodeFromString<Offers>(response)
}