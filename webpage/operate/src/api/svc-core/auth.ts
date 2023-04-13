import { http } from "../../utils/http";

export const loginByUsername = (data: object) => {
  return http.request("post", "/svc-core/api/auth/loginByUsername", { data });
};
