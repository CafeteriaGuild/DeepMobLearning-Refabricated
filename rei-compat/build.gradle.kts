val reiVersion = "7.0.346"

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":base"))
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}
