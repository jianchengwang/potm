<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

import SysDictService from '@/service/svc-core/SysDictService.ts';
const sysDictService = new SysDictService();

const fetchTableData = () => {
//   tableConfig.query.filters = tableConfig.filters; 
  sysDictService.fetchAll(tableConfig.query).then((res) => {
    if (res.status == 200) {
        tableConfig.records = res.data;
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
  title: '系统字典',
  columns: [
    { field: 'id', header: 'ID' },
    { field: 'svcName', header: '服务名' },
    { field: 'dictKey', header: '字典标识' },
    { field: 'description', header: '描述' },
    { field: 'remark', header: '备注' },
    { field: 'systemFlag', header: '系统字典' },
    { field: 'enumFlag', header: '枚举字典' },
  ],
  query: {
  },
  records: null,
  dt: null,
  filters: null
});
const expandedRows = ref([]);
</script>

<template>
    <div class="grid">
        <div class="col-12">
            <div class="card">
                <Toast />
                <DataTable
                    :ref="tableConfig.dt"
                    :value="tableConfig.records"
                    dataKey="id"
                    :filters="tableConfig.filters"
                    responsiveLayout="scroll"
                    paginator :rows="10" :rowsPerPageOptions="[10,20,30]"
                    v-model:expandedRows="expandedRows"
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
                    <Column expander style="width: 5rem" />
                    <Column v-for="col of tableConfig.columns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <template #expansion="slotProps">
                      <DataTable :value="slotProps.data.itemList" dataKey="id" tableStyle="min-width: 60rem">
                          <Column field="type" header="类型"></Column>
                          <Column field="value" header="字典项值"></Column>
                          <Column field="label" header="标签"></Column>
                          <Column field="description" header="描述"></Column>
                          <Column field="remark" header="备注"></Column>
                          <Column field="sortOrder" header="排序"></Column>
                      </DataTable>
                    </template>
                </DataTable>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
