package simulations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import requests.*
import java.time.Duration

class SeckillSimulation : Simulation() {
    val baseUrl = "http://localhost:9070"
    val skGoodsId = 1
    val entryKey = "djingd!"
    private val protocol = http
            .baseUrl(baseUrl)
            .contentTypeHeader("application/json")

    val userFeeder = csv("data/users.csv").circular()

    private val scn = scenario("seckill")
        .feed(userFeeder)
        .exec {
            session ->
                session.set("skGoodsId", skGoodsId).set("entryKey", entryKey)
        }
        .exec(LoginByUsernameRequest.loginByUsername)
        .exec {
            session ->
                session
        }
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .exec(GetGoodsInfoRequest.getGoodsInfo)
        .exec(GetSkTokenRequest.getSkToken)
        .exec {
            session ->
            session
        }
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .exec(CreateOrderRequest.createOrder)
        .pause(Duration.ofMillis(100))
        .doWhile { session -> session.getString("orderNo") == null || session.getString("orderNo") == "0" }.on(
            exec(CheckOrderRequest.checkOrder).pause(Duration.ofMillis(500), Duration.ofMillis(800))
        ).exec { session ->
//          println(session.getString("orderNo"))
            session
        }
        .pause(Duration.ofMillis(10), Duration.ofMillis(50))
        .doIf { session -> session.getString("orderNo") != null && session.getString("orderNo") != "-1" }.then(
            exec(ConfirmPayInfoRequest.confirmPayInfo)
        )

    init {
        setUp(scn.injectOpen(
                nothingFor(Duration.ofMillis(10)),
               rampUsers(1000).during(Duration.ofSeconds(60)) // total-231103 p95-1129 p99-1304/6091-365-533
                  //rampUsers(500).during(Duration.ofSeconds(60)) // total-3091 p50-101	p99-192
        )).protocols(protocol)
    }
}
