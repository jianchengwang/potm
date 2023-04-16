<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';

import CdcLogService from '@/service/svc-core/CdcLogService.ts';
const cdcLogService = new CdcLogService();

const fetchTableData = () => {
  // tableConfig.query.filters = tableConfig.filters; 
  cdcLogService.page(tableConfig.query).then((res) => {
    if (res.status == 200) {
        tableConfig.records = res.data.records;
        tableConfig.query.total = res.data.total;
      }
    });
};

onBeforeMount(() => {
    tableConfig.filters = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    };
});
onMounted(() => {
    fetchTableData();
});

const tableConfig = reactive({
  title: '操作日志',
  columns: [
    { field: 'id', header: 'ID' },
    { field: 'svcName', header: '服务名' },
    { field: 'objTitle', header: '资源' },
    { field: 'act', header: '方法' },
    { field: 'path', header: '路径' },
    { field: 'args', header: '参数' },
    { field: 'costTime', header: '花费时间' },
    { field: 'operatorName', header: '操作者' },
    { field: 'logDateTime', header: '记录时间' },
  ],
  query: {
    page: 1,
    size: 10,
    total: 0
  },
  records: null,
  dt: null,
  filters: null,
  onPage: (event) => {
    tableConfig.query.page = event.page + 1;
    tableConfig.query.size = event.rows;
    fetchTableData();
  }
});

const rowDetailsDialog = ref(false);
const rowDetails = ref([]);
const expandedRows = ref([]);
const showRowDetails = (row) => {
  rowDetails.value = [];
  expandedRows.value = [];
  cdcLogService.getRowDetails(row.id).then((res) => {
    if (res.status == 200) {
      rowDetails.value = res.data;
      for(let rd of rowDetails.value) {
        if (rd.oldData) {
          rd.oldData = JSON.parse(rd.oldData);
        }
        if (rd.newData) {
          rd.newData = JSON.parse(rd.newData);
        }
      }
      rowDetailsDialog.value = true;
    }
  });
};
</script>

<template>
    <div class="grid">
        <div class="col-12">
            <div class="card">
              <ConfirmDialog />
              <Toast />
              <DataTable
              :ref="tableConfig.dt"
              :value="tableConfig.records"
              dataKey="id"
              :filters="tableConfig.filters"
              responsiveLayout="scroll"
              lazy paginator :rows="tableConfig.query.size" :totalRecords="tableConfig.query.total" :rowsPerPageOptions="[10, 20, 30]" @page="tableConfig.onPage"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="tableConfig.filters['global'].value" placeholder="Search..." />
                            </span>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" @click="fetchTableData" />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableConfig.columns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                          <Button label="查看" text @click="showRowDetails(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="rowDetailsDialog" :style="{ width: '50vw' }" header="变更详情记录" :modal="true" class="p-fluid"  >
                  <DataTable v-model:expandedRows="expandedRows" :value="rowDetails" dataKey="id" tableStyle="min-width: 60rem" paginator :rows="10" :rowsPerPageOptions="[10, 20, 30]">
                    <Column expander style="width: 5rem" />
                    <Column field="db" header="数据库"></Column>
                    <Column field="tableName" header="表名"></Column>
                    <Column field="operate" header="操作类型"></Column>
                    <Column field="rowId" header="主键值"></Column>
                    <Column field="xid" header="事务编号"></Column>
                    <Column field="logDateTime" header="记录时间"></Column>
                    <template #expansion="slotProps">
                      <Splitter class="mb-5">
                          <SplitterPanel class="flex align-items-center justify-content-center">
                            <VueJsonPretty :data="slotProps.data.oldData" />
                          </SplitterPanel>
                          <SplitterPanel class="flex align-items-center justify-content-center">
                            <VueJsonPretty :data="slotProps.data.newData" />
                          </SplitterPanel>
                      </Splitter>
                    </template>
                  </DataTable>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
