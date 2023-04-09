<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import Paginator from 'primevue/paginator';
import { userPage, userPost, userPut } from '@/api/user';
import { useToast } from 'primevue/usetoast';
const toast = useToast();

const tableTitle = ref("运营人员")
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
  userScope: 'OPERATE',
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
    if (form.value.id) {
        userPut(form.value.id, form.value).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '操作成功', detail: '更新成功', life: 3000 });
                formDialog.value = false;
                form.value = {};
                fetchData();
            }
        });
    } else {
        userPost(form.value).then((res) => {
            if (res.status == 200) {
                toast.add({ severity: 'success', summary: '操作成功', detail: '新增成功', life: 3000 });
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
                            <div class="my-2">
                                <Button label="新增" icon="pi pi-plus" class="p-button-success mr-2" @click="openNew" />
                            </div>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="filters['global'].value" placeholder="Search..." />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableColumns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-pencil" class="p-button-rounded p-button-success mr-2" @click="editform(slotProps.data)" />
                            <Button icon="pi pi-trash" class="p-button-rounded p-button-warning mt-2" @click="confirmDeleteform(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>
                <Paginator :rows="queryParam.size" :totalRecords="queryParam.total" :rowsPerPageOptions="[10, 20, 30]" @page="onPage"></Paginator>

                <Dialog v-model:visible="formDialog" :style="{ width: '450px' }" header="运营人员信息" :modal="true" class="p-fluid">
                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="username">用户名</label>
                            <InputText id="username" v-model="form.username" :class="{ 'p-invalid': submitted && !form.username }" :required="true" />
                            <small class="p-invalid" v-if="submitted && !form.skPrice">用户名必填</small>
                        </div>
                        <div class="field col">
                            <label for="password">密码</label>
                            <Password v-model="form.password" toggleMask />
                        </div>
                    </div>
                    <div class="formgrid grid">
                        <div class="field col">
                            <label for="nickname">昵称</label>
                            <InputText id="nickname" v-model="form.nickname" :class="{ 'p-invalid': submitted && !form.nickname }" :required="true" />
                            <small class="p-invalid" v-if="submitted && !form.nickname">昵称必填</small>
                        </div>
                        <div class="field col">
                            <label for="mobile">手机号</label>
                            <InputText id="mobile" v-model="form.mobile" required="true" :class="{ 'p-invalid': submitted && !form.mobile }" />
                            <small class="p-invalid" v-if="submitted && !form.mobile">手机号必填</small>
                        </div>
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="hideDialog" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="saveform" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
