import { http } from "../utils/http";


export const cdcLogPage = (params?: object) => {
  return http.request("get", `/operate/cdcLog/page`, { params });
};

export const cdcLogRowDetails = (logInfoId: number) => {
  return http.request("get", `/operate/cdcLog/${logInfoId}`, { });
};