import { http } from "@/utils/http";

export default class ${table.className}Service {
    page = (params?: object) => {
        return http.request("get", `/${svcName}/api/operate/${table.className?uncap_first}/page`, { params });
    }

    get = (id: number) => {
        return http.get(`/${svcName}/operate/api/${table.className?uncap_first}/<#noparse>${id}</#noparse>`);
    }

    save = (data: object) => {
        return http.request("post", `/${svcName}/api/operate/${table.className?uncap_first}`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/${svcName}/api/operate/${table.className?uncap_first}/<#noparse>${id}</#noparse>`, { });
    }
}
