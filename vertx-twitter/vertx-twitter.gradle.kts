import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

// -------------------------- Dependencies ------------------------------------

dependencies {
    implementation(Libraries.kotlinxHtml)
    implementation(Libraries.postgresDriver)
    implementation(Libraries.twitterCore)
    implementation(Libraries.vertxJdbc)
    implementation(Libraries.vertxSockJS)
    implementation(Libraries.vertxWebClient)
}

// -------------------------- Building Application ----------------------------

application {
    mainClassName = "com.olx.MainKt"
}

tasks {
    getByName<ShadowJar>("shadowJar") {
        baseName = project.name
        classifier = ""
        destinationDir = file("$rootDir/docker/vertx-twitter/build/")
        mergeServiceFiles()
        exclude("META-INF/*.DSA", "META-INF/*.RSA", "*.yml")
    }
}