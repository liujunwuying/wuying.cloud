# wuying.cloud
![qrcode_for_gh_a395f30b97ef_258](https://user-images.githubusercontent.com/65218736/135223017-fd5a8539-3462-46a2-b02d-ed6c0bfb09e4.jpg)<br>
公众号：树下遛鸟 邮箱：liujun_wuying@163.com
## wuying-cloud-parent:<br>
父模块(依赖包版本管理):<br>
spring-boot-dependencies	2.3.0.RELEASE<br>
spring-cloud-dependencies	Hoxton.SR5<br>
wuying-cloud-dependencies	1.0.0-SNAPSHOT
## wuying-cloud-dependencies:<br>
spring-boot/cloud-dependencies以外的包版本管理，避免包版本不兼容报错，<br>
如mybatis-plus-boot-starter/mysql-connector-java/lombok/mybatis-spring-boot-starter/wuying-cloud-xxx等。
## wuying-cloud-commons:<br>
WuyingLogger日志工具类，通过Thread.currentThread().getStackTrace获取调用类名称；<br>
ServiceInfoUtils工具类，获取实例Ip/hostname/processId；
## wuying-cloud-context:<br>
上下文插件包，ApplicationContextHolder类，封装getBean/getProperty静态方法，实例化ServiceInstanceInfo(实例信息)；
## wuying-cloud-starter-web:<br>
微服务starter包，包含：<br>
wuying-cloud-context：上下文插件包<br>
spring-boot-starter-web：springboot web启动包<br>
spring-boot-starter-actuator：springboot监控包<br>
micrometer-registry-prometheus：普米数据格式支持<br>
spring-cloud-starter-consul-discovery：consul服务发现<br>
spring-cloud-starter-openfeign：rpc<br>
spring-cloud-starter-netflix-hystrix：熔断限流<br>
## wuying-cloud-mybatis-support:<br>
mybatis查询条数限制,默认10000<br>
限制行数配置项，支持热刷新：wuying.cloud.mybatis.support.maxRow<br>
拦截器开关配置项：wuying.cloud.mybatis.rowLimit.enabled
## wuying-cloud-redis-support:<br>
redis分布式锁，requestId用于解锁校验，超时时间内重试，lua脚本解锁
## wuying-cloud-transaction-async:<br>
未成品，不可使用
