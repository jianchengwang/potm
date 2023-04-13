import { http } from "../../utils/http";


export const blockPage = (params?: object) => {
  return http.request("get", `/svc-lowcode/api/operate/block/page`, { params });
};

export const blockGet = (lcBlockId: number) => {
  return http.get(`/svc-lowcode/operate/api/block/${lcBlockId}`);
};

export const blockSave = (data: object) => {
  return http.request("post", `/svc-lowcode/api/operate/block`, { data });
};