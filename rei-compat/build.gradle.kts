val reiVersion: String by project

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":base", configuration = "namedElements"))
    modApi("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}
