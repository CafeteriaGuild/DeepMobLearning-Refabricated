val libguiVersion: String by project
val energyVersion: String by project
val clothConfigVersion: String by project
val modmenuVersion: String by project

repositories {
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
        }
    }
    maven {
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroup("com.terraformersmc")
        }
    }
}

dependencies {
    api(project(":vanilla-events", configuration = "namedElements"))

    modApi("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")

    modApi("teamreborn:energy:${energyVersion}")
    include("teamreborn:energy:${energyVersion}")

    modApi("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementation("com.terraformersmc:modmenu:${modmenuVersion}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }
}
