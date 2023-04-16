import { http } from "@/utils/http";

export default class SysUserService {
    page = (params?: object) => {
        return http.request("get", `/svc-seckill/api/operate/skGoods/page`, { params });
    }

    get = (id: number) => {
        return http.get(`/svc-seckill/operate/api/skGoods/${id}`);
    }

    save = (data: object) => {
        return http.request("post", `/svc-seckill/api/operate/skGoods`, { data });
    }

    delete = (id: number) => {
        return http.request("delete", `/svc-seckill/api/operate/skGoods/${id}`);
    }

    preheat = (id: number, data: object) => {
        return http.request("put", `/svc-seckill/api/operate/skGoods/${id}/preheat`, data);
    }

    loadCacheStock(id: number) {
        return http.request("put", `/svc-seckill/operate/skGoods/${id}/loadCacheStock`);
    }
}