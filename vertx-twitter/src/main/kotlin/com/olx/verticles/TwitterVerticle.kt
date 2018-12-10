package com.olx.verticles

import com.olx.DEFAULT_TOPIC_INTERVAL
import com.olx.TOPIC_INTERVAL
import com.olx.TWITTER_BUS_ADDRESS
import com.olx.TwitterConfiguration.ACCESS_TOKEN
import com.olx.TwitterConfiguration.ACCESS_TOKEN_SECRET
import com.olx.TwitterConfiguration.CONSUMER_KEY
import com.olx.TwitterConfiguration.CONSUMER_SECRET_KEY
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitBlocking
import io.vertx.kotlin.coroutines.toChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import twitter4j.Query
import twitter4j.Query.RECENT
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AuthorizationFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.Random

class TwitterVerticle : CoroutineVerticle() {
    private lateinit var twitter: Twitter

    override suspend fun start() {
        logger.info("starting twitter verticle")

        val configuration = ConfigurationBuilder()
            .setOAuthConsumerKey(config.getString(CONSUMER_KEY.name))
            .setOAuthConsumerSecret(config.getString(CONSUMER_SECRET_KEY.name))
            .setOAuthAccessToken(config.getString(ACCESS_TOKEN.name))
            .setOAuthAccessTokenSecret(config.getString(ACCESS_TOKEN_SECRET.name))
            .build()
        twitter = TwitterFactory().getInstance(AuthorizationFactory.getInstance(configuration))

        vertx.eventBus().consumer<String>(TWITTER_BUS_ADDRESS) {
            // route message to correct function
            val topic = it.body()
            vertx.setPeriodic(config.getLong(TOPIC_INTERVAL, DEFAULT_TOPIC_INTERVAL)) { searchTopic(topic) }
        }

        logger.info("completed twitter verticle deployment")
    }

    private fun CoroutineScope.searchTopic(topic: String) {
        launch {
            val topicEventBus = vertx.eventBus()
                .publisher<JsonObject>(topic)
                .toChannel(vertx)
            val query = Query("q=$topic").resultType(RECENT).count(Random().nextInt(15))
            awaitBlocking { twitter.search(query) }
                .tweets
                .forEach {
                    topicEventBus.send(json {
                        obj(
                            "name" to it.user.name,
                            "message" to it.text
                        )
                    })
                }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TwitterVerticle::class.java)
    }
}