import { http } from "../utils/http";


export const lcBlockPage = (params?: object) => {
  return http.request("get", `/svc-lowcode/api/operate/block/page`, { params });
};

export const lcBlockGet = (lcBlockId: number) => {
  return http.get(`/svc-lowcode/operate/api/block/${lcBlockId}`);
};

export const lcBlockSave = (data: object) => {
  return http.request("post", `/svc-lowcode/api/operate/block`, { data });
};