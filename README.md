# Credit-Giving-System
Credit-Giving-System,基于Springboot的多模块JAVA WEB应用

---
### 已添加模块功能
1. spring-boot-common: 工具模块，此模块只提供工具类使用，不依赖任何模块
    1. 枚举类
    2. 加密工具类
    3. 时间转换类
    4. 参数校验类，包括空校验、长度校验、数字校验、手机号校验、邮箱校验、用户名校验、密码校验、身份证校验
    5. validation分组类，仅用于分组使用
2. spring-boot-service: service模块，包括数据底层处理、实体类
    1. dao层
    2. service层
    3. model实体
    4. mybatis-generator插件，自动生成dao、model、mapper.xml
    5. logback日志
3. spring-boot-shiro: 权限模块，此模块是基于shiro开发的用户角色权限的验证
    1. 多角色或关系配置
    2. 自定义过滤器规则
    3. 自定义认证鉴权规则
    4. 禁用Session，基于token的RESTful风格的API
4. spring-boot-web: 应用层，RESTful风格的API，统一格式返回数据
    1. controller层
    2. 全局异常处理
    3. 统一接口返回格式
    4. 基于validation的参数校验
