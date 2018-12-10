package com.olx.verticles

import com.olx.DATABASE_BUS_ADDRESS
import com.olx.DatabaseConfiguration.PASSWORD
import com.olx.DatabaseConfiguration.URL
import com.olx.DatabaseConfiguration.USER
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.sql.closeAwait
import io.vertx.kotlin.ext.sql.executeAwait
import io.vertx.kotlin.ext.sql.getConnectionAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DatabaseVerticle : CoroutineVerticle() {
    private lateinit var client: JDBCClient

    override suspend fun start() {
        logger.info("starting database verticle")
        initializeDatabase()
        client = JDBCClient.createShared(
            vertx, JsonObject(
                "url" to "${config.getString(URL.name)}example",
                "driver_class" to "org.postgresql.Driver",
                "user" to config.getString(USER.name),
                "password" to config.getString(PASSWORD.name)
            )
        )
        client.getConnectionAwait().closeAwait()
        vertx.eventBus().consumer<String>(DATABASE_BUS_ADDRESS).handler { storeTopic(it.body()) }
        logger.info("completed database verticle deployment")
    }

    private fun CoroutineScope.storeTopic(topic: String) {
        launch {
            client
                .getConnectionAwait()
                .executeAwait("INSERT INTO example (topic) VALUES ('$topic')")
            logger.info("stored topic=$topic")
        }
    }

    private suspend fun initializeDatabase() {
        JDBCClient.createShared(
            vertx, JsonObject(
                "url" to config.getString(URL.name),
                "driver_class" to "org.postgresql.Driver",
                "user" to config.getString(USER.name),
                "password" to config.getString(PASSWORD.name)
            )
        ).getConnectionAwait()
            .executeAwait("CREATE TABLE IF NOT EXISTS example (topic VARCHAR(250) NOT NULL)")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseVerticle::class.java)
    }
}
