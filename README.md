# 懒狗Bean配置管理工具
> 说到痛处就气急败坏，其实还是一条懒狗！
### 技术架构
* server端和client端使用Netty构建长连接
### 模块划分
* bean-manager-client       包括接收服务器配置更新消息，和spring配置
* bean-manager-server       发送配置更新消息
* bean-manager-common       公用工具类，异常类
* bean-manager-persistence  持久化 基于MyBatis
* bean-manager-message      server端和client端的消息实体约定
* bean-manager-web          提供更新和查看配置的UI
* bean-manager-sample       样例程序，测试用
