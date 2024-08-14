<template>
  <a-form
    :model="formSearchParams"
    :style="{ marginBottom: '20px' }"
    layout="inline"
    @submit="doSearch"
  >
    <a-form-item field="appName" label="应用名">
      <a-input v-model="formSearchParams.appName" placeholder="请输入应用名" />
    </a-form-item>
    <a-form-item field="userProfile" label="审核人">
      <a-input-number
        v-model="formSearchParams.reviewerId"
        placeholder="审核人id"
      />
    </a-form-item>
    <a-form-item field="reviewStatus" label="审核状态">
      <a-select
        v-model="formSearchParams.reviewStatus"
        placeholder="选择审核状态"
        allow-clear
      >
        <a-option :value="0" label="待审核" />
        <a-option :value="1" label="通过" />
        <a-option :value="2" label="拒绝" />
      </a-select>
    </a-form-item>
    <a-form-item field="reviewStatus" label="评分策略">
      <a-select
        v-model="formSearchParams.scoringStrategy"
        placeholder="选择评分策略"
        allow-clear
      >
        <a-option :value="0" label="自定义" />
        <a-option :value="1" label="AI" />
      </a-select>
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit" style="width: 100px">
        搜索
      </a-button>
    </a-form-item>
  </a-form>
  <a-table
    :columns="columns"
    :data="dataList"
    :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.current,
      total,
    }"
    @page-change="onPageChange"
  >
    <template #appIcon="{ record }">
      <a-image width="64" :src="record.appIcon" />
    </template>

    <template #reviewStatus="{ record }">
      <a-tag v-if="record.reviewStatus == 0" color="yellow">待审核</a-tag>
      <a-tag
        v-else-if="record.reviewStatus == 1"
        if="record.reviewStatus==0"
        color="green"
        >通过
      </a-tag>
      <a-tag v-else color="red">拒绝</a-tag>
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button
          v-if="record.reviewStatus == 0"
          status="success"
          @click="doReviewer(record)"
          >审核
        </a-button>
        <a-button
          v-else-if="record.reviewStatus == 1"
          status="warning"
          @click="doReviewer(record)"
          >下架
        </a-button>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { deleteUserUsingPost } from "@/api/userController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import {
  deleteAppUsingPost,
  doAppReviewUsingPost,
  listAppByPageUsingPost,
} from "@/api/appController";
import dayjs from "dayjs";

const formSearchParams = ref<API.AppQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.App[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listAppByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 删除
 * @param record
 */
const doDelete = async (record: API.App) => {
  if (!record.id) {
    return;
  }

  const res = await deleteAppUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};
/**
 * 审核
 * @param record
 */
const doReviewer = async (record: API.App) => {
  if (!record.id) {
    return;
  }

  const res = await doAppReviewUsingPost({
    id: record.id,
    reviewStatus:
      record.reviewStatus === 0
        ? 1
        : record.reviewStatus === 1
        ? 2
        : record.reviewStatus,
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("审核失败，" + res.data.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "创建用户",
    dataIndex: "userId",
  },
  {
    title: "应用名",
    dataIndex: "appName",
  },
  {
    title: "应用图标",
    dataIndex: "appDesc",
  },
  {
    title: "应用头像",
    dataIndex: "appIcon",
    slotName: "appIcon",
  },
  {
    title: "应用类型",
    dataIndex: "appType",
  },
  {
    title: "评分策略",
    dataIndex: "scoringStrategy",
  },

  {
    title: "审核人",
    dataIndex: "reviewerId",
  },
  {
    title: "审核状态",
    dataIndex: "reviewStatus",
    slotName: "reviewStatus",
  },
  {
    title: "审核信息",
    dataIndex: "reviewMessage",
  },
  {
    title: "审核时间",
    dataIndex: "reviewTime",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
