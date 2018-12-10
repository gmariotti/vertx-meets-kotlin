import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

// -------------------------- Dependencies ------------------------------------

dependencies {
    implementation(Libraries.postgresReactive)
    implementation(Libraries.vertxRxJava2)
    implementation(Libraries.vertxWebApiContract)
}

// -------------------------- Building Application ----------------------------

application {
    mainClassName = "com.olx.MainKt"
}

tasks {
    getByName<ShadowJar>("shadowJar") {
        baseName = project.name
        classifier = ""
        destinationDir = file("$rootDir/docker/vertx-reactive/build/")
        mergeServiceFiles()
        exclude("META-INF/*.DSA", "META-INF/*.RSA")
    }
}