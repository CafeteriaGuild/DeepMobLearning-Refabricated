val palVersion = "1.5.0"
val libguiVersion = "5.1.0+1.18"

repositories {
    maven {
        name = "LadySnake"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
}

dependencies {
    api(project(":base"))
    api(project(":vanilla-events"))

    modImplementation("io.github.ladysnake:PlayerAbilityLib:${palVersion}")
    include("io.github.ladysnake:PlayerAbilityLib:${palVersion}")

    modImplementation("io.github.cottonmc:LibGui:${libguiVersion}")
    include("io.github.cottonmc:LibGui:${libguiVersion}")
}
