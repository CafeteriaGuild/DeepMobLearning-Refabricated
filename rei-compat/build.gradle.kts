val reiVersion: String by project

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":base", configuration = "namedElements"))
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}
