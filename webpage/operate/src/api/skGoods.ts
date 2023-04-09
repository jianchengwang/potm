import { http } from "../utils/http";


export const skGoodsPage = (params?: object) => {
  return http.request("get", `/operate/skGoods/page`, { params });
};

export const skGoodsGet = (skGoodsId: number) => {
  return http.get(`/operate/skGoods/${skGoodsId}`);
};

export const skGoodsCreate = (data: object) => {
  return http.request("post", `/operate/skGoods/create`, { data });
};

export const skGoodsUpdate = (skGoodsId: number, data: object) => {
  return http.request("put", `/operate/skGoods/${skGoodsId}/update`, { data });
};

export const skGoodsDelete = (skGoodsId: number) => {
  return http.request("delete", `/operate/skGoods/${skGoodsId}`);
}

export const skGoodsPreheat = (skGoodsId: number, data: object) => {
  return http.request("put", `/operate/skGoods/${skGoodsId}/preheat`, data);
}

export const skGoodsLoadCacheStock = (skGoodsId: number) => {
  return http.request("put", `/operate/skGoods/${skGoodsId}/loadCacheStock`);
}