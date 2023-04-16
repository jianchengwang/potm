import { http } from "@/utils/http";
import { getBlob, uploadBlob } from "@/utils/http/getBlob";

export default class LcCodeGenService {

  codeGenOutMap = (data: object) => {
      return http.request("post", `/svc-lowcode/api/operate/codeGen/genByTableOutMap`, { data });
    }
  
  codeGenOutZip = (data: object) => {
    return getBlob("post", `/svc-lowcode/api/operate/codeGen/genByTableOutZip`, {}, data, null);
    }
}