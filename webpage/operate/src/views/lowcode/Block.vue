<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import moment from 'moment';
import { lcBlockPage, lcBlockGet, lcBlockSave } from '@/api/lcBlock';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

import Codemirror from "codemirror-editor-vue3";
import "codemirror/mode/javascript/javascript.js";
import "codemirror/mode/css/css.js";
import "codemirror/theme/dracula.css";

const cmOptions = reactive({
  mode: "text/javascript",
  theme: "dracula", // Theme
  lineNumbers: true, // Show line number
  smartIndent: true, // Smart indent
  indentUnit: 2, // The smart indent unit is 2 spaces in length
  foldGutter: true, // Code folding
  styleActiveLine: true // Display the style of the selected row
});

const tableTitle = ref("代码块")
const tableColumns = [
    { field: 'id', header: 'ID' },
    { field: 'name', header: '名称' },
    { field: 'tags', header: '标签' },
    { field: 'createAt', header: '创建时间' }
]
const queryParam = ref({
  page: 1,
  size: 10,
  total: 0,
})
const records = ref(null);
const formDialog = ref(false);
const blockEditView = ref(true);
const blockPreviewDialog = ref(false);
const deleteFormDialog = ref(false);
const form = ref({});
const dt = ref(null);
const filters = ref({});
const submitted = ref(false);

const fetchData = () => {
    queryParam.value.filters = filters.value; 
    lcBlockPage(queryParam.value).then((res) => {
        queryParam.value.total = res.data.total;
        records.value = res.data.records;
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
    lcBlockSave(formData).then((res) => {
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

const confirmdeleteForm = (editForm) => {
    form.value = editForm;
    deleteFormDialog.value = true;
};

const deleteForm = () => {
    records.value = records.value.filter((val) => val.id !== form.value.id);
    deleteFormDialog.value = false;
    form.value = {};
    toast.add({ severity: 'success', summary: 'Successful', detail: 'form Deleted', life: 3000 });
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

const confirmDeleteSelected = () => {
    deleterecordsDialog.value = true;
};
const deleteselectedRecords = () => {
    records.value = records.value.filter((val) => !selectedRecords.value.includes(val));
    deleterecordsDialog.value = false;
    selectedRecords.value = null;
    toast.add({ severity: 'success', summary: 'Successful', detail: 'records Deleted', life: 3000 });
};

const openBlockPreview = (editForm) => {
    form.value = { ...editForm };
    blockPreviewDialog.value = true;
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
                    dataKey="id"
                    :filters="filters"
                    responsiveLayout="scroll"
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
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" />
                            </span>
                        </div>
                    </template>

                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-pencil" class="p-button-rounded p-button-success mr-2" @click="editForm(slotProps.data)" />
                            <Button icon="pi pi-trash" class="p-button-rounded p-button-warning mr-2" @click="confirmdeleteForm(slotProps.data)" />
                            <Button icon="pi pi-search" class="p-button-rounded p-button mt-2" @click="openBlockPreview(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>
                <Paginator :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]" @page="onPage"></Paginator>

                <Dialog v-model:visible="formDialog" maximizable :style="{ width: '90%', height: '85%' }" header="代码块信息" :modal="true" class="p-fluid">

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="name">名称</label>
                            <InputText id="name" v-model.trim="form.name" required="true" autofocus :class="{ 'p-invalid': submitted && !form.name }" />
                            <small class="p-invalid" v-if="submitted && !form.name">名称必填</small>                        </div>
                        <div class="field col">
                            <label for="tags">标签</label>
                            <InputText id="tags" v-model="form.tags" />
                        </div>
                    </div>

                    <div class="formgrid grid surface-card m-0">
                        <Codemirror v-if="blockEditView"
                            v-model:value="form.code"
                            :options="cmOptions"
                            border
                            placeholder=""
                            :height="auto"
                        />
                        <div v-else v-html="form.code" />
                    </div>
                    <template #footer>
                        <Button :label="blockEditView ? '预览' : '编辑'" icon="pi pi-search" class="p-button-text" @click="blockEditView = !blockEditView" />
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

                <Dialog v-model:visible="blockPreviewDialog" maximizable :style="{ width: '90%', height: '85%' }" header="代码块预览" :modal="true" class="p-fluid">
                    <BlockViewer :header="form.name" :code="form.code">
                        <div v-html="form.code" />
                    </BlockViewer>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
