import { http } from "@/utils/http";

export default class SysUserService {
    page = (params?: object) => {
        return http.request("get", `/svc-core/api/operate/sysUser/page`, { params });
    }

    save = (data: object) => {
        return http.request("post", `/svc-core/api/operate/sysUser`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/svc-core/api/operate/sysUser/${id}`, { });
    }
}
