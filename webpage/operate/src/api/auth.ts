import { http } from "../utils/http";

export const loginByUsername = (data: object) => {
  return http.request("post", "/auth/loginByUsername", { data });
};
