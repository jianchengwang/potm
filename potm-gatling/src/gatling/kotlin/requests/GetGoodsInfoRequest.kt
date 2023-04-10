package requests

import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.http.HttpDsl.http

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object GetGoodsInfoRequest {
    val getGoodsInfo = http("Get GoodsInfo")
            .get("/svc-seckill/api/client/seckill/goodsInfo/#{skGoodsId}")
            .header("Authorization", "#{userToken}")
            .check(jsonPath("$.status").find().`is`("200"))
}
