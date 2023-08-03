val palVersion: String by project
val libguiVersion: String by project

repositories {
    maven {
        name = "LadySnake"
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("io.github.ladysnake")
            includeGroupByRegex("io\\.github\\.onyxstudios.*")
        }
    }
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
}

dependencies {
    api(project(":base", configuration = "namedElements"))

    modApi("io.github.ladysnake:PlayerAbilityLib:${palVersion}")
    include("io.github.ladysnake:PlayerAbilityLib:${palVersion}")

    modApi("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")
}
