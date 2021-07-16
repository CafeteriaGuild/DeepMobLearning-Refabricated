val rei_version = "6.0.262-alpha"

repositories {
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":base"))
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:$rei_version")
}
