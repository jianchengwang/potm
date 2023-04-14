<script setup>
import { FilterMatchMode } from 'primevue/api';
import { ref, reactive, onMounted, onBeforeMount } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import { useToast } from 'primevue/usetoast';
const confirm = useConfirm();
const toast = useToast();

import SysUserService from '@/service/svc-core/SysUserService.ts';
const sysUserService = new SysUserService();

const fetchTableData = () => {
  tableConfig.query.filters = tableConfig.filters; 
  sysUserService.page(tableConfig.query).then((res) => {
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
  title: '系统用户',
  columns: [
    { field: 'id', header: 'ID' },
    { field: 'username', header: '用户名' },
    { field: 'nickname', header: '昵称' },
    { field: 'mobile', header: '手机号' },
    { field: 'userStatus', header: '状态' },
  ],
  query: {
    page: 1,
    size: 10,
    total: 0,
    userScope: 'OPERATE',
  },
  dt: null,
  records: null,
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
const userStatusOptionList = ref([
  { label: '正常', code: 'NORMAL' },
  { label: '冻结', code: 'FROZEN' },
  { label: '注销', code: 'LOGOUT' },
])
const selectedUserStatusOption = ref({})
const editForm = (row) => {
    if(row) {
        form.value = { ...row };
        selectedUserStatusOption.value = userStatusOptionList.value.find((item) => item.code == form.value.userStatus);
    } else {
        form.value = {};
        selectedUserStatusOption.value = {};
    }
    formDialog.value = true;
};
const saveForm = () => {
    submitted.value = true;
    if (selectedUserStatusOption.value) {
        form.value.userStatus = selectedUserStatusOption.value.code;
    }
    form.value.userScope = 'OPERATE';
    sysUserService.save(form.value).then((res) => {
        if (res.status == 200) {
            toast.add({ severity: 'success', summary: '操作成功', detail: '保存成功', life: 3000 });
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
            sysUserService.delete(row.id).then((res) => {
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
                            <div class="m-0">
                                <Button label="创建" icon="pi pi-plus" text @click="editForm" />
                            </div>
                            <span class="block mt-2 md:mt-0 p-input-icon-left">
                                <i class="pi pi-search" />
                                <InputText v-model="tableConfig.filters['global'].value" placeholder="Search..." />
                            </span>
                        </div>
                    </template>
                    <Column v-for="col of tableConfig.columns" :key="col.field" :field="col.field" :header="col.header"></Column>
                    <Column headerStyle="min-width:10rem;">
                        <template #body="slotProps">
                            <Button label="编辑" text @click="editForm(slotProps.data)" />
                            <Button label="删除" severity="warning" text class="btn-m2" @click="confirmDelete(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="formDialog" :style="{ width: '450px' }" :header="tableConfig.title" :modal="true" class="p-fluid">
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

                    <div class="field">
                        <label for="userStatus">状态</label>
                        <Dropdown v-model="selectedUserStatusOption" :options="userStatusOptionList" optionLabel="label" placeholder="请选择状态" class="w-ful" />
                    </div>
                    <template #footer>
                        <Button label="取消" icon="pi pi-times" class="p-button-text" @click="formDialog = false" />
                        <Button label="确定" icon="pi pi-check" class="p-button-text" @click="saveForm" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import '@/assets/demo/styles/badges.scss';
</style>
