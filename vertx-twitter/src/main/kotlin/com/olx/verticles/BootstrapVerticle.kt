package com.olx.verticles

import com.olx.DatabaseConfiguration
import com.olx.TwitterConfiguration
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.VertxException
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import io.vertx.kotlin.config.getConfigAwait
import io.vertx.kotlin.core.DeploymentOptions
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BootstrapVerticle : CoroutineVerticle() {

    override suspend fun start() {
        logger.info("starting verticles deployment")
        coroutineScope {
            val configuration = vertx.retrieveConfiguration()
            launch { vertx.deployVerticleAwait(ServerVerticle::class.java.canonicalName) }
            launch {
                vertx.deployVerticleAwait(
                    name = TwitterVerticle::class.java.canonicalName,
                    options = DeploymentOptions(
                        config = configuration.twitterConfiguration,
                        instances = 2
                    )
                )
            }
            launch {
                vertx.deployVerticleAwait(
                    name = DatabaseVerticle::class.java.canonicalName,
                    options = DeploymentOptions(config = configuration.databaseConfiguration)
                )
            }
        }
        logger.info("completed verticles deployment")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BootstrapVerticle::class.java)
    }
}

suspend fun Vertx.retrieveConfiguration(): JsonObject {
    val options = ConfigRetrieverOptions(
        stores = listOf(ConfigStoreOptions(type = "env"))
    )
    return ConfigRetriever
        .create(this, options)
        .setConfigurationProcessor(::configurationProcessor)
        .getConfigAwait()
}

private fun configurationProcessor(config: JsonObject): JsonObject {
    if (!config.fieldNames().containsAll(TwitterConfiguration.variables)) {
        throw VertxException("One or more twitter configuration options are missing")
    }
    if (!config.fieldNames().containsAll(DatabaseConfiguration.variables)) {
        throw VertxException("One or more database configuration options are missing")
    }
    return config
}

private val JsonObject.twitterConfiguration: JsonObject
    get() = json {
        obj(TwitterConfiguration.variables.map { it to this@twitterConfiguration.getString(it) }.toMap())
    }

private val JsonObject.databaseConfiguration: JsonObject
    get() = json {
        obj(DatabaseConfiguration.variables.map { it to this@databaseConfiguration.getString(it) }.toMap())
    }