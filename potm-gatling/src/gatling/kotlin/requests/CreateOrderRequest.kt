package requests

import io.gatling.javaapi.core.CoreDsl.ElFileBody
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object CreateOrderRequest {
    val createOrder = http("Create Order")
            .post("/svc-seckill/api/client/seckill/order")
            .body(ElFileBody("bodies/CreateOrder.json"))
            .header("Authorization", "#{userToken}")
            .check(jsonPath("$.status").find().`is`("200"))
}
