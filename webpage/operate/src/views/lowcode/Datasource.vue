<script setup>
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import moment from 'moment';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

import { datasourcePage, datasourceGet, datasourceSave, datasourceRefreshTable } from '@/api/svc-lowcode/datasource';


const tableTitle = ref("数据源")
const tableColumns = [
    { field: 'id', header: 'ID', sortable: true },
    { field: 'db', header: '数据库', filterField: 'db' },
    { field: 'jdbc', header: 'jdbc'},
    { field: 'createAt', header: '创建时间' }
]
const queryParam = ref({
  page: 1,
  size: 10,
  total: 0,
  sort: 'id:desc'
})
const records = ref(null);
const formDialog = ref(false);
const blockEditView = ref(true);
const blockPreviewDialog = ref(false);
const deleteFormDialog = ref(false);

const form = ref({});
const dt = ref(null);
const filters = ref({});
const globalFilterFields = ref(['db']);
const submitted = ref(false);

const fetchData = () => {
    queryParam.value.filters = simpleFilters(filters.value); 
    datasourcePage(queryParam.value).then((res) => {
        queryParam.value.total = res.data.total;
        records.value = res.data.records;
    });
};

const convertMatchMode = (matchMode) => {
    console.info(matchMode)
    switch(matchMode) {
        case 'contains':
            return 'in';
        case 'startsWith':
            return 'sw';
        case 'not contains':
            return 'in';
        case 'endsWith':
            return 'ew';
        case 'equals':
            return 'eq';
        case 'not Equals':
            return 'ne';
        default:
            return 'eq';
    }
}

const simpleFilters = () => {
    let filterQuery = "";
    if(filters.value) {
        globalFilterFields.value.forEach(element => {
            if(filters.value[element] && filters.value[element][element]) {
                filterQuery += element + " " + convertMatchMode(filters.value[element].matchMode) + " " + filters.value[element][element] + ";";
            }
        });
    }
    console.info(filterQuery);
    return filterQuery;
}

onBeforeMount(() => {
    initFilters();
});
onMounted(() => {
  fetchData();
});

const openNew = () => {
    form.value = {};
    submitted.value = false;
    formDialog.value = true;
    blockEditView.value = true;
};

const hideDialog = () => {
    formDialog.value = false;
    submitted.value = false;
};

const saveForm = () => {
    submitted.value = true;
    let formData = Object.assign({}, form.value);
    datasourceSave(formData).then((res) => {
        if (res.status == 200) {
            toast.add({ severity: 'success', summary: '成功', detail: '创建成功', life: 3000 });
            formDialog.value = false;
            form.value = {};
            fetchData();
        }
    });
    
};

const editForm = (editForm) => {
    form.value = { ...editForm };
    formDialog.value = true;
    blockEditView.value = true;
};

const confirmDeleteForm = (editForm) => {
    form.value = editForm;
    deleteFormDialog.value = true;
};

const deleteForm = () => {
    records.value = records.value.filter((val) => val.id !== form.value.id);
    deleteFormDialog.value = false;
    form.value = {};
    toast.add({ severity: 'success', summary: 'Successful', detail: 'form Deleted', life: 3000 });
};

const exportCSV = () => {
    dt.value.exportCSV();
};

const onPage = (event) => {
  queryParam.value.page = event.page + 1;
  queryParam.value.size = event.rows;
  fetchData();
};

const onSort = (event) => {
    if(event.sortField) {
        const sort = event.sortField + ':' + (event.sortOrder > 0 ?'asc':'desc');
        queryParam.value.sort = sort;
    } else {
        queryParam.value.sort = 'id:desc';
    }
    fetchData();
};
const onFilter = () => {
    fetchData();
};

const confirmDeleteSelected = () => {
    deleterecordsDialog.value = true;
};
const deleteselectedRecords = () => {
    records.value = records.value.filter((val) => !selectedRecords.value.includes(val));
    deleterecordsDialog.value = false;
    selectedRecords.value = null;
    toast.add({ severity: 'success', summary: 'Successful', detail: 'records Deleted', life: 3000 });
};

const refreshTable = (row) => {
    datasourceRefreshTable(row.id).then((res) => {
        if (res.status == 200) {
            toast.add({ severity: 'success', summary: '成功', detail: '刷新表结构成功', life: 3000 });
            fetchData();
        }
    });
};

const initFilters = () => {
    filters.value = {
        name: { value: '', matchMode: 'contains' },
        tags: { value: '', matchMode: 'contains' },
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
                    responsiveLayout="scroll"
                    lazy paginator
                    v-model:filters="filters"
                    :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]"
                    filterDisplay="menu"
                    @page="onPage"
                    @sort="onSort($event)" 
                    @filter="onFilter($event)"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <div class="my-2">
                                <Button label="创建" icon="pi pi-plus" class="p-button-success mr-2" @click="openNew" />
                            </div>
                            <!-- <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filters['global'].value" placeholder="Search..." />
                            </span> -->

                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <Button severity="secondary" icon="pi pi-upload" text rounded aria-label="导入" />
                                <Button severity="secondary" icon="pi pi-download" text rounded aria-label="导出" />
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" @click="fetchData" />
                            </span>
                        </div>
                    </template>

                    <Column v-for="col of tableColumns" 
                        :key="col.field" :field="col.field" :header="col.header" 
                        lazy paginator
                        :sortable="col.sortable" 
                        :filterField="col.filterField" 
                        filterDisplay="row"
                        :globalFilterFields="globalFilterFields"
                        >
                        <template #filter="{ filterModel, filterCallback }" v-if="globalFilterFields.includes(col.filterField)">
                            <InputText v-model="filterModel.db" type="text"  @keydown.enter="filterCallback()" class="p-column-filter" placeholder="" v-if="col.filterField == 'db'" filterMatchMode="contains" />
                        </template>
                    </Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-pencil" class="p-button-rounded p-button-success mr-2" @click="editForm(slotProps.data)" />
                            <Button icon="pi pi-trash" class="p-button-rounded p-button-warning mr-2" @click="confirmDeleteForm(slotProps.data)" />
                            <Button icon="pi pi-refresh" class="p-button-rounded p-button mr-2" @click="refreshTable(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="formDialog" maximizable :style="{ width: '60%', height: '40%' }" :header="tableTitle + '信息'" :modal="true" class="p-fluid">

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="db">数据库</label>
                            <InputText id="db" v-model.trim="form.db" required="true" autofocus :class="{ 'p-invalid': submitted && !form.db }" />
                            <small class="p-invalid" v-if="submitted && !form.name">数据库必填</small>                        </div>
                    </div>

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="username">用户名</label>
                            <InputText id="username" v-model.trim="form.username" required="true" autofocus :class="{ 'p-invalid': submitted && !form.username }" />
                            <small class="p-invalid" v-if="submitted && !form.username">用户名必填</small>                        
                        </div>
                        <div class="field col">
                            <label for="password">密码</label>
                            <Password id="password" v-model.trim="form.password" required="true" autofocus :class="{ 'p-invalid': submitted && !form.password }" />
                            <small class="p-invalid" v-if="submitted && !form.password">密码必填</small>                        
                        </div>
                    </div>

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="jdbc">JDBC</label>
                            <InputText id="jdbc" v-model.trim="form.jdbc" required="true" autofocus :class="{ 'p-invalid': submitted && !form.jdbc }" />
                            <small class="p-invalid" v-if="submitted && !form.jdbc">jdbc必填</small>                        
                        </div>
                    </div>

                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="hideDialog" />
                        <Button label="保存" icon="pi pi-check" class="p-button-text" @click="saveForm" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="deleteFormDialog" :style="{ width: '450px' }" header="删除确认" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="form"
                            >确定删除 <b>{{ form.name }}</b
                            >?</span
                        >
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="deleteFormDialog = false" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="deleteForm" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
