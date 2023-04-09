import { http } from "../utils/http";


export const userPage = (params?: object) => {
  return http.request("get", `/operate/user/page`, { params });
};

export const userPost = (data?: object) => {
  return http.request("post", `/operate/user`, { data });
};

export const userPut = (id: number, data?: object) => {
  return http.request("post", `/operate/user/${id}`, { data });
};