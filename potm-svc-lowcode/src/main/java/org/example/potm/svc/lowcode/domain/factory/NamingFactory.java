package org.example.potm.svc.lowcode.domain.factory;

import java.util.Arrays;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
public class NamingFactory {
    public static String tableNameToClassName(String tableName) {
        String[] split = tableName.split("_");
        StringBuilder sb = new StringBuilder();
        Arrays.stream(split).forEach(s -> {
            sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        });
        return sb.toString();
    }

    public static String columnNameToPropertyName(String columnName) {
        String[] split = columnName.split("_");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<split.length; i++) {
            if(i == 0) {
                sb.append(split[i]);
            } else {
                sb.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1));
            }
        }
        return sb.toString();
    }

    public static String columnTypeToPropertyType(String columnType) {
        if(columnType.contains("bigint")) {
            return "Long";
        } else if(columnType.contains("int") || columnType.contains("smallint")) {
            return "Integer";
        } else if(columnType.contains("tinyint")) {
            return "Boolean";
        } else if(columnType.contains("datetime")) {
            return "LocalDateTime";
        } else if(columnType.contains("decimal")) {
            return "BigDecimal";
        } else {
            return "String";
        }
    }

    public static String getOutputPath(String templateFileName, String moduleName, String className, String backendPackage) {
        String backendPackagePath = backendPackage.replace(".", "/");
        String outputPath = templateFileName.replace("backend", backendPackagePath).replace(".ftl", "");
        if(outputPath.startsWith("frontend")) {
            outputPath = moduleName + "/" + outputPath;
        }
        String fileName = outputPath.split("\\/")[outputPath.split("\\/").length - 1];
        String extension = fileName.split("\\.")[fileName.split("\\.").length - 1];
        String path = outputPath.substring(0, outputPath.length() - fileName.length());
        fileName = fileName.substring(0, fileName.length() - extension.length() - 1);
        // 首字母大写
        fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
        fileName = String.format("%s%s.%s", className, fileName, extension);
        return String.format("%s%s", path, fileName);
    }
}
