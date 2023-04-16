<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

import TestService from '@/service/svc-core/TestService.ts';
import SysUserService from '@/service/svc-core/SysUserService.ts';
const testService = new TestService();
const sysUserService = new SysUserService();

const fetchTableData = () => {
//   tableConfig.query.filters = tableConfig.filters; 
  sysUserService.page(tableConfig.query).then((res) => {
    if (res.status == 200) {
        tableConfig.records = res.data.records;
        tableConfig.query.total = res.data.total;
        }
    });
};

const tableConfig = reactive({
  title: '客户端用户',
  columns: [
    { field: 'id', header: 'ID' },
    { field: 'username', header: '用户名' },
    { field: 'nickname', header: '昵称' },
    { field: 'mobile', header: '手机号' },
    { field: 'userStatusDictLabel', header: '状态' },
  ],
  query: {
    page: 1,
    size: 10,
    total: 0,
    userScope: 'CLIENT',
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

const generateUserDialog = ref(false);
const generateUserNum = ref(10);
const confirmGenerateUser = () => {
    if (generateUserNum.value > 0) {
        testService.generateUser(generateUserNum.value).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '操作成功', detail: '已生成模拟用户', life: 3000 });
                generateUserDialog.value = false;
                fetchTableData();
            }
        });
    }
};

const confirmClearData = () => {
    confirm.require({
        message: '请确认是否清空所有测试数据？',
        header: '确认操作',
        icon: 'pi pi-exclamation-triangle',
        acceptClass: 'p-button-danger',
        acceptLabel: '确定',
        rejectLabel: '取消',
        accept: () => {
            testService.clearData().then((res) => {
                if(res.status == 200) {
                    toast.add({ severity: 'success', summary: '操作成功', detail: '已清除数据', life: 3000 });
                    fetchData();
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
                    :value="tableConfig.records"
                    dataKey="id"
                    :filters="tableConfig.filters"
                    responsiveLayout="scroll"
                    lazy paginator :rows="tableConfig.query.size" :totalRecords="tableConfig.query.total" :rowsPerPageOptions="[10, 20, 30]" @page="tableConfig.onPage"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <div class="m-0">
                                <Button label="生成用户" icon="pi pi-plus" text class="p-button" @click="generateUserDialog = true" />
                                <Button label="清空数据" icon="pi pi-trash" text class="p-button-danger" @click="confirmClearData" />
                            </div>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <Button severity="secondary" icon="pi pi-refresh" text rounded aria-label="刷新" @click="fetchTableData" />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableConfig.columns" :key="col.field" :field="col.field" :header="col.header"></Column>
                </DataTable>

                <Dialog v-model:visible="generateUserDialog" :style="{ width: '450px' }" header="生成模拟用户" :modal="true" class="p-fluid">
                    <div class="field">
                        <label for="num" class="mb-3">生成数量</label>
                        <InputText id="num" v-model.trim="generateUserNum" required="true" :class="{ 'p-invalid': !generateUserNum }" />
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="generateUserDialog = false" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="confirmGenerateUser" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
