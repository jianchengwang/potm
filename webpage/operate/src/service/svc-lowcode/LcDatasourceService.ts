import { http } from "../../utils/http";

export default class LcDatasourceService {
    page = (params?: object) => {
        return http.request("get", `/svc-lowcode/api/operate/lcDatasource/page`, { params });
    }

    fetchAll = (params?: object) => {
        return http.request("get", `/svc-lowcode/api/operate/lcDatasource/fetchAll`, { params });
    }

    save = (data: object) => {
        return http.request("post", `/svc-lowcode/api/operate/lcDatasource`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/svc-lowcode/api/operate/lcDatasource/${id}`, { });
    }

    refreshTable = (id: number) => {
        return http.post(`/svc-lowcode/api/operate/lcDatasource/${id}/refreshTable`);
    }

    getTables = (id: number) => {
        return http.request("get", `/svc-lowcode/api/operate/lcDatasource/${id}/tables`, { });
    }
}