<a id="readme-top"></a>

<div align="center">

# dingtalk-spring-boot-starter

**Spring Boot Starter for dingtalk**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/dingtalk-spring-boot-starter)](https://github.com/easy-4-java/dingtalk-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17-orange)](#3-requirements-and-compatibility)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[简体中文](./README.zh-CN.md) | [English](./README.md)

[Positioning](#1-positioning) · [Capabilities](#2-core-capabilities) ·
[Dependency](#5-dependency) · [Quick Start](#6-quick-start) ·
[Configuration](#7-configuration-reference) · [Versions](#9-version-lines-and-compatibility) ·
[Build](#10-build-and-test) · [License](#12-license)

</div>

---

> **Current Version**：`4.1.x.20260527-SNAPSHOT`<br>
> **JDK Baseline**：`17`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`dingtalk-spring-boot-starter`<br>
> **License**：Apache License 2.0<br>

## 1. Positioning

**dingtalk-spring-boot-starter** is a Spring Boot starter that integrates **dingtalk** for applications using dingtalk. It provides auto-configuration, property binding, and ready-to-use beans so that applications can consume dingtalk capabilities with minimal setup.

| Dimension | Description |
|---|---|
| Type | Spring Boot Starter |
| Consumers | Spring Boot applications using dingtalk |
| Core Capabilities | auto-configuration, property binding, ready-to-use beans for dingtalk |
| JDK | `17` |
| Coordinates | `io.github.easy4j:dingtalk-spring-boot-starter:4.1.x.20260527-SNAPSHOT` |
| Config Prefix | `dingtalk` |

## 2. Core Capabilities

| Capability | Status | Description |
|---|:---:|---|
| Auto-configuration | ✅ Stable | Registers dingtalk beans automatically |
| Property Binding | ✅ Stable | Binds `dingtalk.*` to `DingTalkProperties` |
| `DingTalkConfigProvider` bean | ✅ Stable | Auto-registered via DingTalkAutoConfiguration |

## 3. Requirements and Compatibility

| Dependency | Minimum | Evidence |
|---|---:|---|
| JDK | `17` | `pom.xml` |
| Spring Boot | `4.1.0-M4` | `pom.xml` parent |
| Maven | `3.6+` | Maven Enforcer |

## 4. Auto-configuration

The starter auto-configures exactly **one** service facade graph so callers go through the
`DingTalkService` composite interface (defined in `dingtalk-sdk-extension`).  The six
operation sub-facades (account / SNS / SSO / JSAPI / robot / user) are accessed through
the service — they are **not** registered as individual Spring beans.

| Bean | Condition | Missing Behavior |
|---|---|---|
| `DingTalkConfig` | Spring `@ConditionalOnMissingBean` | not created if user overrides |
| `DingTalkConfigProvider` | Spring `@ConditionalOnMissingBean` | not created if user overrides |
| `DingTalkConfigStorage` | Spring `@ConditionalOnMissingBean` | not created if user overrides |
| `DingTalkAccessTokenProvider` | Spring `@ConditionalOnMissingBean` | not created if user overrides |
| `DingTalkService` | Spring `@ConditionalOnMissingBean(DingTalkService.class)` | user-provided service bean wins |

> All SDK domain types (`DingTalkService`, `DingTalkConfigStorage`,
> `DingTalkAccountOperations`, etc.) come from the pure-SDK artifact
> `io.github.easy4j:dingtalk-sdk-extension` — the starter is a **thin adapter only**.

Auto-configuration registration:

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 2.7+ / 3.x / 4.x)
- `META-INF/spring.factories` (Spring Boot 2.x legacy, kept for transient compatibility)

## 5. Dependency

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>dingtalk-spring-boot-starter</artifactId>
    <version>4.1.x.20260527-SNAPSHOT</version>
</dependency>
```

No additional easy4j component dependencies.

## 6. Quick Start

### 6.1 Add dependency

Add the dependency above to your `pom.xml`.

### 6.2 Configure

```yaml
dingtalk:
  enabled: true
```

### 6.3 Use the bean

Inject the single `DingTalkService` facade (domain operations live on it):

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

Operation sub-facades are accessed via the service:

```java
String token = dingTalkService.getAccessToken(corpId, appKey);
dingTalkService.opsForRobot().sendText(corpId, appKey, webhookAccessToken, secret, "hello");
```

If you want to customize any link in the graph, provide your own `@Bean` of the
matching type and the auto-configuration will back off via `@ConditionalOnMissingBean`.

## 7. Configuration Reference

### 7.1 Config Prefix

`dingtalk`

### 7.2 Configuration Items

| Property | Type | Default | Required | Description | Sensitive |
|---|---|---|:---:|---|:---:|
| `dingtalk.enabled` | boolean | `true` | No | Enable the starter | No |
<!-- additional properties below -->

## 8. Version Lines and Compatibility

| Branch | JDK | Spring Boot | Component Version | Status |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | Maintenance |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | Maintenance |
| `4.0.x` / `4.1.x` | `17+` | 4.x | `3.0.x` | Active |

## 9. Build and Test

```bash
mvn clean verify
mvn -pl dingtalk-spring-boot-starter -am test
```

## 10. Troubleshooting

| Symptom | Diagnosis | Resolution |
|---|---|---|
| Bean not created | Check auto-configuration report | Verify `dingtalk.enabled=true` and classpath |
| `ClassNotFoundException` | Missing dependency | Add the required module |
| Version conflict | `mvn dependency:tree` | Use BOM for version alignment |

## 11. Contribution

1. Fork the repository.
2. Create a feature branch.
3. Run `mvn clean verify` before submitting.
4. Submit a pull request.

## 12. License

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

<div align="center">

[Back to top](#readme-top) · [Issues](https://github.com/easy-4-java/dingtalk-spring-boot-starter/issues) · [Repository](https://github.com/easy-4-java/dingtalk-spring-boot-starter)

</div>
