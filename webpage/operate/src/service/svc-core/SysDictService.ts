import { http } from "@/utils/http";

export default class SysDictService {
    page = (params?: object) => {
        return http.request("get", `/svc-core/api/operate/sysDict/page`, { params });
    }

    fetchAll = () => {
        return http.request("get", `/svc-core/api/operate/sysDict/fetchAll`, { });
    }

    getItemList = (svcName: string, dictKey: string) => {
        return http.request("get", `/svc-core/api/operate/sysDict/${svcName}/${dictKey}`, { });
    }
}
