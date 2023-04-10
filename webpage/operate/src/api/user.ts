import { http } from "../utils/http";


export const userPage = (params?: object) => {
  return http.request("get", `/svc-core/api/operate/user/page`, { params });
};

export const userSave = (data?: object) => {
  return http.request("post", `/svc-core/api/operate/user`, { data });
};

export const userDelete = (id: number) => {
  return http.request("delete", `/svc-core/api/operate/user/${id}`, { });
};