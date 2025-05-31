package me.koendev

import de.thelooter.toml.Toml
import java.io.File

data class GeneralConfig(
    val unitType: String,
    val allowZeist: Boolean,
    val smoking: Int,
    val pets: Int
)

data class GenderConfig(
    val male: Boolean,
    val female: Boolean,
    val none: Boolean
)

data class Config(
    val general: GeneralConfig,
    val gender: GenderConfig,
)

val config: Config = Toml().read(File("config.toml")).to(Config::class.java)