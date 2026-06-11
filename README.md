# Community

仿 StackOverflow 社区论坛，基于 Spring Boot 构建。

## 技术栈

- **Spring Boot** 2.2.4
- **MyBatis** 2.1.1
- **Thymeleaf** 模板引擎
- **H2** (dev) / **MySQL** (prod)
- **Flyway** 数据库迁移
- **GitHub OAuth** 登录

## 功能

- GitHub OAuth 登录 / 登出
- 问题发布、编辑、删除
- 评论（支持二级回复）
- 通知系统
- 热门标签
- 搜索与排序
- 相关推荐

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 配置 GitHub OAuth

在 [GitHub Developer Settings](https://github.com/settings/developers) 创建 OAuth App，然后设置环境变量：

```bash
# Windows PowerShell
$env:GITHUB_CLIENT_ID="your_client_id"
$env:GITHUB_CLIENT_SECRET="your_client_secret"
$env:GITHUB_REDIRECT_URI="http://localhost:8080/callback"
```

### 启动

```bash
mvnw spring-boot:run
```

访问 http://localhost:8080

## 环境 & 配置

项目使用 Spring Profile 区分环境：

| Profile | 数据库 | 配置文件 |
|---------|--------|----------|
| `dev` (默认) | H2 嵌入式 | `application-dev.properties` |
| `prod` | MySQL | `application-prod.properties` |

### 本地开发 (H2)

直接运行，无需额外配置：

```bash
./mvnw spring-boot:run
```

### 生产环境 (MySQL)

需要先准备 MySQL 实例，然后设置环境变量：

```bash
export SPRING_PROFILES_ACTIVE=prod
export MYSQL_HOST=your-mysql-host
export MYSQL_PORT=3306
export MYSQL_DATABASE=community
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your-password

./mvnw spring-boot:run
```

### 部署到腾讯云托管 (CloudBase Run)

1. 修改 `cloudbaserc.json`，填入你的环境 ID、MySQL 和 GitHub OAuth 参数
2. 打开 [CloudBase 控制台](https://console.cloud.tencent.com/tcb)，进入"云托管"
3. 创建服务，选择"从本地代码/Dockerfile 创建"
4. 将项目目录上传，系统自动构建部署

### Docker 构建

```bash
# 构建
docker build -t community .

# 运行 (prod + MySQL)
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MYSQL_HOST=host \
  -e MYSQL_DATABASE=community \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=xxx \
  --name community \
  community
```

## 项目结构

```
src/main/java/com/zfx/community/
├── cache/          # 缓存（热标签、标签库）
├── controller/     # 控制器
├── dto/            # 数据传输对象
├── enums/          # 枚举
├── exception/      # 异常处理
├── interceptor/    # 拦截器
├── mapper/         # MyBatis Mapper
├── model/          # 数据模型
├── provider/       # 外部 API（GitHub OAuth）
├── schedule/       # 定时任务
└── service/        # 业务逻辑
```
