import { http } from "../utils/http";


export const lcDatasourcePage = (params?: object) => {
  return http.request("get", `/svc-lowcode/api/operate/datasource/page`, { params });
};

export const lcDatasourceGet = (lcDatasourceId: number) => {
  return http.get(`/svc-lowcode/operate/api/datasource/${lcDatasourceId}`);
};

export const lcDatasourceSave = (data: object) => {
  return http.request("post", `/svc-lowcode/api/operate/datasource`, { data });
};