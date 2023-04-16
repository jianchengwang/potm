import { http } from "@/utils/http";

export default class TestService {
    generateUser = (num: number) => {
        return http.request("post", "/svc-core/api/operate/test/generateUser", { params: {num: num} });
    }

    clearData = () => {
        return http.request("post", "/svc-core/api/operate/test/clearData", { });
    }
}
