val libguiVersion: String by project
//val patchouliVersion: String by project

repositories {
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    /*maven {
        name = "Patchouli"
        url = uri("https://maven.blamejared.com")
    }*/
}

dependencies {
    api(project(":vanilla-events", configuration = "namedElements"))

    modApi("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")

    //modApi("vazkii.patchouli:Patchouli:${patchouliVersion}")
}
