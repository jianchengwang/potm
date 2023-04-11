import { http } from "../utils/http";


export const dictPage = (params?: object) => {
  return http.request("get", `/svc-core/api/operate/dict/page`, { params });
};

export const dictLoadAll = () => {
  return http.request("get", `/svc-core/api/operate/dict/loadAll`, { });
};

export const dictItemList = (svcName: string, dictKey: string) => {
  return http.request("get", `/svc-core/api/operate/dict/${svcName}/${dictKey}`, { });
};