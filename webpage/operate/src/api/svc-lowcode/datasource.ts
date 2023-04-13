import { http } from "../../utils/http";


export const datasourcePage = (params?: object) => {
  return http.request("get", `/svc-lowcode/api/operate/datasource/page`, { params });
};

export const datasourceGet = (datasourceId: number) => {
  return http.get(`/svc-lowcode/operate/api/datasource/${datasourceId}`);
};

export const datasourceSave = (data: object) => {
  return http.request("post", `/svc-lowcode/api/operate/datasource`, { data });
};

export const datasourceRefreshTable = (datasourceId: number) => {
  return http.post(`/svc-lowcode/api/operate/datasource/${datasourceId}/refreshTable`);
}