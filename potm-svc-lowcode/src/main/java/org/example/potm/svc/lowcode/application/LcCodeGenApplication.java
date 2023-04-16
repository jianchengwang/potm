package org.example.potm.svc.lowcode.application;

import cn.hutool.core.io.IoUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.lowcode.domain.entity.CodeGen;
import org.example.potm.svc.lowcode.domain.entity.CodeGenTable;
import org.example.potm.svc.lowcode.domain.factory.NamingFactory;
import org.example.potm.svc.lowcode.domain.factory.TemplateFileConstant;
import org.example.potm.svc.lowcode.domain.repository.LcTableColumnRepository;
import org.example.potm.svc.lowcode.domain.repository.LcTableRepository;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;
import org.example.potm.svc.lowcode.interfaces.operate.dto.CodeGenByTableDTO;
import org.example.potm.svc.lowcode.interfaces.operate.vo.CodeGenPreviewTableFtlVO;
import org.example.potm.svc.lowcode.interfaces.operate.vo.CodeGenPreviewTableVO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcCodeGenApplication {

    private final LcTableRepository tableRepository;
    private final LcTableColumnRepository columnRepository;

    private Configuration cfg;
    {
        cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setTemplateLoader(new ClassTemplateLoader(LcCodeGenApplication.class, "/templates"));
        cfg.setDefaultEncoding("UTF-8");
    }

    public List<CodeGenPreviewTableVO> genByTable(CodeGenByTableDTO param, ZipOutputStream zip) throws IOException {

        List<CodeGenPreviewTableVO> previewTableList = new ArrayList<>();
        List<LcTable> tableList = tableRepository.listByIds(param.getTableIdList());
        for(LcTable table: tableList) {
            CodeGenPreviewTableVO previewTable = new CodeGenPreviewTableVO();
            previewTable.setTableName(table.getTableName());
            List<CodeGenPreviewTableFtlVO> previweTableFtlList = new ArrayList<>();
            table.setColumnList(columnRepository.selectByDatasourceIdAndTableId(table.getDatasourceId(), table.getId()));
            CodeGen codeGen = new CodeGen(param, new CodeGenTable(table));
            TemplateFileConstant.ftl_lsit.forEach(ftl -> {
                try {
                    String outputPath = NamingFactory.getOutputPath(ftl, codeGen.getModuleName(), codeGen.getTable().getClassName(), codeGen.getBackendPackage());
                    String fileName = outputPath.split("\\/")[outputPath.split("\\/").length - 1];
                    log.info("fileName: {}, path: {}", fileName, outputPath);
                    StringWriter sw = new StringWriter();
                    Template template = cfg.getTemplate(ftl);
                    template.process(codeGen, sw);
                    String ftlContent = sw.toString();
                    if (zip != null) {
                        try {
                            zip.putNextEntry(new ZipEntry(Objects.requireNonNull(outputPath)));
                            IoUtil.write(zip, StandardCharsets.UTF_8, false, ftlContent);
                            IoUtil.close(sw);
                        }
                        catch (ZipException zipException) {
                        }
                        zip.closeEntry();
                    }
                    CodeGenPreviewTableFtlVO previewTableFtl = new CodeGenPreviewTableFtlVO();
                    previewTableFtl.setFtlName(fileName);
                    previewTableFtl.setFtlContent(ftlContent);
                    previweTableFtlList.add(previewTableFtl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            previewTable.setFtlList(previweTableFtlList);
            previewTableList.add(previewTable);
        }
        return previewTableList;
    }
}
