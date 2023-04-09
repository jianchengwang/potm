import Cookies from "js-cookie";

export const sessionKey = "user-info";
export const TokenKey = "authorized-token";

export function getToken() {
  // 此处与`TokenKey`相同，此写法解决初始化时`Cookies`中不存在`TokenKey`报错
  return Cookies.get(TokenKey)
    ? Cookies.get(TokenKey)
    : localStorage.getItem(sessionKey);
}

export function setToken(token: string) {
  Cookies.set(TokenKey, token);
  localStorage.setItem(TokenKey, token);
}

export function removeToken() {
  Cookies.remove(TokenKey);
  localStorage.clear();
}
