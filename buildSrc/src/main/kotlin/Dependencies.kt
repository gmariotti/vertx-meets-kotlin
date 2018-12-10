object Versions {
    const val arrow = "0.7.3"
    const val detekt = "1.0.0.RC9.2"
    const val junit = "5.3.1"
    const val ktlint = "0.29.0"
    const val logback = "1.2.3"
    const val mockK = "1.8.13.kotlin13"
    const val newRelic = "4.7.0"
    const val twitter4j = "4.0.7"
    const val vertx = "3.6.0"
}

object Libraries {
    const val arrowCore = "io.arrow-kt:arrow-core:${Versions.arrow}"
    const val junitApi = "org.junit.jupiter:junit-jupiter-api"
    const val junitEngine = "org.junit.jupiter:junit-jupiter-engine"
    const val junitParams = "org.junit.jupiter:junit-jupiter-params"
    const val kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val kotlinxHtml = "org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.11"
    const val ktlint = "com.github.shyiko:ktlint:${Versions.ktlint}"
    const val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logback}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
    const val newRelicAgent = "com.newrelic.agent.java:newrelic-agent:${Versions.newRelic}"
    const val postgresDriver = "org.postgresql:postgresql:42.2.5"
    const val postgresReactive = "io.reactiverse:reactive-pg-client:0.11.0"
    const val twitterCore = "org.twitter4j:twitter4j-core:${Versions.twitter4j}"
    const val vertxConfig = "io.vertx:vertx-config:${Versions.vertx}"
    const val vertxCore = "io.vertx:vertx-core:${Versions.vertx}"
    const val vertxJdbc = "io.vertx:vertx-jdbc-client:${Versions.vertx}"
    const val vertxJunit5 = "io.vertx:vertx-junit5:${Versions.vertx}"
    const val vertxKotlin = "io.vertx:vertx-lang-kotlin:${Versions.vertx}"
    const val vertxKotlinCoroutines = "io.vertx:vertx-lang-kotlin-coroutines:${Versions.vertx}"
    const val vertxRxJava2 = "io.vertx:vertx-rx-java2:${Versions.vertx}"
    const val vertxSockJS = "io.vertx:vertx-sockjs-service-proxy:${Versions.vertx}"
    const val vertxWeb = "io.vertx:vertx-web:${Versions.vertx}"
    const val vertxWebClient = "io.vertx:vertx-web-client:${Versions.vertx}"
    const val vertxWebApiContract = "io.vertx:vertx-web-api-contract:${Versions.vertx}"
}

object Boms {
    const val junit = "org.junit:junit-bom:${Versions.junit}"
}