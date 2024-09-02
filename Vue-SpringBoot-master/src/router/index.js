import Vue from 'vue'  
import Router from 'vue-router'  
import HelloWorld from '@/components/HelloWorld'  
import Login from '@/components/Login'  
import Fail from '@/components/Fail'  
  
Vue.use(Router)  
  
// 路由配置  
const router = new Router({  
  routes: [  
    {  
      path: '/',  
      name: 'Login',  
      component: Login  
    },  
    {  
      path: '/HelloWorld',  
      name: 'HelloWorld',  
      component: HelloWorld,  
      meta: { requiresAuth: true } // 标记需要认证才能访问  
    },  
    {  
      path: '/Fail',  
      name: 'Fail',  
      component: Fail  
    }  
  ]  
})  

// 假设的isAuthenticated函数，这里仅为示例  
function isAuthenticated() {  
  // 检查localStorage或Vuex中的token是否存在且有效  
  const token = localStorage.getItem('token');  
  // 这里应该添加更多的逻辑来验证token是否有效，这里仅检查token是否存在  
  return token !== null;  
}
  
// 全局前置守卫  
router.beforeEach((to, from, next) => {  
  // 检查路由是否需要认证  
  if (to.matched.some(record => record.meta.requiresAuth)) {  
    // 检查用户是否已登录  
    if (!isAuthenticated()) {  
      // 用户未登录，则重定向到登录页面  
      next({  
        path: '/',  
        query: { redirect: to.fullPath } // 将要跳转的路由地址作为参数，登录后可以根据这个参数重定向  
      })  
    } else {  
      // 用户已登录，则继续执行后续路由  
      next()  
    }  
  } else {  
    // 路由不需要认证，则直接继续执行后续路由  
    next()  
  }  
})  
  
export default router