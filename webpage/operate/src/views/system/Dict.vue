<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import { dictPage, dictLoadAll, dictItemList } from '@/api/dict';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';

const tableTitle = ref("操作日志")
const tableColumns = [
  { field: 'id', header: 'ID' },
  { field: 'svcName', header: '服务名' },
  { field: 'dictKey', header: '字典标识' },
  { field: 'description', header: '描述' },
  { field: 'remark', header: '备注' },
  { field: 'systemFlag', header: '系统字典' },
  { field: 'enumFlag', header: '枚举字典' },
]
const queryParam = ref({
})
const records = ref(null);
const dt = ref(null);
const filters = ref({});

const expandedRows = ref([]);

const fetchData = () => {
  queryParam.value.filters = filters; 
  dictLoadAll(queryParam.value).then((res) => {
        if (res.status == 200) {
            records.value = res.data;
        }
    });
};

onBeforeMount(() => {
    initFilters();
});
onMounted(() => {
  fetchData();
});
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
                    dataKey="id"
                    :filters="filters"
                    responsiveLayout="scroll"
                    paginator :rows="10" :rowsPerPageOptions="[10,20,30]"
                    v-model:expandedRows="expandedRows"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filters['global'].value" placeholder="Search..." />
                            </span>
                        </div>
                    </template>
                    <Column expander style="width: 5rem" />
                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
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
