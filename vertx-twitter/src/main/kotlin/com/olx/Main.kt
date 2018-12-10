package com.olx

import com.olx.verticles.BootstrapVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait

suspend fun main() {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    Vertx
        .vertx()
        .deployVerticleAwait(BootstrapVerticle::class.java.canonicalName)
}