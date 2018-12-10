package com.olx.web

import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

fun buildIndex(topic: String) = buildString {
    appendHTML(prettyPrint = true).html {
        head {
            title("Twitter Feed - $topic")
            link(
                href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css",
                rel= "stylesheet"
            )
            script(src = "https://code.jquery.com/jquery-1.11.2.min.js") {}
            script(src = "//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js") {}
            script(
                src = "https://raw.githack.com/vert-x3/vertx-examples/master/web-examples/src/main/java/io/vertx/example/web/realtime/webroot/vertx-eventbus.js",
                type = ScriptType.textJavaScript
            ) {}
            style {
                unsafe {
                    raw(""".tweets { font-size: 20pt; }""")
                }
            }
        }
        body {
            div(classes = "tweets") {
                text("Latest tweets for $topic:")
            }
            br
            div(classes = "tweets") {
                id = "latest"
            }
            script {
                unsafe {
                    raw(eventBusScript(topic, tweetBlock))
                }
            }
        }
    }
}

private val tweetBlock = buildString {
    appendHTML(prettyPrint = false).div(classes = "card") {
        style = "width: 18rem; margin-left: auto; margin-right: auto;"
        div(classes = "card-body") {
            h5(classes = "card-title") { text("?NAME?") }
            p(classes = "card-text") { text("?TEXT?") }
        }
    }
}

private fun eventBusScript(topic: String, tweetBlock: String) = """
var eb = new EventBus("http://localhost:8080/eventbus");
eb.onopen = function () {
    eb.registerHandler("$topic", function (err, msg) {
        var name = msg.body.name;
        var text = msg.body.message;
        var str = '$tweetBlock'.replace("?NAME?", name).replace("?TEXT?", text);
        ${'$'}('#latest').prepend(str);
    })
}
"""