val reiVersion: String by project

repositories {
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
    api(project(":base", configuration = "namedElements"))
    modApi("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}
