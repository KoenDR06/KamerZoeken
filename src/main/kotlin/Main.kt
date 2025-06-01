package me.koendev

import java.io.File

data class ReactableOffer(
    val room: Room,
    val offer: Offer,
    val floor: Floor
)

fun main() {
    val rooms = getRooms().filter { room ->
        room.unitType == config.general.unitType
    }
    val offers = getOffers(rooms.map { it.wocasId }).offers.filter { offer ->
        offer.adres[0].plaats !in listOf(
            "ZWOLLE",
            "GRONINGEN",
            "TILBURG"
        ) + if (!config.general.allowZeist) "ZEIST" else ""
    }

    val coupled = rooms.mapNotNull { room ->
        val offer: Offer? = offers.find { room.wocasId.toInt() == it.eenheidNummer.toInt() }

        if (offer == null) null else ReactableOffer(room, offer, getFloorInfo(room))
    }.filter {
        val gender = it.floor.floorInfo.genderPreference

        ((gender == "female" && config.gender.female) ||
        (gender == "male" && config.gender.male) ||
        (gender == "none" && config.gender.none)) &&

        ((config.general.smoking == -1 && !it.floor.floorInfo.smokingAllowed) || (config.general.smoking == 1 && it.floor.floorInfo.smokingAllowed) || config.general.smoking == 0) &&

        ((config.general.pets == -1 && !it.floor.floorInfo.petsAllowed) || (config.general.pets == 1 && it.floor.floorInfo.petsAllowed) || config.general.pets == 0)
    }

    val fileName = "offers.md"
    val out = File(fileName)

    val str = StringBuilder()

    coupled.forEach {
        val address = it.offer.adres[0]

        str.append("## [${address.straatnaam} ${address.nummer}, ${address.plaats.lowercase().capitalize()}](https://sshxl.nl/nl/aanbod/${it.room.flowId}-${address.straatnaam.lowercase().replace(" ", "-")})\n")

        str.append("\n| Categorie | Waarde |\n")
        str.append("|-----------|--------|\n")

        str.append("| Huisgenoten | ${it.room.numberOfRooms-1} |\n")

        val genderString = when (it.floor.floorInfo.genderPreference) {
            "none" -> "Geen voorkeur"
            "male" -> "Man"
            "female" -> "Vrouw"
            else -> it.floor.floorInfo.genderPreference
        }
        str.append("| Geslacht | $genderString |\n")


        str.append("| Roken | ${if (it.floor.floorInfo.smokingAllowed) "✅ Mag" else "❌ Mag niet"} |\n")
        str.append("| Huisdieren | ${if (it.floor.floorInfo.petsAllowed) "✅ Mogen" else "❌ Mogen niet"} |\n")

        str.append("\n")
        str.append("### Message: \n\n${it.floor.floorInfo.description}\n")

        str.append("\n\n")
    }
    out.writeText(str.toString())
    println("${coupled.size} offers found, wrote to $fileName")
}
