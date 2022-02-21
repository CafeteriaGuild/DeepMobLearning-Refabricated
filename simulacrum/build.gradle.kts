val energyVersion: String by project
val modmenuVersion: String by project
val clothConfigVersion: String by project

repositories {
    maven {
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
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
    modApi("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    include("teamreborn:energy:${energyVersion}")
    modApi("teamreborn:energy:${energyVersion}")

    modImplementation("com.terraformersmc:modmenu:${modmenuVersion}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }

    api(project(":base", configuration = "namedElements"))
}
