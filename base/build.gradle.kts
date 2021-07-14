val libgui_version = "4.0.0-beta.4+1.17-rc1"

repositories {
    maven { url = uri("https://server.bbkr.space/artifactory/libs-release") }
    maven { url = uri("https://maven.shedaniel.me/") }
}

dependencies {
    api(project(":vanilla-events"))

    modImplementation("io.github.cottonmc:LibGui:$libgui_version")
    include("io.github.cottonmc:LibGui:$libgui_version")
}
