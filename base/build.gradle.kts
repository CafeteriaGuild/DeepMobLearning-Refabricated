val libguiVersion: String by project

repositories {
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    /*maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }*/
}

dependencies {
    api(project(":vanilla-events", configuration = "namedElements"))

    modImplementation("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")
}
