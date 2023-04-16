<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
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

import LcBlockService from '@/service/svc-lowcode/LcBlockService.ts';
const lcBlockService = new LcBlockService();

const fetchTableData = () => {
//   tableConfig.query.filters = tableConfig.filters; 
  lcBlockService.page(tableConfig.query).then((res) => {
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
  title: '代码块',
  columns: [
    { field: 'id', header: 'ID', sortable: true },
    { field: 'name', header: '名称', filterField: 'name' },
    { field: 'tags', header: '标签', filterField: 'tags' },
    { field: 'createAt', header: '创建时间' }
  ],
  query: {
    page: 1,
    size: 10,
    total: 0,
    sort: 'id:desc'
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

const formDialog = ref(false);
const form = ref({});
const submitted = ref(false);

const blockEditView = ref(true);
const blockPreviewDialog = ref(false);

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

const editForm = (row) => {
    if(row) {
        form.value = { ...row };
    } else {
        form.value = {};
    }
    formDialog.value = true;
    blockEditView.value = true;
};

const saveForm = () => {
    submitted.value = true;
    let formData = Object.assign({}, form.value);
    lcBlockService.save(formData).then((res) => {
        if (res.status == 200) {
            toast.add({ severity: 'success', summary: '成功', detail: '创建成功', life: 3000 });
            formDialog.value = false;
            form.value = {};
            fetchTableData();
        }
    });
};

const confirmDelete = (row) => {
    confirm.require({
        message: '请确认是否删除该数据？',
        header: '确认操作',
        icon: 'pi pi-exclamation-triangle',
        acceptClass: 'p-button-danger',
        acceptLabel: '确定',
        rejectLabel: '取消',
        accept: () => {
            lcBlockService.delete(row.id).then((res) => {
                if (res.status == 200) {
                    toast.add({ severity: 'success', summary: 'Successful', detail: '删除成功', life: 3000 });
                    fetchTableData();
                }
            });
        },
        reject: () => {
        }
    });
};

const openBlockPreview = (row) => {
    form.value = { ...row };
    blockPreviewDialog.value = true;
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
                            <div class="my-2">
                                <Button label="创建" icon="pi pi-plus" text @click="editForm" />
                            </div>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" @click="fetchTableData" />
                            </span>
                        </div>
                    </template>

                    <Column v-for="col of tableConfig.columns" 
                        :key="col.field" :field="col.field" :header="col.header" 
                        lazy paginator
                        :sortable="col.sortable" 
                        >
                    </Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button label="编辑" text  @click="editForm(slotProps.data)" />
                            <Button severity="danger" label="删除" text @click="confirmDelete(slotProps.data)" />
                            <Button label="预览" text plain @click="openBlockPreview(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="formDialog" maximizable :style="{ width: '90%', height: '85%' }" :header="tableConfig.title" :modal="true" class="p-fluid">

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
                            :height="450"
                        />
                        <div v-else v-html="form.code" />
                    </div>
                    <template #footer>
                        <Button :label="blockEditView ? '预览' : '编辑'" icon="pi pi-search" class="p-button-text" @click="blockEditView = !blockEditView" />
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="formDialog = false" />
                        <Button label="保存" icon="pi pi-check" class="p-button-text" @click="saveForm" />
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
