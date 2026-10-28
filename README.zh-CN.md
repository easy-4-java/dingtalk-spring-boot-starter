<a id="readme-top"></a>

<div align="center">

# dingtalk-spring-boot-starter

**Spring Boot Starter，集成 dingtalk**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/dingtalk-spring-boot-starter)](https://github.com/easy-4-java/dingtalk-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17-orange)](#3-运行要求与兼容性)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[English](./README.md) | [简体中文](./README.zh-CN.md)

[项目定位](#1-项目定位) · [核心能力](#2-核心能力) ·
[引入依赖](#5-引入依赖) · [快速开始](#6-快速开始) ·
[配置参考](#7-配置参考) · [版本线](#8-版本线与兼容性) ·
[构建测试](#9-构建与测试) · [许可证](#12-许可证)

</div>

---

> **当前版本**：`4.1.x.20260527-SNAPSHOT`<br>
> **JDK 基线**：`17`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`dingtalk-spring-boot-starter`<br>
> **许可证**：Apache License 2.0<br>

## 1. 项目定位

**dingtalk-spring-boot-starter** 是一个面向 使用 dingtalk 的应用 的 Spring Boot Starter，用于将 **dingtalk** 集成到 Spring Boot 应用中。它提供自动装配、属性绑定与开箱即用的 Bean，使应用以最小配置即可使用 dingtalk 的全部能力。

| 维度 | 说明 |
|---|---|
| 类型 | Spring Boot Starter |
| 消费方 | 使用 dingtalk 的 Spring Boot 应用 |
| 核心能力 | 自动装配、属性绑定、开箱即用的 dingtalk Bean |
| JDK | `17` |
| 坐标 | `io.github.easy4j:dingtalk-spring-boot-starter:4.1.x.20260527-SNAPSHOT` |
| 配置前缀 | `dingtalk` |

## 2. 核心能力

| 能力 | 状态 | 说明 |
|---|:---:|---|
| 自动装配 | ✅ 稳定 | 自动注册 dingtalk 相关 Bean |
| 属性绑定 | ✅ 稳定 | 绑定 `dingtalk.*` 到 `DingTalkProperties` |
| `DingTalkConfigProvider` Bean | ✅ 稳定 | 通过 DingTalkAutoConfiguration 自动注册 |

## 3. 运行要求与兼容性

| 依赖 | 最低版本 | 证据来源 |
|---|---:|---|
| JDK | `17` | `pom.xml` |
| Spring Boot | `4.1.0-M4` | `pom.xml` parent |
| Maven | `3.6+` | Maven Enforcer |

## 4. 自动装配

Starter 只注册**一张**服务图：调用方统一通过复合接口 `DingTalkService`（由 `dingtalk-sdk-extension` 提供）访问能力。6 个域 Operation（account / SNS / SSO / JSAPI / robot / user）通过该服务进入，**不**会在 Spring 上下文里作为独立 Bean 出现。

| Bean | 条件 | 缺失时行为 |
|---|---|---|
| `DingTalkConfig` | `@ConditionalOnMissingBean` | 用户覆盖则不创建 |
| `DingTalkConfigProvider` | `@ConditionalOnMissingBean` | 用户覆盖则不创建 |
| `DingTalkConfigStorage` | `@ConditionalOnMissingBean` | 用户覆盖则不创建 |
| `DingTalkAccessTokenProvider` | `@ConditionalOnMissingBean` | 用户覆盖则不创建 |
| `DingTalkService` | `@ConditionalOnMissingBean(DingTalkService.class)` | 用户自定义 service bean 优先 |

> 所有 SDK 域类型（`DingTalkService`、`DingTalkConfigStorage`、`DingTalkAccountOperations` 等）都来自纯 SDK 构件 `io.github.easy4j:dingtalk-sdk-extension`，本 Starter 只做「薄适配」。

自动装配注册：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 2.7+ / 3.x / 4.x 标准）
- `META-INF/spring.factories`（Spring Boot 2.x 兼容，暂保留过渡期）

## 5. 引入依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dingtalk-spring-boot-starter</artifactId>
    <version>4.1.x.20260527-SNAPSHOT</version>
</dependency>
```

无其他 easy4j 组件依赖。

## 6. 快速开始

### 6.1 引入依赖

在 `pom.xml` 中添加上述依赖。

### 6.2 配置

```yaml
dingtalk:
  enabled: true
```

### 6.3 使用 Bean

统一注入单例 `DingTalkService` 复合门面，所有域操作从服务上取：

```java
import io.github.easy4j.dingtalk.service.DingTalkService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
@Autowired
private DingTalkService dingTalkService;
```

Operation 子门面从服务上获取：

```java
String token = dingTalkService.getAccessToken(corpId, appKey);
dingTalkService.opsForRobot().sendText(corpId, appKey, webhookAccessToken, secret, "hello");
```

如需自构建图中任一环，只需提供同类型的自定义 `@Bean`，自动装配会通过 `@ConditionalOnMissingBean` 自动退避。

## 7. 配置参考

### 7.1 配置前缀

`dingtalk`

### 7.2 配置项

| 属性 | 类型 | 默认值 | 必填 | 说明 | 敏感 |
|---|---|---|:---:|---|:---:|
| `dingtalk.enabled` | boolean | `true` | 否 | 是否启用 Starter | 否 |
<!-- 更多属性见下方 -->

## 8. 版本线与兼容性

| 分支 | JDK | Spring Boot | 组件版本 | 状态 |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | 维护中 |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | 维护中 |
| `4.0.x` / `4.1.x` | `17+` | 4.x | `3.0.x` | 活跃开发 |

## 9. 构建与测试

```bash
mvn clean verify
mvn -pl dingtalk-spring-boot-starter -am test
```

## 10. 排障

| 症状 | 诊断 | 解决 |
|---|---|---|
| Bean 未创建 | 查看自动装配报告 | 确认 `dingtalk.enabled=true` 与 classpath |
| `ClassNotFoundException` | 缺少依赖 | 引入对应模块 |
| 版本冲突 | `mvn dependency:tree` | 使用 BOM 统一版本 |

## 11. 贡献

1. Fork 本仓库。
2. 创建特性分支。
3. 提交前运行 `mvn clean verify`。
4. 提交 Pull Request。

## 12. 许可证

本项目采用 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

---

<div align="center">

[返回顶部](#readme-top) · [问题反馈](https://github.com/easy-4-java/dingtalk-spring-boot-starter/issues) · [仓库地址](https://github.com/easy-4-java/dingtalk-spring-boot-starter)

</div>
