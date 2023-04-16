import { http } from "@/utils/http";

export default class LcTemplateService {

    page = (params?: object) => {
        return http.request("get", `/svc-lowcode/api/operate/lcTemplate/page`, { params });
    }

    get = (id: number) => {
        return http.get(`/svc-lowcode/operate/api/lcTemplate/${id}`);
    }

    save = (data: object) => {
        return http.request("post", `/svc-lowcode/api/operate/lcTemplate`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/svc-lowcode/api/operate/lcTemplate/${id}`, { });
    }
}