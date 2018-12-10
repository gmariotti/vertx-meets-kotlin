package com.olx.verticles

import arrow.core.Try
import com.olx.DATABASE_BUS_ADDRESS
import com.olx.TWITTER_BUS_ADDRESS
import com.olx.web.buildIndex
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import io.vertx.core.http.HttpHeaders.TEXT_HTML
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.bridge.BridgeEventType.SOCKET_CLOSED
import io.vertx.ext.bridge.BridgeEventType.SOCKET_CREATED
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.handler.sockjs.BridgeOptions
import io.vertx.kotlin.ext.web.handler.sockjs.PermittedOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts

class ServerVerticle : CoroutineVerticle() {

    @ExperimentalContracts
    override suspend fun start() {
        logger.info("starting deployment of http server")

        val router = Router.router(vertx)
        router.apply {
            route().handler(BodyHandler.create(false))
            route().handler(CorsHandler.create("*"))
            // websocket
            get("/search/:topic").coroutineHandler(this@ServerVerticle) {
                val topic = it.request().getParam("topic")
                vertx.eventBus().send(TWITTER_BUS_ADDRESS, topic)
                vertx.eventBus().send(DATABASE_BUS_ADDRESS, topic)
                it.response()
                    .putHeader(CONTENT_TYPE.toString(), TEXT_HTML.toString())
                    .end(Buffer.buffer(buildIndex(topic)))
            }
            val bridgeOptions = BridgeOptions(
                outboundPermitted = listOf(PermittedOptions(addressRegex = ".*"))
            )
            route("/eventbus/*")
                .handler(SockJSHandler.create(vertx).bridge(bridgeOptions) { event ->
                    when (event.type()) {
                        SOCKET_CREATED -> logger.info("socket created")
                        SOCKET_CLOSED -> logger.info("socket closed")
                    }
                    event.complete(true)
                })
        }
        vertx.createHttpServer()
            .requestHandler(router)
            .listenAwait(8080)

        logger.info("completed deployment of http server")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ServerVerticle::class.java)
    }
}

fun Route.coroutineHandler(context: CoroutineScope, handle: suspend (RoutingContext) -> Unit): Route = handler { ctx ->
    context.launch {
        Try { coroutineScope { handle(ctx) } }
            .failed()
            .map { ctx.fail(it) }
    }
}