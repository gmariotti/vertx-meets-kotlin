package com.olx.verticles

import com.olx.DATABASE_BUS_ADDRESS
import com.olx.TWITTER_BUS_ADDRESS
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders.TEXT_HTML
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.toChannel
import io.vertx.kotlin.ext.web.client.sendAwait
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ServerVerticleTest {

    @BeforeEach
    fun `deploy server`(vertx: Vertx, context: VertxTestContext) = runBlocking {
        vertx.deployVerticleAwait(ServerVerticle::class.java.canonicalName)
        context.completeNow()
    }

    @Test
    fun `check successful request`(vertx: Vertx, context: VertxTestContext) = runBlocking {
        val twitterChannel = vertx.eventBus().consumer<String>(TWITTER_BUS_ADDRESS).toChannel(vertx)
        val dbChannel = vertx.eventBus().consumer<String>(DATABASE_BUS_ADDRESS).toChannel(vertx)
        val webClient = WebClient.create(vertx)
        val topic = "whatever"

        context.verifyCoroutine {
            webClient
                .getAbs("http://127.0.0.1:8080/search/$topic")
                .expect(ResponsePredicate.SC_OK)
                .expect(ResponsePredicate.contentType(TEXT_HTML.toString()))
                .sendAwait()
        }

        val twitterMessage = twitterChannel.receiveOrNull()
        context.verify { assertEquals(twitterMessage?.body(), topic) }

        val dbMessage = dbChannel.receiveOrNull()
        context.verify { assertEquals(dbMessage?.body(), topic) }

        context.completeNow()
    }
}

@Suppress("TooGenericExceptionCaught")
suspend fun VertxTestContext.verifyCoroutine(block: suspend () -> Unit) = coroutineScope {
    launch(coroutineContext) {
        try {
            block()
        } catch (t: Throwable) {
            failNow(t)
        }
    }
    this
}
