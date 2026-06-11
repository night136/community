# ============================================
# Stage 1: Build — 使用腾讯云 Maven 镜像加速
# ============================================
FROM maven:3.6.0-jdk-8-slim AS build

WORKDIR /app

# 先拷贝依赖文件，利用 Docker 缓存层
COPY pom.xml settings.xml /app/
RUN mvn -s /app/settings.xml dependency:go-offline -B

# 拷贝源码编译
COPY src /app/src
RUN mvn -s /app/settings.xml -f /app/pom.xml clean package -DskipTests

# ============================================
# Stage 2: Run — 使用 Alpine + OpenJDK 8 缩小镜像
# ============================================
FROM alpine:3.13

# 使用腾讯云镜像源安装 JRE
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tencent.com/g' /etc/apk/repositories \
    && apk add --update --no-cache openjdk8-jre-base ca-certificates tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && rm -f /var/cache/apk/*

WORKDIR /app

# 拷贝构建产物
COPY --from=build /app/target/*.jar app.jar

# 端口必须与 cloudbaserc.json 中的 containerPort 一致
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
