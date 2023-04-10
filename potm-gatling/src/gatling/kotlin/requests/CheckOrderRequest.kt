package requests

import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object CheckOrderRequest {
    val checkOrder = http("Check Order")
            .get("/svc-seckill/api/client/seckill/order/check/#{skToken}")
            .header("Authorization", "#{userToken}")
            .check(jsonPath("$.status").find().`is`("200"))
            .check(jsonPath("$.data").find().saveAs("orderNo"))
}
