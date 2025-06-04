package me.koendev

import java.io.File

data class ReactableOffer(
    val room: Room,
    val offer: Offer,
    val floor: Floor
)

fun main() {
    val inputFile = File("rooms-scanned.txt")

    val roomsFound = if (inputFile.exists()) inputFile.readLines() else listOf()

    val rooms = getRooms().filter { room ->
        room.unitType == config.general.unitType && room.wocasId !in roomsFound
    }
    if (rooms.isEmpty()) {
        println("No suitable offers were found, quitting.")
        return
    }
    val offers = getOffers(rooms.map { it.wocasId }).offers.filter { offer ->
        offer.adres[0].plaats in listOf(
            "UTRECHT",
        ) + if (config.general.allowZeist) "ZEIST" else ""
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

        str.append("\n| Categorie   | Waarde             |\n")
        str.append("|-------------|--------------------|\n")

        str.append("| Huisgenoten | ${(it.room.numberOfRooms-1).toString().padEnd(18, ' ')} |\n")

        val genderString = when (it.floor.floorInfo.genderPreference) {
            "none" -> "Geen voorkeur"
            "male" -> "Man"
            "female" -> "Vrouw"
            else -> it.floor.floorInfo.genderPreference
        }
        str.append("| Geslacht    | ${genderString.padEnd(18, ' ')} |\n")


        str.append("| Roken       | ${(if (it.floor.floorInfo.smokingAllowed) "✅ Mag" else "❌ Mag niet").padEnd(17, ' ')} |\n")
        str.append("| Huisdieren  | ${(if (it.floor.floorInfo.petsAllowed) "✅ Mogen" else "❌ Mogen niet").padEnd(17, ' ')} |\n")
        str.append("| Reacties    | ${it.floor.applicantCount.toString().padStart(3, ' ')} al gereageerd. |\n")

        str.append("\n")
        str.append("### Message: \n\n${it.floor.floorInfo.description ?: "Deze pannekoeken hebben geen bericht achtergelaten"}\n")

        str.append("\n\n")
    }
    out.writeText(out.readText() + "\n\n" + str.toString())
    inputFile.writeText(roomsFound.joinToString("\n") + "\n" + rooms.joinToString("\n") { it.wocasId })
    println("${coupled.size} offers found, wrote to $fileName")
}
