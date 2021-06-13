val libgui_version = "4.0.0-beta.4+1.17-rc1"
val pal_version = "1.3.0-nightly.1.17-rc1"
val safer_version = "1.1.0"

repositories {
    maven { url = uri("https://server.bbkr.space/artifactory/libs-release") }
    maven { url = uri("https://ladysnake.jfrog.io/artifactory/mods") }
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":vanilla-events"))

    modImplementation("io.github.cottonmc:LibGui:$libgui_version")
    include("io.github.cottonmc:LibGui:$libgui_version")

    api("com.github.NathanPB:Safer:$safer_version")
    include("com.github.NathanPB:Safer:$safer_version")

    modImplementation("io.github.ladysnake:PlayerAbilityLib:$pal_version")
    include("io.github.ladysnake:PlayerAbilityLib:$pal_version")
}
