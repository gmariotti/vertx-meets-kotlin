package com.olx

import io.reactiverse.kotlin.pgclient.PgPoolOptions
import io.reactiverse.reactivex.pgclient.PgClient
import io.vertx.core.json.Json
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.reactivex.core.Vertx

fun main(argv: Array<String>) {
    val openApiPath = argv[0]
    val options = PgPoolOptions(
        port = 5432,
        user = "vertx",
        password = "password",
        database = "vertx",
        host = "postgres"
    )
    val vertx = Vertx.vertx()
    val client = PgClient.pool(vertx, options)

    vertx.rxDeployVerticle(object : CoroutineVerticle() {
        override suspend fun start() {
            val factory = OpenAPI3RouterFactory.createAwait(
                this.vertx, openApiPath
            )
            factory.addHandlerByOperationId("topics") { routingContext ->
                client.rxQuery("SELECT topic FROM example GROUP BY topic")
                    .subscribe { set, error ->
                        error
                            ?.let { routingContext.fail(it) }
                            ?: routingContext.response().end(Json.encode(JsonObject("value" to set.size())))
                    }
            }
            this.vertx.createHttpServer()
                .requestHandler(factory.router)
                .listen(8080)
        }
    }).blockingGet()
}