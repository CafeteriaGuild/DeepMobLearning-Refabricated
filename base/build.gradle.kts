val rei_version = "6.0.247-alpha"
val libgui_version = "4.0.0-beta.4+1.17-rc1"
val ktdatataglib_version = "1.5.2"
val pal_version = "1.3.0-nightly.1.17-rc1"
val safer_version = "1.1.0"

repositories {
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://server.bbkr.space/artifactory/libs-release") }
    maven { url = uri("https://ladysnake.jfrog.io/artifactory/mods") }
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:$rei_version")

    modImplementation("io.github.cottonmc:LibGui:$libgui_version")
    include("io.github.cottonmc:LibGui:$libgui_version")

    modApi("com.github.NathanPB:KtDataTagLib:$ktdatataglib_version")
    include("com.github.NathanPB:KtDataTagLib:$ktdatataglib_version")

    api("com.github.NathanPB:Safer:$safer_version")
    include("com.github.NathanPB:Safer:$safer_version")

    modImplementation("io.github.ladysnake:PlayerAbilityLib:$pal_version")
    include("io.github.ladysnake:PlayerAbilityLib:$pal_version")
}
