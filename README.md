基于SpringBoot和Vue的前后端分离项目，实现线上存储图片功能。

登录地址：http://65.49.194.52:8080/#/
首页地址：http://65.49.194.52:8080/#/HelloWorld

还没有做注册页面，通过接口注册
注册接口：65.49.194.52:8081/person/register
请求方式：POST
请求头：registerCode
传入参数：{
    "username":"public",
    "password":"123456"
}

图片文件需要使用请求代理工具，例如nginx做转发，后端项目中没有处理图片请求功能

实际演示效果:
![image](https://github.com/user-attachments/assets/18dda36c-eed3-4a72-b009-ac5aa3cf001e)
