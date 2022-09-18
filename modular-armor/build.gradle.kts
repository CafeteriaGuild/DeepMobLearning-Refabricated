val palVersion: String by project
val libguiVersion: String by project

repositories {
    maven {
        name = "LadySnake"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
    maven {
        name = "Patchouli"
        url = uri("https://maven.blamejared.com")
    }
}

dependencies {
    api(project(":base", configuration = "namedElements"))

    modApi("io.github.ladysnake:PlayerAbilityLib:${palVersion}")
    include("io.github.ladysnake:PlayerAbilityLib:${palVersion}")

    modApi("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")
}
