val libguiVersion = "5.1.0+1.18"

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
