<template>
  <div class="component-upload-image">
    <ele-upload-video
      :headers="headers"
      :limit="limit"
      ref="imageUpload"
      :action="uploadImgUrl"
      :value="video"
      :fileSize="fileSize"
      :fileType="fileType"
      :response-fn="handleResponse"
      @delete="handleDel"
      :height="360"
    />
  </div>

</template>

<script>
import EleUploadVideo from "vue-ele-upload-video";
import { getToken } from "@/utils/auth";
import { delOss, listByIds } from "@/api/system/oss";

export default {
  components: {
    EleUploadVideo,
  },
  props: {
    value: [String, Object, Array],
    // 图片数量限制
    limit: {
      type: Number,
      default: 5,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 500,
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => ["mp4", "mp3", "avi",".wmv",".mpeg",".mpg",".mov"],
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      video: "",
      ossId: '',
      uploadImgUrl: process.env.VUE_APP_BASE_API + "/resource/oss/upload", // 上传的图片服务器地址
      number: 0,
      uploadList: [],
    };
  },
  methods: {
    handleUploadError(error) {
      console.log("error", error);
    },
    handleResponse(response) {
      console.log("response", response);
      if (response.code === 200) {
        this.video = response.data.url
        this.ossId = response.data.ossId
      }else{
        this.$modal.msgError(response.msg);

      }
    },
    // 删除图片
    handleDel(file) {
      if(this.ossId){
        delOss(this.ossId);
        this.video = ""
        this.ossId = ""
        this.$message.success("删除成功!");
      }

    },

  },
};
</script>

<style>
body {
  background-color: #f0f2f5;
}
</style>
