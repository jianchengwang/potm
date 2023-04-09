package simulations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import requests.*
import java.time.Duration

class DemoSimulation : Simulation() {
    var var1 = 0
    var var2 = "helloworld"
    val baseUrl = "http://localhost:9071"
    private val protocol = http
            .baseUrl(baseUrl)
            .contentTypeHeader("application/json")

    val userFeeder = csv("data/users.csv").circular()

    private val scn = scenario("test")
        .feed(userFeeder)
        .exec {
            session ->
                session.set("var1", var1).set("var2", var2)
        }
        .exec(LoginByUsernameRequest.loginByUsername)
        .exec {
            session ->
                session
        }
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .pause(Duration.ofMillis(100))
        .doWhile { session -> session.getString("var1") == null || session.getString("var1") == "0" }.on(
            exec {
                session ->
                    session.set("var1", 1)
            }
        ).exec { session ->
            println(session.getString("var1"))
            session
        }
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .doIf { session -> session.getString("var1") != null && session.getString("var1") != "-1" }.then(
            exec {
                session ->
                println("done")
                session
            }
        )

    init {
        setUp(scn.injectOpen(
                nothingFor(Duration.ofMillis(10)),
               rampUsers(1000).during(Duration.ofSeconds(60))
        )).protocols(protocol)
    }
}
