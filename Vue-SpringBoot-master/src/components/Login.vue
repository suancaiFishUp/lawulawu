<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">{{ message }}</h2>
      <input v-model="username" placeholder="用户名" class="login-input"><br/>
      <input v-model="password" placeholder="密码" type="password" class="login-input"><br/>
      <button v-on:click="login()" class="login-button">登录</button>
    </div>
  </div>
</template>

<script>
import http from '@/http/http.js';

export default {
  name: "login",
  data() {
    return {
      message: 'lawulawu登陆页面',
      username: '',
      password: ''
    }
  },
  methods: {
    login() {
      const username = this.username;
      const password = this.password;
      http.post('/person/login', {
        username,
        password
      })
      .then(response => {
        const { code, data } = response.data;
        const token = data;
        if (code == "200") {
          localStorage.setItem('token', token);
          this.$router.push({ path: '/HelloWorld' });
        } else {
          this.$router.push({ path: '/Fail' });
        }
      })
      .catch(error => {
        console.error(error);
      });
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
}

.login-box {
  background-color: #ffffff;
  padding: 20px 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.login-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333333;
}

.login-input {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

.login-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.2);
}

.login-button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #0056b3;
}
</style>
