package requests

import io.gatling.javaapi.core.CoreDsl.ElFileBody
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object ConfirmPayInfoRequest {
    val confirmPayInfo = http("Confirm PayInfo")
            .post("/svc-seckill/api/client/seckill/order/confirmPayInfo")
            .body(ElFileBody("bodies/ConfirmPayInfo.json"))
            .header("Authorization", "#{userToken}")
            .check(jsonPath("$.status").find().`is`("200"))
}
