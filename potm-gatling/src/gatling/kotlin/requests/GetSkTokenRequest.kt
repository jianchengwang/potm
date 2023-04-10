package requests

import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object GetSkTokenRequest {
    val getSkToken = http("Get SkToken")
            .get("/svc-seckill/api/client/seckill/token/#{skGoodsId}?entryKey=#{entryKey}")
            .header("Authorization", "#{userToken}")
            .check(jsonPath("$.status").find().`is`("200"))
            .check(jsonPath("$.data").find().saveAs("skToken"))
}
