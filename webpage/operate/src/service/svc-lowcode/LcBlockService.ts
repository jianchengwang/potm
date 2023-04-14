import { http } from "../../utils/http";

export default class LcBlockService {
    page = (params?: object) => {
        return http.request("get", `/svc-lowcode/api/operate/lcBlock/page`, { params });
    }

    get = (id: number) => {
        return http.get(`/svc-lowcode/operate/api/lcBlock/${id}`);
    }

    save = (data: object) => {
        return http.request("post", `/svc-lowcode/api/operate/lcBlock`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/svc-lowcode/api/operate/lcBlock/${id}`, { });
    }
}
