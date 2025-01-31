package it.fast4x.invidious.models

enum class Instances {
    YEWTU,
    NADEKO,
    JIING,
    DRGNZ;

    val endpoint = "/api/v1/"

    val apiUrl: String
        get() = when (this) {
            YEWTU -> "https://yewtu.be$endpoint"
            NADEKO -> "https://inv.nadeko.net$endpoint"
            JIING -> "https://invidious.jing.rocks$endpoint"
            DRGNZ -> "https://yt.drgnz.club$endpoint"
        }

    val country: String
        get() = when (this) {
            YEWTU -> "DE"
            NADEKO -> "CL"
            JIING -> "JP"
            DRGNZ -> "CZ"
    }

}