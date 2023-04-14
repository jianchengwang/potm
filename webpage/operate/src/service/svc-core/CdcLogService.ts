import { http } from "../../utils/http";

export default class CdcLogService {
    page = (params?: object) => {
        return http.request("get", `/svc-core/api/operate/cdcLog/page`, { params });
    }

    fetchAll = () => {
        return http.request("get", `/svc-core/api/operate/sysDict/fetchAll`, { });
    }
}
