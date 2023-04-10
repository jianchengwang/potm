import Axios, {
  AxiosInstance,
  AxiosRequestConfig,
  CustomParamsSerializer
} from "axios";
import {
  PureHttpError,
  RequestMethods,
  PureHttpResponse,
  PureHttpRequestConfig
} from "./types.d";
import { stringify } from "qs";
import NProgress from "../progress";

import { loadEnv } from "@build/index";
import { getToken } from "../auth";

import { useUserStore } from '@/stores/user';

import { ToastSeverity } from 'primevue/api';
import { app } from '@/main';

// 加载环境变量 VITE_PROXY_DOMAIN（开发环境）  VITE_PROXY_DOMAIN_REAL（打包后的线上环境）
const { VITE_PROXY_DOMAIN, VITE_PROXY_DOMAIN_REAL } = loadEnv();

// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
const defaultConfig: AxiosRequestConfig = {
  baseURL: process.env.NODE_ENV === "production"
    ? VITE_PROXY_DOMAIN_REAL
    : VITE_PROXY_DOMAIN,
  // 请求超时时间
  timeout: 10000,
  headers: {
    Accept: "application/json, text/plain, */*",
    "Content-Type": "application/json",
    "X-Requested-With": "XMLHttpRequest"
  },
  // 数组格式参数序列化（https://github.com/axios/axios/issues/5142）
  paramsSerializer: {
    serialize: stringify as unknown as CustomParamsSerializer
  }
};

const showErrorToast = (status: number, message: string) => {
  switch (status) {
    case 400:
      app.config.globalProperties.$toast.add({ severity: ToastSeverity.WARN, summary: 'Info', detail: message, life: 3000 });
      break;
    case 404:
      app.config.globalProperties.$toast.add({ severity: ToastSeverity.WARN, summary: 'Warn', detail: message, life: 3000 });
      break;
    case 401:
    case 403:
      app.config.globalProperties.$toast.add({ severity: ToastSeverity.WARN, summary: 'Warn', detail: message, life: 3000 });
      break;
    case 500:
      app.config.globalProperties.$toast.add({ severity: ToastSeverity.ERROR, summary: 'Error', detail: message, life: 3000 });
      break;
    default:
      break;
  }
}

class PureHttp {
  constructor() {
    this.httpInterceptorsRequest();
    this.httpInterceptorsResponse();
  }

  /** 初始化配置对象 */
  private static initConfig: PureHttpRequestConfig = {
    beforeResponseCallback: response => {
      if (response.data && response.data.status != 200) {
        switch (response.data.status) {
          case 400:
            showErrorToast(response.data.status, response.data.message ? response.data.message : "请求参数错误")
            break;
          case 404:
            showErrorToast(response.data.status, response.data.message ? response.data.message : "请求资源不存在")
            break;
          case 401:
          case 403:
            showErrorToast(response.data.status, response.data.message ? response.data.message : "无权限")
            const userStore = useUserStore();
            userStore.logout();
            break;
          case 500:
            showErrorToast(response.data.status, response.data.message ? response.data.message : "服务器错误")
            break;
          default:
            break;
        }
      }
    }
  };

  /** token过期后，暂存待执行的请求 */
  private static requests = [];

  /** 防止重复刷新token */
  private static isRefreshing = false;

  /** 保存当前Axios实例对象 */
  private static axiosInstance: AxiosInstance = Axios.create(defaultConfig);

  /** 重连原始请求 */
  private static retryOriginalRequest(config: PureHttpRequestConfig) {
    return new Promise(resolve => {
      PureHttp.requests.push((token: string) => {
        config.headers["Authorization"] = token;
        resolve(config);
      });
    });
  }

  /** 请求拦截 */
  private httpInterceptorsRequest(): void {
    PureHttp.axiosInstance.interceptors.request.use(
      async (config: PureHttpRequestConfig) => {
        // 开启进度条动画
        NProgress.start();
        // 优先判断post/get等方法是否传入回掉，否则执行初始化设置等回掉
        if (typeof config.beforeRequestCallback === "function") {
          config.beforeRequestCallback(config);
          return config;
        }
        if (PureHttp.initConfig.beforeRequestCallback) {
          PureHttp.initConfig.beforeRequestCallback(config);
          return config;
        }
        /** 请求白名单，放置一些不需要token的接口（通过设置请求白名单，防止token过期后再请求造成的死循环问题） */
        const whiteList = ["/auth/login"];
        return whiteList.some(v => config.url.indexOf(v) > -1)
          ? config
          : new Promise(resolve => {
              const token = getToken();
              if (token) {
                config.headers["Authorization"] = token;
                resolve(config);
              } else {
                resolve(config);
              }
            });
      },
      error => {
        return Promise.reject(error);
      }
    );
  }

  /** 响应拦截 */
  private httpInterceptorsResponse(): void {
    const instance = PureHttp.axiosInstance;
    instance.interceptors.response.use(
      (response: PureHttpResponse) => {
        const $config = response.config;
        // 关闭进度条动画
        NProgress.done();
        // 优先判断post/get等方法是否传入回掉，否则执行初始化设置等回掉
        if (typeof $config.beforeResponseCallback === "function") {
          $config.beforeResponseCallback(response);
          return response.data;
        }
        if (PureHttp.initConfig.beforeResponseCallback) {
          PureHttp.initConfig.beforeResponseCallback(response);
          return response.data;
        }
        return response.data;
      },
      (error: PureHttpError) => {
        const $error = error;
        $error.isCancelRequest = Axios.isCancel($error);
        // 关闭进度条动画
        NProgress.done();
        // 所有的响应异常 区分来源为取消请求/非取消请求
        if($error.response) {
          showErrorToast($error.response.status, $error.response.statusText ? $error.response.statusText : "请求错误")
        }
        return Promise.reject($error);
      }
    );
  }

  /** 通用请求工具函数 */
  public request<T>(
    method: RequestMethods,
    url: string,
    param?: AxiosRequestConfig,
    axiosConfig?: PureHttpRequestConfig
  ): Promise<T> {
    const config = {
      method,
      url,
      ...param,
      ...axiosConfig
    } as PureHttpRequestConfig;

    // 单独处理自定义请求/响应回掉
    return new Promise((resolve, reject) => {
      PureHttp.axiosInstance
        .request(config)
        .then((response: undefined) => {
          resolve(response);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  /** 单独抽离的post工具函数 */
  public post<T, P>(
    url: string,
    params?: AxiosRequestConfig<T>,
    config?: PureHttpRequestConfig
  ): Promise<P> {
    return this.request<P>("post", url, params, config);
  }

  /** 单独抽离的get工具函数 */
  public get<T, P>(
    url: string,
    params?: AxiosRequestConfig<T>,
    config?: PureHttpRequestConfig
  ): Promise<P> {
    return this.request<P>("get", url, params, config);
  }
}

export const http = new PureHttp();
