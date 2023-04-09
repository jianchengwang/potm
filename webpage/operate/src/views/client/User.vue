<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import { userPage } from '@/api/user';
import { generateUser, clearData } from '@/api/test';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

const tableTitle = ref("客户端用户")
const tableColumns = [
  { field: 'id', header: 'ID' },
  { field: 'username', header: '用户名' },
  { field: 'nickname', header: '昵称' },
  { field: 'mobile', header: '手机号' },
  { field: 'userStatus', header: '状态' },
]
const queryParam = ref({
  page: 1,
  size: 10,
  total: 0,
  userScope: 'CLIENT',
})
const records = ref(null);
const formDialog = ref(false);
const clearDataDialog = ref(false);
const deleterecordsDialog = ref(false);
const form = ref({});
const selectedRecords = ref(null);
const dt = ref(null);
const filters = ref({});
const submitted = ref(false);

const fetchData = () => {
  queryParam.value.filters = filters; 
    userPage(queryParam.value).then((res) => {
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

const genUser = () => {
    form.value = {};
    submitted.value = false;
    formDialog.value = true;
};

const hideDialog = () => {
    formDialog.value = false;
    submitted.value = false;
};

const saveform = () => {
    submitted.value = true;
    if (form.value.num > 0) {
        generateUser(form.value.num).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '操作成功', detail: '已生成模拟用户', life: 3000 });
                formDialog.value = false;
                form.value = {};
                fetchData();
            }
        });
    }
};

const editform = (editform) => {
    form.value = { ...editform };
    formDialog.value = true;
};

const findIndexById = (id) => {
    let index = -1;
    for (let i = 0; i < records.value.length; i++) {
        if (records.value[i].id === id) {
            index = i;
            break;
        }
    }
    return index;
};

const exportCSV = () => {
    dt.value.exportCSV();
};

const onPage = (event) => {
  queryParam.value.page = event.page + 1;
  queryParam.value.size = event.rows;
  fetchData();
};

const confirmClearData = () => {
    clearDataDialog.value = true;
};

const doClearData = () => {
    clearData().then((res) => {
        if(res.status == 200) {
            toast.add({ severity: 'success', summary: '操作成功', detail: '已清除数据', life: 3000 });
            clearDataDialog.value = false;
            form.value = {};
            fetchData();
        }
    });
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
                <Toolbar class="mb-4">
                    <template v-slot:start>
                        <div class="my-2">
                            <Button label="生成用户" icon="pi pi-plus" class="p-button-success mr-2" @click="genUser" />
                            <Button label="清空数据" icon="pi pi-trash" class="p-button-danger" @click="confirmClearData" />
                        </div>
                    </template>

                    <template v-slot:end>
                        <FileUpload mode="basic" accept="xls/*" :maxFileSize="1000000" label="导入" chooseLabel="导入" class="mr-2 inline-block" />
                        <Button label="导出" icon="pi pi-upload" class="p-button-help" @click="exportCSV($event)" />
                    </template>
                </Toolbar>

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
                            <h5 class="m-0">{{ tableTitle }}</h5>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filters['global'].value" placeholder="Search..." />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
                </DataTable>
                <Paginator :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]" @page="onPage"></Paginator>

                <Dialog v-model:visible="formDialog" :style="{ width: '450px' }" header="生成模拟用户" :modal="true" class="p-fluid">
                    <div class="field">
                        <label for="num" class="mb-3">生成数量</label>
                        <InputText id="num" v-model.trim="form.num" required="true" :class="{ 'p-invalid': submitted && !form.num }" />
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="hideDialog" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="saveform" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="clearDataDialog" :style="{ width: '450px' }" header="确认操作" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="form">确定清空所有测试数据</span>
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="clearDataDialog = false" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="doClearData" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
