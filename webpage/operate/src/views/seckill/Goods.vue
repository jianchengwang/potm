<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import moment from 'moment';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

import SkGoodsService from '@/service/svc-seckill/SkGoodsService';
const skGoodsService = new SkGoodsService();

const fetchTableData = () => {
    // tableConfig.query.filters = tableConfig.filters; 
    skGoodsService.page(tableConfig.query).then((res) => {
        tableConfig.query.total = res.data.total;
        tableConfig.tableRecords = res.data.records;
    });
};

const tableConfig = reactive({
  title: '秒杀商品',
  columns: [
    { field: 'id', header: 'ID' },
    { field: 'goodsName', header: '商品名称' },
    { field: 'goodsPrice', header: '商品价格' },
    { field: 'skPrice', header: '秒杀价格' },
    { field: 'skNum', header: '秒杀数量' },
    { field: 'stockNum', header: '剩余库存' },
    { field: 'startTime', header: '开始时间' },
    { field: 'endTime', header: '截止时间' },
  ],
  query: {
    page: 1,
    size: 10,
    total: 0,
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

onBeforeMount(() => {
    tableConfig.filters = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    };
});
onMounted(() => {
    fetchTableData();
});

const formDialog = ref(false);
const form = ref({});
const submitted = ref(false);
const editForm = (row) => {
    if(row) {
        form.value = { ...row };
    } else {
        form.value = {};
    }
    formDialog.value = true;
    submitted.value = false;
};
const saveform = () => {
    submitted.value = true;
    if (form.value.goodsName && form.value.goodsName.trim()) {
        let formData = Object.assign({}, form.value);
        formData.startTime = moment(formData.satrtTime).format('YYYY-MM-DD HH:mm:ss');
        formData.endTime = moment(formData.endTime).format('YYYY-MM-DD HH:mm:ss');
        skGoodsService.save(formData).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '成功', detail: '创建成功', life: 3000 });
                formDialog.value = false;
                form.value = {};
                fetchTableData();
            }
        });
    }
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
            skGoodsService.delete(row.id).then((res) => {
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
</script>

<template>
    <div class="grid">
        <div class="col-12">
            <div class="card">
                <Toast />
                <ConfirmDialog />
                <DataTable
                    :ref="tableConfig.dt"
                    :value="tableConfig.tableRecords"
                    dataKey="id"
                    :filters="tableConfig.filters"
                    responsiveLayout="scroll"
                    lazy paginator :rows="tableConfig.query.size" :totalRecords="tableConfig.query.total" :rowsPerPageOptions="[10, 20, 30]" @page="tableConfig.onPage"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <div class="m-0">
                                <Button label="创建" icon="pi pi-plus" text @click="editForm" />
                            </div>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" @click="fetchTableData" />
                            </span>
                        </div>
                    </template>

                    <Column v-for="col of tableConfig.columns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button label="编辑" text @click="editForm(slotProps.data)" />
                            <Button label="删除" severity="danger" text class="btn-m2" @click="confirmDelete(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="formDialog" :style="{ width: '450px' }" header="商品信息" :modal="true" class="p-fluid">
                    <div class="field">
                        <label for="goodsName">商品名称</label>
                        <InputText id="goodsName" v-model.trim="form.goodsName" required="true" autofocus :class="{ 'p-invalid': submitted && !form.goodsName }" />
                        <small class="p-invalid" v-if="submitted && !form.goodsName">商品名称必填</small>
                    </div>
                    <div class="field">
                        <label for="price">商品价格</label>
                        <InputNumber id="price" v-model="form.goodsPrice" :class="{ 'p-invalid': submitted && !form.goodsPrice }" :required="true" />
                        <small class="p-invalid" v-if="submitted && !form.goodsPrice">商品价格必填</small>

                    </div>
                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="price">秒杀价格</label>
                            <InputNumber id="price" v-model="form.skPrice" :class="{ 'p-invalid': submitted && !form.skPrice }" :required="true" />
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
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="formDialog = false" />
                        <Button label="保存" icon="pi pi-check" class="p-button-text" @click="saveform" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
