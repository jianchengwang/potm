import { http } from "../utils/http";


export const cdcLogPage = (params?: object) => {
  return http.request("get", `/svc-core/api/operate/cdcLog/page`, { params });
};

export const cdcLogRowDetails = (logInfoId: number) => {
  return http.request("get", `/svc-core/api/operate/cdcLog/${logInfoId}`, { });
};