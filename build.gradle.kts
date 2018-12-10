import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    `java-library`
    id("com.github.ben-manes.versions") version "0.20.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC9.2"
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("com.github.ben-manes.versions")
        plugin("io.gitlab.arturbosch.detekt")
        plugin("jacoco")
        plugin("java-library")
        plugin("kotlin")
    }

    // -------------------------- Dependencies ------------------------------------

    val ktlint by configurations.creating

    dependencies {
        implementation(Libraries.arrowCore)
        implementation(Libraries.kotlinStdlibJdk8)
        implementation(Libraries.logbackClassic)
        implementation(Libraries.vertxConfig)
        implementation(Libraries.vertxCore)
        implementation(Libraries.vertxKotlin)
        implementation(Libraries.vertxKotlinCoroutines)
        implementation(Libraries.vertxWeb)
        testImplementation(Libraries.mockK)
        testImplementation(Libraries.vertxJunit5)
        ktlint(Libraries.ktlint)
    }

    tasks {
        getByName<DependencyUpdatesTask>("dependencyUpdates") {
            resolutionStrategy {
                componentSelection {
                    all {
                        val rejected = listOf("alpha", "beta", "rc", "cr", "m", "dmr")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                        if (rejected) {
                            reject("Release candidate")
                        }
                    }
                }
            }
        }
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        withType<Test> {
            useJUnitPlatform()
            reports.html.destination = file("${reporting.baseDir}/$name")
            testLogging { exceptionFormat = TestExceptionFormat.FULL }
            // https://github.com/gradle/gradle/issues/5431
            addTestListener(object : TestListener {
                override fun beforeTest(testDescriptor: TestDescriptor) {}
                override fun beforeSuite(suite: TestDescriptor) {}
                override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
                override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                    if (suite.parent == null) {
                        println(buildString {
                            append("Results: ${result.resultType} (${result.testCount} tests, ")
                            append("${result.successfulTestCount} successes, ")
                            append("${result.failedTestCount} failures, ")
                            append("${result.skippedTestCount} skipped)")
                        })
                    }
                }
            })
        }

        val ktlintMain = "com.github.shyiko.ktlint.Main"
        val ktlintArgs = listOf("-F", "$projectDir/src/**/*.kt")
        val checkTask = getByName("check")
        create<JavaExec>("ktlintCheck") {
            description = "Runs ktlint on all kotlin sources in this project."
            checkTask.group?.let { group = it }
            main = ktlintMain
            classpath = ktlint
            args = ktlintArgs.drop(1)
        }
        create<JavaExec>("ktlintFormat") {
            description = "Runs the ktlint formatter on all kotlin sources in this project."
            group = "Formatting tasks"
            main = ktlintMain
            classpath = ktlint
            args = ktlintArgs
        }
    }

    detekt {
        toolVersion = Versions.detekt
        input = files("$projectDir/src/main/kotlin", "$projectDir/src/test/kotlin")
        config = files("$rootDir/detekt.yml")
    }
}