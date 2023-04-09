import { fileURLToPath, URL } from 'node:url';

import dayjs from "dayjs";
import { resolve } from "path";
import pkg from "./package.json";
import { UserConfigExport, ConfigEnv, loadEnv } from "vite";
import { warpperEnv, regExps } from "./builds";
import vue from '@vitejs/plugin-vue';

/** 当前执行node命令时文件夹的地址（工作目录） */
const root: string = process.cwd();

/** 路径查找 */
const pathResolve = (dir: string): string => {
  return resolve(__dirname, ".", dir);
};

/** 设置别名 */
const alias: Record<string, string> = {
  "/@": pathResolve("src"),
  "@build": pathResolve("builds")
};

const { dependencies, devDependencies, name, version } = pkg;
const __APP_INFO__ = {
  pkg: { dependencies, devDependencies, name, version },
  lastBuildTime: dayjs(new Date()).format("YYYY-MM-DD HH:mm:ss")
};

export default ({ command, mode }: ConfigEnv): UserConfigExport => {
  const {
    VITE_PORT,
    VITE_LEGACY,
    VITE_PUBLIC_PATH,
    VITE_PROXY_DOMAIN,
    VITE_PROXY_DOMAIN_REAL
  } = loadEnv(mode, root);
  console.info("VITE_PORT", VITE_PORT);
  console.info("VITE_PROXY_DOMAIN", VITE_PROXY_DOMAIN);
  console.info("VITE_PROXY_DOMAIN_REAL", VITE_PROXY_DOMAIN_REAL);
  return {
    base: VITE_PUBLIC_PATH,
    root,
    resolve: {
      alias:  {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        "@build": pathResolve("builds")
      }
    },
    // 服务端渲染
    server: {
      // 是否开启 https
      https: false,
      // 端口号
      port: VITE_PORT,
      host: "0.0.0.0",
      // 本地跨域代理
      proxy:
        VITE_PROXY_DOMAIN_REAL.length > 0
          ? {
              [VITE_PROXY_DOMAIN]: {
                target: VITE_PROXY_DOMAIN_REAL,
                // ws: true,
                changeOrigin: true,
                rewrite: (path: string) => regExps(path, VITE_PROXY_DOMAIN)
              }
            }
          : null
    },
    plugins: [
      vue()
    ],
    optimizeDeps: {
      include: ["pinia", "vue-i18n", "lodash-es", "@vueuse/core", "dayjs"],
      exclude: ["@pureadmin/theme/dist/browser-utils"]
    },
    build: {
      sourcemap: false,
      // 消除打包大小超过500kb警告
      chunkSizeWarningLimit: 4000
    },
    define: {
      __INTLIFY_PROD_DEVTOOLS__: false,
      __APP_INFO__: JSON.stringify(__APP_INFO__)
    }
  };
};
