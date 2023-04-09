<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import { cdcLogPage, cdcLogRowDetails } from '@/api/cdcLog';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';

const tableTitle = ref("操作日志")
const tableColumns = [
  { field: 'id', header: 'ID' },
  { field: 'appName', header: '应用名' },
  { field: 'objTitle', header: '资源' },
  { field: 'act', header: '方法' },
  { field: 'path', header: '路径' },
  { field: 'args', header: '参数' },
  { field: 'costTime', header: '花费时间' },
  { field: 'operatorName', header: '操作者' },
  { field: 'logDateTime', header: '记录时间' },
]
const queryParam = ref({
  page: 1,
  size: 10,
  total: 0
})
const records = ref(null);
const rowDetailsDialog = ref(false);
const dt = ref(null);
const filters = ref({});

const logInfoId = ref(null);
const rowDetails = ref([]);
const expandedRows = ref([]);

const fetchData = () => {
  queryParam.value.filters = filters; 
  cdcLogPage(queryParam.value).then((res) => {
        if (res.status == 200) {
            records.value = res.data.records;
            queryParam.value.total = res.data.total;
        }
    });
};

onBeforeMount(() => {
    initFilters();
});
onMounted(() => {
  fetchData();
});
const formatCurrency = (value) => {
    return value.toLocaleString('en-US', { style: 'currency', currency: 'USD' });
};

const showRowDetails = (row) => {
  rowDetails.value = [];
  expandedRows.value = [];
  logInfoId.value = row.id;
  cdcLogRowDetails(logInfoId.value).then((res) => {
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

const onPage = (event) => {
  queryParam.value.page = event.page + 1;
  queryParam.value.size = event.rows;
  fetchData();
};

const initFilters = () => {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS }
    };
};
</script>

<template>
    <div class="grid">
        <div class="col-12">
            <div class="card">
                <Toast />
                <DataTable
                    ref="dt"
                    :value="records"
                    v-model:selection="selectedRecords"
                    dataKey="id"
                    :filters="filters"
                    responsiveLayout="scroll"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filters['global'].value" placeholder="Search..." />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-search" severity="success" aria-label="查看" @click="showRowDetails(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>
                <Paginator :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]" @page="onPage"></Paginator>

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
