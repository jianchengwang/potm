import { http } from "@/utils/http";

export default class CdcLogService {
    page = (params?: object) => {
        return http.request("get", `/svc-core/api/operate/cdcLog/page`, { params });
    }

    getRowDetails = (logInfoId: number) => {
        return http.request("get", `/svc-core/api/operate/cdcLog/${logInfoId}`, { });
    }
}
