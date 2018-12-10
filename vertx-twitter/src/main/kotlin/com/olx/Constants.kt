package com.olx

const val TWITTER_BUS_ADDRESS = "twitter-bus"
const val DATABASE_BUS_ADDRESS = "db-bus"
const val TOPIC_INTERVAL = "TOPIC_INTERVAL"

const val DEFAULT_TOPIC_INTERVAL = 10_000L

// Environment Variables
enum class TwitterConfiguration {
    CONSUMER_KEY, CONSUMER_SECRET_KEY, ACCESS_TOKEN, ACCESS_TOKEN_SECRET;

    companion object {
        val variables = values().map { it.name }.toSet()
    }
}

enum class DatabaseConfiguration {
    URL, USER, PASSWORD;

    companion object {
        val variables = values().map { it.name }.toSet()
    }
}