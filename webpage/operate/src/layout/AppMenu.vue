<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

const model = ref([
    {
        label: '首页',
        items: [{ label: '仪表盘', icon: 'pi pi-fw pi-home', to: '/' }]
    },
    // {
    //     label: '客户数据',
    //     items: [
    //         { label: '客户信息', icon: 'pi pi-fw pi-id-card', to: '/client/user' },
    //     ]
    // },
    // {
    //     label: '秒杀模块',
    //     items: [
    //         { label: '秒杀商品', icon: 'pi pi-fw pi-share-alt', to: '/seckill/goods' },
    //         { label: '秒杀订单', icon: 'pi pi-fw pi-check-square', to: '/seckill/order' },
    //     ]
    // },
    {
        label: '系统管理',
        items: [
            { label: '用户管理', icon: 'pi pi-fw pi-circle', to: '/system/user' },
            { label: '操作日志', icon: 'pi pi-fw pi-chart-bar', to: '/system/cdcLog' },
            { label: '字典管理', icon: 'pi pi-fw pi-clone', to: '/system/dict' },
        ]
    },
    {
        label: '低代码',
        icon: 'pi pi-fw pi-bookmark',
        items: [
            { label: '数据源', icon: 'pi pi-fw pi-list', to: '/lowcode/datasource' },
            { label: '代码生成', icon: 'pi pi-fw pi-chart-bar', to: '/lowcode/codeGen' },
            { label: '代码块', icon: 'pi pi-fw pi-code', to: '/lowcode/block' },
            { label: '表单设计', icon: 'pi pi-fw pi-pencil', to: '/lowcode/formDesign' }
        ]
    },
    // {
    //     label: '运维监控',
    //     icon: 'pi pi-fw pi-globe',
    //     items: [
    //         { label: 'swagger', icon: 'pi pi-fw pi-list', url: 'http://localhost:9070/webjars/swagger-ui/index.html', target: '_blank' },
    //         { label: 'nacos', icon: 'pi pi-fw pi-comment', url: 'http://localhost:8848/nacos', target: '_blank' },
    //         { label: 'springbootadmin', icon: 'pi pi-fw pi-chart-bar', url: 'http://localhost:9060', target: '_blank' },
    //         { label: 'skywalking', icon: 'pi pi-fw pi-image', url: 'http://localhost:13800', target: '_blank' },
    //         { label: 'zipkin', icon: 'pi pi-fw pi-times-circle', url: 'http://localhost:9411/zipkin', target: '_blank' },
    //         { label: 'grafana', icon: 'pi pi-fw pi-circle', url: 'http://localhost:3000', target: '_blank' },
    //     ]
    // },
    {
        label: '通用页面',
        icon: 'pi pi-fw pi-briefcase',
        to: '/pages',
        items: [
            {
                label: 'Auth',
                icon: 'pi pi-fw pi-user',
                items: [
                    {
                        label: 'Login',
                        icon: 'pi pi-fw pi-sign-in',
                        to: '/auth/login'
                    },
                    {
                        label: 'Error',
                        icon: 'pi pi-fw pi-times-circle',
                        to: '/auth/error'
                    },
                    {
                        label: 'Access Denied',
                        icon: 'pi pi-fw pi-lock',
                        to: '/auth/access'
                    }
                ]
            },
            {
                label: 'Crud',
                icon: 'pi pi-fw pi-pencil',
                to: '/pages/crud'
            },
            {
                label: 'NotFound',
                icon: 'pi pi-fw pi-exclamation-circle',
                to: '/pages/notfound'
            },
            {
                label: 'Empty',
                icon: 'pi pi-fw pi-circle-off',
                to: '/pages/empty'
            }
        ]
    },
    {
        label: '模板组件',
        icon: 'pi pi-fw pi-file',
        items: [{
            label: 'UI Components',
            items: [
                { label: 'Form Layout', icon: 'pi pi-fw pi-id-card', to: '/uikit/formlayout' },
                { label: 'Input', icon: 'pi pi-fw pi-check-square', to: '/uikit/input' },
                { label: 'Float Label', icon: 'pi pi-fw pi-bookmark', to: '/uikit/floatlabel' },
                { label: 'Invalid State', icon: 'pi pi-fw pi-exclamation-circle', to: '/uikit/invalidstate' },
                { label: 'Button', icon: 'pi pi-fw pi-mobile', to: '/uikit/button', class: 'rotated-icon' },
                { label: 'Table', icon: 'pi pi-fw pi-table', to: '/uikit/table' },
                { label: 'List', icon: 'pi pi-fw pi-list', to: '/uikit/list' },
                { label: 'Tree', icon: 'pi pi-fw pi-share-alt', to: '/uikit/tree' },
                { label: 'Panel', icon: 'pi pi-fw pi-tablet', to: '/uikit/panel' },
                { label: 'Overlay', icon: 'pi pi-fw pi-clone', to: '/uikit/overlay' },
                { label: 'Media', icon: 'pi pi-fw pi-image', to: '/uikit/media' },
                { label: 'Menu', icon: 'pi pi-fw pi-bars', to: '/uikit/menu', preventExact: true },
                { label: 'Message', icon: 'pi pi-fw pi-comment', to: '/uikit/message' },
                { label: 'File', icon: 'pi pi-fw pi-file', to: '/uikit/file' },
                { label: 'Chart', icon: 'pi pi-fw pi-chart-bar', to: '/uikit/charts' },
                { label: 'Misc', icon: 'pi pi-fw pi-circle', to: '/uikit/misc' }
            ]
        },
        {
            label: 'Prime Blocks',
            items: [
                { label: 'Free Blocks', icon: 'pi pi-fw pi-eye', to: '/blocks', badge: 'NEW' },
                { label: 'All Blocks', icon: 'pi pi-fw pi-globe', url: 'https://www.primefaces.org/primeblocks-vue', target: '_blank' }
            ]
        },
        {
            label: 'Utilities',
            items: [
                { label: 'PrimeIcons', icon: 'pi pi-fw pi-prime', to: '/utilities/icons' },
                { label: 'PrimeFlex', icon: 'pi pi-fw pi-desktop', url: 'https://www.primefaces.org/primeflex/', target: '_blank' }
            ]
        },
    ]},
]);
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model" :key="item">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
