val modmenuVersion: String by project

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
    modImplementation("com.terraformersmc:modmenu:${modmenuVersion}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }

    api(project(":base", configuration = "namedElements"))
}
