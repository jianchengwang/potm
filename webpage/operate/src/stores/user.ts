import { defineStore } from 'pinia'
import { setToken, removeToken } from '@/utils/auth.ts'
import router from "@/router/index.ts";

import AuthService from '@/service/svc-core/AuthService.ts';
const authService = new AuthService();

// 第一个参数必须是全局唯一
export const useUserStore = defineStore('userStore', {
  state: () => ({
    username: "",
    password: "",
    rememberMe: false,
    userInfo: {},
  }),
  actions: {
    async loginByUsername(username: string, password: string, rememberMe: boolean) {
      this.rememberMe = rememberMe
      if (rememberMe) {
        this.username = username
        this.password = password
      }
      return new Promise((resolve, reject) => {
        authService.loginByUsername({ username, password })
          .then(data => {
            if (data) {
              setToken(data.data);
              resolve(data);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    async logout() {
      removeToken();
      localStorage.clear();
      router.push("/auth/login");
    },
  },
})