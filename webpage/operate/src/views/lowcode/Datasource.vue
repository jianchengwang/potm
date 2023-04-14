<script setup>
import { FilterMatchMode, FilterOperator } from "primevue/api";
import { ref, reactive, onMounted, onBeforeMount } from "vue";
import Paginator from "primevue/paginator";
import moment from "moment";
import { useToast } from "primevue/usetoast";
const toast = useToast();

import {
  datasourcePage,
  datasourceFetchAll,
  datasourceSave,
  datasourceRefreshTable,
  datasourceTables,
} from "@/api/svc-lowcode/lcDatasource";

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
      editDatasoureForm(selectedDatasource.value);
    },
  },
]);
const onDatasourceClick = (event) => {
  datasourceMenu.value.show(event);
};
const fetchDatasource = () => {
  datasourceFetchAll().then((res) => {
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
  form.value = {};
  if (row) {
    datasourceForm.value = { ...row };
  }
  datasourceSumitted.value = false;
  datasourceDialog.value = true;
};
const hideDatasourceDialog = () => {
  datasourceDialog.value = false;
  datasourceSumitted.value = false;
};
const saveDatasourceForm = () => {
  datasourceSumitted.value = true;
  datasourceSave(datasourceForm.value).then((res) => {
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
const datasourceDeleteDialog = ref(false);
const deleteDatasource = (row) => {
  datasourceForm.value = row;
  datasourceDeleteDialog.value = true;
};

const tableColumns = [
  { field: "db", header: "数据库" },
  { field: "tableName", header: "表名", sortable: true },
  { field: "tableRemark", header: "表备注" },
];
const tableRecords = ref(null);

const fetchTableData = () => {
  if (selectedDatasource.value) {
    datasourceTables(selectedDatasource.value.id).then((res) => {
      tableRecords.value = res.data;
    });
  }
};

const refreshTable = () => {
  if (selectedDatasource.value) {
    datasourceRefreshTable(selectedDatasource.value.id).then((res) => {
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

const tableExpandedRows = ref([]);

onMounted(() => {
  fetchDatasource();
});
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <div class="grid">
          <div class="col-3">
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
          <div class="col-9">
            <div class="card">
              <Toast />
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
                    <div class="my-2">
                      <Button
                        label="重载表"
                        icon="pi pi-refresh"
                        class="p-button mr-2"
                        @click="refreshTable"
                      />
                    </div>

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
                    @click="hideDatasourceDialog"
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
                v-model:visible="datasourceDeleteDialog"
                :style="{ width: '450px' }"
                header="删除确认"
                :modal="true"
              >
                <div class="flex align-items-center justify-content-center">
                  <i
                    class="pi pi-exclamation-triangle mr-3"
                    style="font-size: 2rem"
                  />
                  <span v-if="form"
                    >确定删除 <b>{{ form.name }}</b
                    >?</span
                  >
                </div>
                <template #footer>
                  <Button
                    label="取消"
                    icon="pi pi-times"
                    class="p-button-text"
                    @click="datasourceDeleteDialog = false"
                  />
                  <Button
                    label="确定"
                    icon="pi pi-check"
                    class="p-button-text"
                    @click="deleteDatasource"
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
