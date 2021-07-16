val libgui_version = "4.1.1+1.17.1-rc1"
val pal_version = "1.3.0-nightly.1.17-rc1"

repositories {
    maven { url = uri("https://ladysnake.jfrog.io/artifactory/mods") }
    maven { url = uri("https://server.bbkr.space/artifactory/libs-release") }
}

dependencies {
    api(project(":base"))
    api(project(":vanilla-events"))

    modImplementation("io.github.ladysnake:PlayerAbilityLib:$pal_version")
    include("io.github.ladysnake:PlayerAbilityLib:$pal_version")

    modImplementation("io.github.cottonmc:LibGui:$libgui_version")
}
