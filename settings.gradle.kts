include("vertx-twitter", "vertx-reactive")

with(rootProject) {
    name = "vertx-hack-and-learn"
    children.forEach { it.buildFileName = "${it.name}.gradle.kts" }
}