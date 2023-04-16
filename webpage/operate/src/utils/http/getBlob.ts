import axios from "axios";
import { parse, stringify } from 'qs'
import { loadEnv } from "@build/index";
import { getToken } from "../auth";

const { VITE_PROXY_DOMAIN, VITE_PROXY_DOMAIN_REAL } = loadEnv();
const baseURL =
  process.env.NODE_ENV === "production"
    ? VITE_PROXY_DOMAIN_REAL
    : VITE_PROXY_DOMAIN;

const getBlob = async (method, fetchUrl, queryParam, dataParam, filename) => {
  const token = getToken();
  const { data, headers } = await axios({
    method: method,
    responseType: "blob",
    baseURL: baseURL,
    url: fetchUrl,
    headers: {
      Authorization: token
    },
    params: queryParam,
    paramsSerializer: {
      encode: parse,
      serialize: stringify
    },
    data: dataParam
  });
  console.log(headers);
  if (!filename) {
    filename = headers["filename"];
  }
  const blob = new Blob([data]);
  const reader = new FileReader();
  reader.readAsDataURL(blob);
  reader.onload = e => {
    const a = document.createElement("a");
    a.download = filename;
    a.href = e.target.result;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  };
};

const uploadBlob = async (fetchUrl, method, formData: FormData) => {
  const accessToken = getToken();
  return await axios({
    method: method,
    responseType: "json",
    url: baseURL + fetchUrl,
    headers: {
      Authorization: "Bearer " + accessToken,
      "Content-Type": "multipart/form-data"
    },
    data: formData
  });
};

export { getBlob, uploadBlob };
