val libguiVersion: String by project
val reiVersion: String by project
val patchouliVersion: String by project

repositories {
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    maven {
        name = "Patchouli"
        url = uri("https://maven.blamejared.com")
    }
    maven {
        name = "Shedaniel's Maven Repository"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/releases/")
    }
}

dependencies {
    modApi("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")

    modApi("vazkii.patchouli:Patchouli:${patchouliVersion}")

    modApi("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}
