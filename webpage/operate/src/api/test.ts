import { http } from "../utils/http";

export const generateUser = (num: number) => {
  return http.request("post", "/operate/test/generateUser", { params: {num: num} });
};

export const clearData = () => {
  return http.request("post", "/operate/test/clearData", {});
};

