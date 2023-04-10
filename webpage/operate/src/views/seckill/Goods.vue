<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import moment from 'moment';
import { skGoodsPage, skGoodsGet, skGoodsCreate, skGoodsPreheat, skGoodsLoadCacheStock } from '@/api/skGoods';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

const tableTitle = ref("秒杀商品")
const tableColumns = [
    { field: 'id', header: 'ID' },
    { field: 'goodsName', header: '商品名称' },
    { field: 'goodsPrice', header: '商品价格' },
    { field: 'skPrice', header: '秒杀价格' },
    { field: 'skNum', header: '秒杀数量' },
    { field: 'stockNum', header: '剩余库存' },
    { field: 'startTime', header: '开始时间' },
    { field: 'endTime', header: '截止时间' },
]
const queryParam = ref({
  page: 1,
  size: 10,
  total: 0,
})
const records = ref(null);
const formDialog = ref(false);
const deleteformDialog = ref(false);
const deleterecordsDialog = ref(false);
const form = ref({});
const selectedRecords = ref(null);
const dt = ref(null);
const filters = ref({});
const submitted = ref(false);
const statuses = ref([
    { label: 'INSTOCK', value: 'instock' },
    { label: 'LOWSTOCK', value: 'lowstock' },
    { label: 'OUTOFSTOCK', value: 'outofstock' }
]);

const fetchData = () => {
    queryParam.value.filters = filters; 
    skGoodsPage(queryParam.value).then((res) => {
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
};

const hideDialog = () => {
    formDialog.value = false;
    submitted.value = false;
};

const saveform = () => {
    submitted.value = true;
    if (form.value.goodsName && form.value.goodsName.trim()) {
        let formData = Object.assign({}, form.value);
        formData.startTime = moment(formData.satrtTime).format('YYYY-MM-DD HH:mm:ss');
        formData.endTime = moment(formData.endTime).format('YYYY-MM-DD HH:mm:ss');
        skGoodsCreate(formData).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '成功', detail: '创建成功', life: 3000 });
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

const confirmDeleteform = (editform) => {
    form.value = editform;
    deleteformDialog.value = true;
};

const deleteform = () => {
    records.value = records.value.filter((val) => val.id !== form.value.id);
    deleteformDialog.value = false;
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
                            <Button label="创建" icon="pi pi-plus" class="p-button-success mr-2" @click="openNew" />
                            <Button label="删除" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteSelected" :disabled="!selectedRecords || !selectedRecords.length" />
                        </div>
                    </template>

                    <template v-slot:end>
                        <FileUpload mode="basic" accept="image/*" :maxFileSize="1000000" label="导入" chooseLabel="导入" class="mr-2 inline-block" />
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

                    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-pencil" class="p-button-rounded p-button-success mr-2" @click="editform(slotProps.data)" />
                            <Button icon="pi pi-trash" class="p-button-rounded p-button-warning mt-2" @click="confirmDeleteform(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>
                <Paginator :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]" @page="onPage"></Paginator>

                <Dialog v-model:visible="formDialog" :style="{ width: '450px' }" header="商品信息" :modal="true" class="p-fluid">
                    <img :src="'demo/images/form/' + form.image" :alt="form.image" v-if="form.image" width="150" class="mt-0 mx-auto mb-5 block shadow-2" />
                    <div class="field">
                        <label for="goodsName">商品名称</label>
                        <InputText id="goodsName" v-model.trim="form.goodsName" required="true" autofocus :class="{ 'p-invalid': submitted && !form.goodsName }" />
                        <small class="p-invalid" v-if="submitted && !form.goodsName">商品名称必填</small>
                    </div>
                    <div class="field">
                        <label for="price">商品价格</label>
                        <InputNumber id="price" v-model="form.goodsPrice" mode="currency" currency="USD" locale="en-US" :class="{ 'p-invalid': submitted && !form.goodsPrice }" :required="true" />
                        <small class="p-invalid" v-if="submitted && !form.goodsPrice">商品价格必填</small>

                    </div>
                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="price">秒杀价格</label>
                            <InputNumber id="price" v-model="form.skPrice" mode="currency" currency="USD" locale="en-US" :class="{ 'p-invalid': submitted && !form.skPrice }" :required="true" />
                            <small class="p-invalid" v-if="submitted && !form.skPrice">秒杀价格必填</small>
                        </div>
                        <div class="field col">
                            <label for="skNum">秒杀数量</label>
                            <InputNumber id="skNum" v-model="form.skNum" required="true" :class="{ 'p-invalid': submitted && !form.skNum }" />
                            <small class="p-invalid" v-if="submitted && !form.skNum">秒杀数量必填</small>
                        </div>
                    </div>

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="price">开始时间</label>
                            <Calendar v-model="form.startTime" showTime dateFormat="yy-mm-dd" hourFormat="24" :class="{ 'p-invalid': submitted && !form.startTime }" :required="true" />
                            <small class="p-invalid" v-if="submitted && !form.startTime">开始时间必填</small>
                        </div>
                        <div class="field col">
                            <label for="price">截止时间</label>
                            <Calendar v-model="form.endTime" showTime dateFormat="yy-mm-dd" hourFormat="24" :class="{ 'p-invalid': submitted && !form.endTime }" :required="true" />
                            <small class="p-invalid" v-if="submitted && !form.endTime">截止时间必填</small>
                        </div>
                    </div>

                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="entryKey">访问密钥</label>
                            <InputText id="entryKey" v-model="form.entryKey"/>
                        </div>
                        <div class="field col">
                            <label for="buyLimit">购买限制</label>
                            <InputNumber id="buyLimit" v-model="form.buyLimit" integeronly />
                        </div>
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="hideDialog" />
                        <Button label="保存" icon="pi pi-check" class="p-button-text" @click="saveform" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="deleteformDialog" :style="{ width: '450px' }" header="Confirm" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="form"
                            >Are you sure you want to delete <b>{{ form.name }}</b
                            >?</span
                        >
                    </div>
                    <template #footer>
                        <Button label="No" icon="pi pi-times" class="p-button-text" @click="deleteformDialog = false" />
                        <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="deleteform" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="deleterecordsDialog" :style="{ width: '450px' }" header="Confirm" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="form">Are you sure you want to delete the selected records?</span>
                    </div>
                    <template #footer>
                        <Button label="No" icon="pi pi-times" class="p-button-text" @click="deleterecordsDialog = false" />
                        <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="deleteselectedRecords" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
