<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

// import Codemirror from "codemirror-editor-vue3";
// import "codemirror/mode/javascript/javascript.js";
// import "codemirror/mode/css/css.js";
// import "codemirror/theme/dracula.css";
// const cmOptions = reactive({
//   mode: "text/javascript",
//   theme: "dracula", // Theme
//   lineNumbers: true, // Show line number
//   smartIndent: true, // Smart indent
//   indentUnit: 2, // The smart indent unit is 2 spaces in length
//   foldGutter: true, // Code folding
//   styleActiveLine: true, // Display the style of the selected row
//   readOnly: true
// });
// const codemirrorRef = ref(null);
import CodeHighlight from '@/components/CodeHighlight.vue';

import LcDatasourceService from '@/service/svc-lowcode/LcDatasourceService.ts';
import LcCodeGenService from '@/service/svc-lowcode/LcCodeGenService.ts';
const lcDatasourceService = new LcDatasourceService();
const lcCodeGenService = new LcCodeGenService();

const datasourceList = ref(null);
const selectedDatasource = ref(null);
const datasourceMenu = ref();
const datasourceMenuItems = ref([
  {
    label: "编辑",
    icon: "pi pi-fw pi-pencil",
    command: (event) => {
      editDatasoureForm(selectedDatasource.value);
    },
  },
  {
    label: "删除",
    icon: "pi pi-fw pi-trash",
    command: (event) => {
      confirmDeleteDatasource(selectedDatasource.value);
    },
  },
  {
    label: "刷新表",
    icon: "pi pi-fw pi-refresh",
    command: (event) => {
      refreshTable(selectedDatasource.value);
    },
  },
]);
const onDatasourceClick = (event) => {
  datasourceMenu.value.show(event);
};
const fetchDatasource = () => {
  lcDatasourceService.fetchAll().then((res) => {
    datasourceList.value = res.data;
    if (datasourceList.value.length && !selectedDatasource.value) {
      selectedDatasource.value = datasourceList.value[0];
      fetchTableData();
    }
  });
};
const datasourceDialog = ref(false);
const datasourceForm = ref({});
const datasourceSumitted = ref(false);
const editDatasoureForm = (row) => {
  if (row) {
    datasourceForm.value = { ...row };
  } else {
    datasourceForm.value = {};
  }
  datasourceSumitted.value = false;
  datasourceDialog.value = true;
};
const saveDatasourceForm = () => {
  datasourceSumitted.value = true;
  lcDatasourceService.save(datasourceForm.value).then((res) => {
    if (res.status == 200) {
      toast.add({
        severity: "success",
        summary: "成功",
        detail: "创建成功",
        life: 3000,
      });
      datasourceDialog.value = false;
      datasourceForm.value = {};
      fetchDatasource();
    }
  });
};
const confirmDeleteDatasource = (row) => {
  confirm
    .require({
      message: "确认删除吗？",
      header: "删除",
      icon: "pi pi-exclamation-triangle",
      accept: () => {
        lcDatasourceService.delete(row.id).then((res) => {
          if (res.status == 200) {
            toast.add({
              severity: "success",
              summary: "成功",
              detail: "删除成功",
              life: 3000,
            });
            fetchDatasource();
          }
        });
      },
    })
    .then((res) => {});
};

const tableColumns = [
  { field: "db", header: "数据库" },
  { field: "tableName", header: "表名", sortable: true },
  { field: "tableRemark", header: "表备注" }
];
const tableRecords = ref(null);
const tableExpandedRows = ref([]);
const fetchTableData = () => {
  if (selectedDatasource.value) {
    lcDatasourceService.getTables(selectedDatasource.value.id).then((res) => {
      tableRecords.value = res.data;
    });
  }
};

const refreshTable = (row) => {
  if (selectedDatasource.value) {
    lcDatasourceService.refreshTable(row.id).then((res) => {
      if (res.status == 200) {
        toast.add({
          severity: "success",
          summary: "成功",
          detail: "刷新表结构成功",
          life: 3000,
        });
        fetchTableData();
      }
    });
  }
};

const tableFilters = ref({});
const initTableFilters = () => {
  tableFilters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  };
};

onBeforeMount(() => {
  initTableFilters();
});

onMounted(() => {
  fetchDatasource();
});

const codeGenDialog = ref(false);
const codeGenForm = ref({});
const codeGenFormSubmitted = ref(false);
const codeGenPreviewDialog = ref(false);
const codeGenPreviewMap = ref([]);
const previewTableList = ref([]);
const targetTable = ref(null);
const codeGen = (row) => {
  codeGenForm.value = {
    tableName: row.tableName,
    tableIdList: [row.id]
  };
  codeGenFormSubmitted.value = false;
  codeGenPreviewDialog.value = false;
  codeGenDialog.value = true;
};
const codeGenOutZip = () => {
  codeGenFormSubmitted.value = true;
  lcCodeGenService.codeGenOutZip(codeGenForm.value).then((res) => {
    codeGenDialog.value = false;
    codeGenForm.value = {};
  });
};
const codeGenOutMap = () => {
  codeGenFormSubmitted.value = true;
  lcCodeGenService.codeGenOutMap(codeGenForm.value).then((res) => {
    if (res.status == 200) {
      previewTableList.value = res.data;
      targetTable.value = previewTableList.value[0];
      codeGenPreviewDialog.value = true;
    }
  });
};
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <div class="grid">
          <div class="col-2">
            <Panel header="数据源">
              <template #icons>
                <button
                  class="p-panel-header-icon p-link mr-2"
                  @click="editDatasoureForm"
                >
                  <span class="pi pi-plus"></span>
                </button>
                <button
                  class="p-panel-header-icon p-link mr-2"
                  @click="fetchDatasource"
                >
                  <span class="pi pi-refresh"></span>
                </button>
              </template>
              <Listbox
                v-model="selectedDatasource"
                :options="datasourceList"
                filter
                optionLabel="db"
                class="w-full"
                @contextmenu="onDatasourceClick"
                @change="fetchTableData"
              />
              <ContextMenu ref="datasourceMenu" :model="datasourceMenuItems" />
            </Panel>
          </div>
          <div class="col-10">
            <div class="card">
              <Toast />
              <ConfirmDialog />
              <DataTable
                ref="dt"
                :value="tableRecords"
                dataKey="id"
                :filters="tableFilters"
                responsiveLayout="scroll"
                paginator
                :rows="10"
                :rowsPerPageOptions="[10, 20, 30]"
                v-model:expandedRows="tableExpandedRows"
              >
                <template #header>
                  <div
                    class="flex flex-column md:flex-row md:justify-content-between md:align-items-center"
                  >

                    <span class="block mt-2 md:mt-0 p-input-icon-left">
                      <span class="block mt-2 md:mt-0 p-input-icon-left">
                        <i class="pi pi-search" />
                        <InputText
                          v-model="tableFilters['global'].value"
                          placeholder="Search..."
                        />
                      </span>
                    </span>
                  </div>
                </template>

                <Column expander style="width: 5rem" />
                <Column
                  v-for="col of tableColumns"
                  :key="col.field"
                  :field="col.field"
                  :header="col.header"
                  :sortable="col.sortable"
                >
                </Column>
                <Column headerStyle="min-width:10rem;">
                  <template #body="slotProps">
                    <Button
                      label="代码生成"
                      text
                      @click="codeGen(slotProps.data)"
                    />
                  </template>
                </Column>
                <template #expansion="slotProps">
                  <DataTable
                    :value="slotProps.data.columnList"
                    dataKey="id"
                    tableStyle="min-width: 60rem"
                  >
                    <Column field="columnName" header="字段名"></Column>
                    <Column field="columnType" header="字段类型"></Column>
                    <Column field="columnRemark" header="字段备注"></Column>
                  </DataTable>
                </template>
              </DataTable>

              <Dialog
                v-model:visible="datasourceDialog"
                maximizable
                :style="{ width: '60%', height: '40%' }"
                header="数据源配置"
                :modal="true"
                class="p-fluid"
              >
                <div class="formgrid grid">
                  <div class="field col">
                    <label for="db">数据库</label>
                    <InputText
                      id="db"
                      v-model.trim="datasourceForm.db"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid': datasourceSubmitted && !datasourceForm.db,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="datasourceSubmitted && !datasourceForm.name"
                      >数据库必填</small
                    >
                  </div>
                </div>

                <div class="formgrid grid">
                  <div class="field col">
                    <label for="username">用户名</label>
                    <InputText
                      id="username"
                      v-model.trim="datasourceForm.username"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid':
                          datasourceSubmitted && !datasourceForm.username,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="datasourceSubmitted && !datasourceForm.username"
                      >用户名必填</small
                    >
                  </div>
                  <div class="field col">
                    <label for="password">密码</label>
                    <Password
                      id="password"
                      v-model.trim="datasourceForm.password"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid':
                          datasourceSubmitted && !datasourceForm.password,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="datasourceSubmitted && !datasourceForm.password"
                      >密码必填</small
                    >
                  </div>
                </div>

                <div class="formgrid grid">
                  <div class="field col">
                    <label for="jdbc">JDBC</label>
                    <InputText
                      id="jdbc"
                      v-model.trim="datasourceForm.jdbc"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid':
                          datasourceSubmitted && !datasourceForm.jdbc,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="datasourceSubmitted && !datasourceForm.jdbc"
                      >jdbc必填</small
                    >
                  </div>
                </div>

                <template #footer>
                  <Button
                    label="取消"
                    icon="pi pi-times"
                    class="p-button-text"
                    @click="datasourceDialog = false"
                  />
                  <Button
                    label="保存"
                    icon="pi pi-check"
                    class="p-button-text"
                    @click="saveDatasourceForm"
                  />
                </template>
              </Dialog>

              <Dialog
                v-model:visible="codeGenDialog"
                maximizable
                :style="{ width: '60%' }"
                header="代码生成"
                :modal="true"
                class="p-fluid"
              >

              <div class="formgrid grid">
                  <div class="field col">
                    <label for="tableName">数据表</label>
                    <InputText
                      id="db"
                      v-model.trim="codeGenForm.tableName"
                      required="true"
                      autofocus
                      readOnly
                      :class="{
                        'p-invalid': codeGenFormSubmitted && !codeGenForm.tableName,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="codeGenFormSubmitted && !codeGenForm.tableName"
                      >数据表必填</small
                    >
                  </div>
                  <div class="field col">
                    <label for="tableIdList">后端包路径</label>
                    <InputText
                      id="db"
                      v-model.trim="codeGenForm.backendPackage"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid': codeGenFormSubmitted && !codeGenForm.backendPackage,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="codeGenFormSubmitted && !codeGenForm.backendPackage"
                      >后端包路径必填</small
                    >
                  </div>
                </div>
                <div class="formgrid grid">
                  <div class="field col">
                    <label for="svcName">服务名</label>
                    <InputText
                      id="db"
                      v-model.trim="codeGenForm.svcName"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid': codeGenFormSubmitted && !codeGenForm.svcName,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="codeGenFormSubmitted && !codeGenForm.svcName"
                      >服务名必填</small
                    >
                  </div>
                  <div class="field col">
                    <label for="tableIdList">模块名</label>
                    <InputText
                      id="db"
                      v-model.trim="codeGenForm.moduleName"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid': codeGenFormSubmitted && !codeGenForm.moduleName,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="codeGenFormSubmitted && !codeGenForm.moduleName"
                      >模块名必填</small
                    >
                  </div>
                  <div class="field col">
                    <label for="tableIdList">模块描述</label>
                    <InputText
                      id="db"
                      v-model.trim="codeGenForm.moduleComment"
                      required="true"
                      autofocus
                      :class="{
                        'p-invalid': codeGenFormSubmitted && !codeGenForm.moduleComment,
                      }"
                    />
                    <small
                      class="p-invalid"
                      v-if="codeGenFormSubmitted && !codeGenForm.moduleComment"
                      >模块描述必填</small
                    >
                  </div>
                </div>

                <template #footer>
                  <Button
                    label="取消"
                    icon="pi pi-times"
                    class="p-button-text"
                    @click="codeGenDialog = false"
                  />
                  <Button
                    label="预览"
                    icon="pi pi-search"
                    class="p-button-text"
                    @click="codeGenOutMap"
                  />
                  <Button
                    label="生成"
                    icon="pi pi-check"
                    class="p-button-text"
                    @click="codeGenOutZip"
                  />
                </template>
              </Dialog>

              <Dialog
                v-model:visible="codeGenPreviewDialog"
                maximizable
                :style="{ width: '80%', height: '80%' }"
                header="代码生成预览"
                :modal="true"
                class="p-fluid"
              >
                <div>

                <Splitter class="mb-5">
                    <SplitterPanel class="flex justify-content-center" :size="10">
                      <Listbox v-model="targetTable" :options="previewTableList" optionLabel="tableName" class="w-full" />
                    </SplitterPanel>
                    <SplitterPanel class="flex" :size="90">
                      <TabView>
                        <TabPanel :header="ftl.ftlName" v-for="ftl in targetTable.ftlList">
                           <!-- <Codemirror 
                              ref="codemirrorRef"
                              :value="ftl.ftlContent"
                              :options="cmOptions"
                              border
                              placeholder=""
                              min-height="400px"
                          /> -->
                          <CodeHighlight class="surface-card m-0">{{ftl.ftlContent}}</CodeHighlight>
                        </TabPanel>
                    </TabView>
                    </SplitterPanel>
                </Splitter>
              
                </div>

                <template #footer>
                  <Button
                    label="取消"
                    icon="pi pi-times"
                    class="p-button-text"
                    @click="codeGenPreviewDialog = false"
                  />
                  <Button
                    label="确定生成"
                    icon="pi pi-check"
                    class="p-button-text"
                    @click="codeGenOutZip"
                  />
                </template>
              </Dialog>
              </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import "@/assets/demo/styles/badges.scss";
</style>
