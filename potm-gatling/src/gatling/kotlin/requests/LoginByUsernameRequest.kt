package requests

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

/**
 * @author jianchengwang
 * @date 2023/4/6
 */
object LoginByUsernameRequest {
    val loginByUsername = http("Login By Username")
            .post("/svc-core/api/auth/loginByUsername")
            .body(ElFileBody("bodies/LoginByUsername.json"))
            .check(status().`is`(200))
            .check(jsonPath("$.status").find().`is`("200"))
            .check(jsonPath("$.data").find().saveAs("userToken"))
}
