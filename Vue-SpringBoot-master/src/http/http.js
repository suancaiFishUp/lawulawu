import axios from 'axios';  
  
// 创建 axios 实例  
const http = axios.create({  
  baseURL: 'http://65.49.194.52:8081',  
  // 其他配置...  
});  
  
// 请求拦截器  
http.interceptors.request.use(  
  config => {  
    // 从 localStorage 获取 token  
    const token = localStorage.getItem('token');  
    if (token) {  
      // 如果存在 token，则添加到请求头中  
      config.headers.Authorization = `Bearer ${token}`;  
    }  
    console.log("token:"+token);
    return config;  
  },  
  error => {  
    // 处理请求错误  
    return Promise.reject(error);  
  }  
);  
  
// 使用 http 实例进行请求  
export default http;