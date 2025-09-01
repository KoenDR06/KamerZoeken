package me.koendev

import io.github.cdimascio.dotenv.Dotenv
import me.koendev.utils.println
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val dotEnv = Dotenv.load()

data class ReactableOffer(
    val room: Room,
    val offer: Offer,
    val floor: Floor
)

fun main() {
    val sessionToken = auth()

    print("Getting rooms")
    val rooms = getRooms().filter { room ->
        room.unitType == config.general.unitType
    }
    if (rooms.isEmpty()) {
        System.err.println("No suitable offers were found, quitting.")
        return
    }

    val offers = getOffers(rooms.map { it.wocasId }).offers.filter { offer ->
        offer.adres[0].plaats in listOf(
            "UTRECHT",
        ) + if (config.general.allowZeist) "ZEIST" else ""
    }

    print("\rFiltering on personal filters")
    var index = 0
    val coupled = rooms.mapNotNull { room ->
        val offer: Offer? = offers.find { room.wocasId.toInt() == it.eenheidNummer.toInt() }

        print("\rFiltering per room: ${index++} / ${rooms.size}")
        if (offer == null) null else ReactableOffer(room, offer, getFloorInfo(room, sessionToken))
    }.filter {
        val gender = it.floor.floorInfo.genderPreference

        val date = it.room.expireBy.take(10)
        val date1 = LocalDate.now()
        val date2 = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val daysLeft = ChronoUnit.DAYS.between(date1, date2)

        val smoking = it.floor.floorInfo.smokingAllowed ?: true
        val pets = it.floor.floorInfo.petsAllowed ?: false

        ((gender == "female" && config.gender.female) ||
        (gender == "male" && config.gender.male) ||
        (gender == "none" && config.gender.none)) &&

        ((config.general.smoking == -1 && !smoking) || (config.general.smoking == 1 && smoking) || config.general.smoking == 0) &&

        ((config.general.pets == -1 && !pets) || (config.general.pets == 1 && pets) || config.general.pets == 0) &&

        daysLeft == 0L
    }

    val fileName = "offers.md"
    val out = File(fileName)
    if (!out.exists()) out.createNewFile()

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

        str.append("| Roken       | ${(if (it.floor.floorInfo.smokingAllowed ?: true) "✅ Mag" else "❌ Mag niet").padEnd(17, ' ')} |\n")
        str.append("| Huisdieren  | ${(if (it.floor.floorInfo.petsAllowed ?: false) "✅ Mogen" else "❌ Mogen niet").padEnd(17, ' ')} |\n")
        val positionString = "${it.floor.potentialPosition} / ${it.floor.applicantCount}."
        str.append("| Reacties    | ${positionString.padEnd(18, ' ')} |\n")

        val date = it.room.expireBy.take(10)
        val date1 = LocalDate.now()
        val date2 = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val daysLeft = ChronoUnit.DAYS.between(date1, date2)
        str.append("| Tijd over   | $daysLeft dagen over.      |\n")

        str.append("\n")

        val etage = getEtage(it.offer.assSubjectPersk.first { it.pkHeeftAsp == 52.0 }.waarde!!)

        if (etage != null) str.append("![Foto](${etage.photos[0].etagePhoto[0].url})\n\n")
        str.append("### Message: \n\n${it.floor.floorInfo.description ?: "Deze pannekoeken hebben geen bericht achtergelaten"}\n")

        str.append("\n\n")
    }
    out.writeText(str.toString())
    println("\r${coupled.size} offers found, wrote to $fileName")
}
