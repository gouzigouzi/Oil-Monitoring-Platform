<template>
  <div class="background">
    <div class="bg-title">
      <img src="@/assets/logo/zju.png" class="bg-logo">
      <h1>工程机械机油监测平台</h1>
    </div>
    <div class="login-box">
      <div class="carousel">
        <img src="@/assets/logo/R (2).jpg" alt="Image 1" class="active">
        <img src="@/assets/logo/pic4.png" alt="Image 2">
        <img src="@/assets/logo/bg2.png" alt="Image 3">
        <div class="indicators">
          <div class="indicator active" data-index="0"></div>
          <div class="indicator" data-index="1"></div>
          <div class="indicator" data-index="2"></div>
        </div>
      </div>
      <div class="right">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">
          <div class="logo-container">
            <img src="@/assets/logo/pic3.png" class="loader">
          </div>
          <div class="title-container">
            <h3 class="title">登  录</h3>
          </div>    
          <el-form-item prop="username">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="Username"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              :key="passwordType"
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="Password"
              name="password"
              tabindex="2"
              auto-complete="on"
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <div style="display: flex; justify-content: center;">
            <el-button :loading="loading" type="primary" style="width:50%;margin-top:20px;" @click.native.prevent="handleLogin">Login</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('Please enter the correct user name'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码长度不少于6位'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: 'admin',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, message: '用户名长度在不少于 3 个字符 ', trigger: 'blur' }
        ],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
        console.log(this.redirect)
      },
      immediate: true
    }
  },
  methods: {
    // showPwd() {
    //   if (this.passwordType === 'password') {
    //     this.passwordType = ''
    //   } else {
    //     this.passwordType = 'password'
    //   }
    //   this.$nextTick(() => {
    //     this.$refs.password.focus()
    //   })
    // },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            // this.$router.push({ path: this.redirect || '/' })
            this.$router.push( '/' )
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  },
  mounted() {
    const images = this.$el.querySelectorAll('.carousel img');
    const indicators = this.$el.querySelectorAll('.indicator');
    let currentIndex = 0;
    let interval;

    const showImage = (index) => {
      images[currentIndex].classList.remove('active');
      images[currentIndex].classList.add('prev');
      indicators[currentIndex].classList.remove('active');

      currentIndex = index;

      images[currentIndex].classList.add('active');
      indicators[currentIndex].classList.add('active');

      setTimeout(() => {
        images.forEach(img => img.classList.remove('prev'));
      }, 1000); // Match the transition duration
    }

    const showNextImage = () => {
      showImage((currentIndex + 1) % images.length);
    }

    indicators.forEach(indicator => {
      indicator.addEventListener('click', function () {
        clearInterval(interval);
        showImage(parseInt(this.dataset.index));
        interval = setInterval(showNextImage, 3000); // Restart interval after click
      });
    });

    interval = setInterval(showNextImage, 3000); // 每3秒切换一次图片
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}

.background {
      width: 100%;
      height: 100%;
      background-image: url('../../assets/logo/pic1.png');
      background-size: cover;
      background-position: center;
      position: relative;
      overflow: hidden;
}

.background::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(61, 61, 61, 0.5); /* 阴影颜色 */
}

.login-box {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 900px; /* 改变方框宽度 */
  height: 550px; /* 改变方框高度 */
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 5px 5px rgba(197, 196, 196, 0.3); /* 大方框阴影效果 */
  overflow: hidden;
  display: flex;
}

.svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

.login-box .left, .login-box .right {
  float: left;
  width: 50%;
  height: 100%;
}

.login-box .left img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(5px); /* 模糊效果 */
}

.login-box .right {
  padding: 0px;
  flex: 1
}

.login-box .right h2 {
  margin-bottom: 20px;
  font-size: 24px;
  text-align: center;
}

.carousel {
  flex: 1;
  width: 50%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.carousel img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  transition: opacity 0.5s;
  opacity: 0;
}

.carousel img.active {
  opacity: 1;
}

.carousel img.prev {
  opacity: 0;
  transition: opacity 1s;
}

.indicators {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #ffffff;
  margin: 0 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.indicator.active {
  background-color: #3498db;
}

.login-box .right {
  width: 50%;
  /* padding: 30px; */
  /* padding-bottom: 100px; */
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-top: -30px;
}

.login-box .right h2 {
  margin-bottom: 20px;
  font-size: 24px;
  text-align: center;
}

.title-container {
  position: relative;
  .title {
    font-size: 30px;
    color: #294a8b;
    margin: 30px auto 30px auto;
    text-align: center;
    font-weight: bold;
  }
}
    
.login-form {
  position: relative;
  width: 75%;
  max-width: 100%;
  padding: 0px 35px 0;
  margin: 20px auto;
  overflow: hidden;
}

.logo-container {
  display: flex;
  justify-content: center;
  .loader {
    width: 350px;
  }
}

.show-pwd {
    position: absolute;
    right: 10px;
    top: 0px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
}

.bg-title {
  display: flex;
  margin-top: -20px;
  h1{
    color: #ffffff;
    font-size: 36px;
    font-style: italic;
    padding-top: 5px;
  }
  .bg-logo {
    width: 60px;
    height: 70px;
    padding-top: 30px; 
    padding-right: 10px; 
    padding-left: 5px;
  }
}
</style>
