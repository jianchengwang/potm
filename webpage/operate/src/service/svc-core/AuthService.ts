import { http } from "@/utils/http";

export default class AuthService {
    loginByUsername = (data: object) => {
        return http.request("post", "/svc-core/api/auth/loginByUsername", { data });
    }
}
