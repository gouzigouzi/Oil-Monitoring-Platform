<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb class="breadcrumb-container" />
 
    <div class="right-menu" >
      <!-- <span style="margin-right: 20px; display: flex; align-items: center; height: 100%;"><strong style="font-style: italic">
        用户：{{ username }}
      </strong></span> -->
      <el-dropdown class="avatar-container" trigger="click" @click.native="submitCountLoad">
        <div class="avatar-wrapper">
          <!-- <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar"> -->
          <el-avatar class="user-avatar"> {{username.substr(0,5)}} </el-avatar>
          <i class="el-icon-caret-bottom">
            <el-badge v-if="roles.includes('admin')" :is-dot="submitCount>0" :max="99"></el-badge>
          </i>
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/todo/index">
            <el-dropdown-item v-if="roles.includes('admin')">
              申请列表<el-badge v-if="submitCount>0" :value="submitCount" :max="99"/>
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="dialogVisible = true">
            <span style="display:block;">修改密码</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <change-password-dialog
        :title="'修改密码'"
        :dialogFormVisible.sync="dialogVisible"
        :ruleForm="form"
        @submits="handleSubmit"
        @reset="handleReset"
        ref="changeDialog"></change-password-dialog>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import store from '@/store'
import ChangePasswordDialog from './ChangePasswordDialog.vue'
import {todoCount} from '@/api/todo'

export var websocket = null;

export default {
  components: {
    Breadcrumb,
    Hamburger,
    ChangePasswordDialog
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  created() {
    this.username = store.getters.name;
    this.roles = store.getters.roles;
    this.initWebSocket();
    this.submitCountLoad();
    console.log('username:', this.username);
    console.log('roles:', this.roles);
  },
  mounted() {
      window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
  },
  destroyed() {
      window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
  },
  data() {
    return {
      username: '',
      roles: [],
      submitCount: 0,
      maxCount: 99,
      dialogVisible: false,
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      notification: '',
      openNotification: false
    }
  },
  methods: {
    beforeunloadHandler(e) {
      console.log('关闭窗口之后');
      websocket.close();
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    changePassword(){
      this.dialogVisible = true;
    },
    async logout() {
      websocket.close();
      // closeWebsocket();
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    },
    handleSubmit(formData) {
      // Handle form submission
      console.log('Form submitted with:', formData);
      changePassword(this.username, formData.oldPassword, formData.newPassword).then(res =>{
        console.log('submit:', res)
        this.$message({
            type: 'success',
            message: '修改成功!'
        });
        this.$refs.changeDialog.resetForm(formData)
        this.dialogVisible = false; 
      }).catch(()=>{
        this.$message({
            type: 'error',
            message: '修改失败！'
        });
        this.$refs.changeDialog.resetForm(formData)
        this.dialogVisible = false;
      });
    },
    handleReset() {
      console.log('Form reset');
    },
    // handleNotification(msg){  // 实际调用的方法
    //   //若是ws开启状态
    //   if (this.websocket.readyState === this.websocket.OPEN) {
    //       this.webSocketSend(msg)
    //   }
    //   // 若是 正在开启状态，则等待300毫秒
    //   else if (this.websocket.readyState === this.websocket.CONNECTING) {
    //       let that = this;//保存当前对象this
    //       setTimeout(function () {
    //           that.webSocketSend(msg)
    //       }, 300);
    //   }
    //   // 若未开启 ，则等待500毫秒
    //   else {
    //       this.initWebSocket();
    //       let that = this;//保存当前对象this
    //       setTimeout(function () {
    //           that.webSocketSend(msg)
    //       }, 500);
    //   }
    // },
    initWebSocket(){ //初始化weosocket
        //ws地址
        // const wsuri = "ws:" + "47.98.47.251" + "/ws/" + this.username;
        const wsuri = "ws:" + "192.168.23.205" + "/ws/" + this.username;
        // const wsuri = "ws:" + "//localhost:8999" + "/ws/" + this.username;
        websocket = new WebSocket(wsuri);
        websocket.onmessage = this.webSocketOnMessage;
        websocket.onclose = this.webSocketClose;
    },
    webSocketOnMessage(e){ //数据接收
        const redata = e.data;
        let notifyType = 'info';
        if (redata.substr(0,2) === '恭喜'){
          notifyType = 'success'
        }
        if (redata.substr(0,2) === '抱歉'){
          notifyType = 'error'
        }
        this.$notify({
          title: '申请提示',
          message: e.data,
          type: notifyType
        });
        console.log(e.data);
        this.submitCountLoad();
    },
    webSocketSend(msg){//数据发送
        websocket.send(msg);
    },
    webSocketClose(){  //关闭
        // console.log("connection closed (" + e.code + ")");
        console.log("connection closed ()");
    },
    submitCountLoad(){
        todoCount().then(res=>{
            console.log('count', res.data);
            this.submitCount = res.data;
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    // display: flex;
    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .item {
      margin-bottom: 15px;
      // margin-left: 10px;
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
