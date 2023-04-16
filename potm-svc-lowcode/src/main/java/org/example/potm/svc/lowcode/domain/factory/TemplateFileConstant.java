package org.example.potm.svc.lowcode.domain.factory;

import java.util.Arrays;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
public interface TemplateFileConstant {
    String backend_application = "backend/application/application.java.ftl";
    String backend_domain_repository = "backend/domain/repository/repository.java.ftl";
    String backend_infrastructure_db_po = "backend/infrastructure/db/po/po.java.ftl";
    String backend_infrastructure_db_dao = "backend/infrastructure/db/dao/dao.java.ftl";
    String backend_infrastructure_repository = "backend/infrastructure/repository/repositoryImpl.java.ftl";
    String backend_interfaces_operate_query = "backend/interfaces/operate/query/query.java.ftl";
    String backend_interfaces_operate_api = "backend/interfaces/operate/api/controller.java.ftl";
    String mapper_xml = "mapper/dao.xml.ftl";
    String frontend_service = "frontend/service/service.ts.ftl";
    String frontend_view = "frontend/view/view.vue.ftl";

    List<String> ftl_lsit = Arrays.asList(
            backend_application,
            backend_domain_repository,
            backend_infrastructure_db_po,
            backend_infrastructure_db_dao,
            backend_infrastructure_repository,
            backend_interfaces_operate_query,
            backend_interfaces_operate_api,
            mapper_xml,
            frontend_service,
            frontend_view
    );

}
