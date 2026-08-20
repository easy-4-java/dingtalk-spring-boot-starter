package com.dingtalk.spring.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.spring.boot.DingTalkConfig;
import com.dingtalk.spring.boot.config.DingTalkCorpAppConfig;
import com.dingtalk.spring.boot.config.DingTalkLoginConfig;
import com.dingtalk.spring.boot.config.DingTalkPersonalMiniAppConfig;
import com.dingtalk.spring.boot.config.DingTalkRobotConfig;
import com.dingtalk.spring.boot.config.DingTalkSuiteConfig;

/**
 * Auto-configuration for DingTalk integration.
 * <p>
 * Bean creation order mirrors the explicit dependency chain and intentionally
 * keeps Spring Framework concerns (InitializingBean, ApplicationContextAware,
 * {@code @ConditionalOnMissingBean}, …) isolated in the starter layer so that
 * the pure SDK module stays zero-Spring.  WxJava-like 6 operations facades are
 * exposed individually so user code can {@code @Autowire} only the subset it
 * uses.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DingTalkClient.class })
@EnableConfigurationProperties({ DingTalkProperties.class })
public class DingTalkAutoConfiguration {

    /* -------------------------------------------------------------------- */
    /* 1. 聚合配置：DingTalkProperties（Spring）→ DingTalkConfig（纯 SDK）   */
    /* -------------------------------------------------------------------- */
    @Bean
    @ConditionalOnMissingBean
    public DingTalkConfig dingTalkSdkConfig(DingTalkProperties properties) {
        DingTalkConfig cfg = new DingTalkConfig();
        cfg.setCorpId(properties.getCorpId());
        cfg.setCorpSecret(properties.getCorpSecret());
        cfg.setCorpApps(mapCorpApps(properties.getCorpApps()));
        cfg.setApps(mapPersonalApps(properties.getApps()));
        cfg.setSuites(mapSuites(properties.getSuites()));
        cfg.setLogins(mapLogins(properties.getLogins()));
        cfg.setRobots(mapRobots(properties.getRobots()));
        if (log.isDebugEnabled()) {
            log.debug("DingTalk SDK DingTalkConfig built: corpId={}, apps={} corpApp={} suites={} logins={} robots={}",
                    safe(cfg.getCorpId()),
                    size(cfg.getApps()),
                    size(cfg.getCorpApps()),
                    size(cfg.getSuites()),
                    size(cfg.getLogins()),
                    size(cfg.getRobots()));
        }
        return cfg;
    }

    /* -------------------------------------------------------------------- */
    /* 2. ConfigProvider：加载 + 索引所有子配置                              */
    /* -------------------------------------------------------------------- */
    @Bean
    @ConditionalOnMissingBean
    public DingTalkConfigProvider dingTalkConfigProvider(DingTalkConfig dingTalkSdkConfig) {
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(dingTalkSdkConfig);
        log.debug("DingTalkConfigProvider registered: class={}", provider.getClass().getSimpleName());
        return provider;
    }

    /* -------------------------------------------------------------------- */
    /* 4. AccessTokenProvider                                               */
    /* -------------------------------------------------------------------- */
    @Bean
    @ConditionalOnMissingBean
    public DingTalkAccessTokenProvider dingTalkAccessTokenProvider(
            DingTalkConfigProvider dingTalkConfigProvider) {
        DefaultDingTalkAccessTokenProvider provider =
                new DefaultDingTalkAccessTokenProvider(dingTalkConfigProvider);
        log.debug("DingTalkAccessTokenProvider registered: class={}", provider.getClass().getSimpleName());
        return provider;
    }

    /* -------------------------------------------------------------------- */
    /* 5. 纯 SDK DingTalkTemplate + 包装 SpringDingTalkTemplate（@Primary） */
    /* -------------------------------------------------------------------- */
    @Bean
    @ConditionalOnMissingBean
    public DingTalkTemplate dingTalkTemplate(
            DingTalkConfigProvider dingTalkConfigProvider,
            DingTalkAccessTokenProvider dingTalkAccessTokenProvider) {
        return new DingTalkTemplate(dingTalkConfigProvider, dingTalkAccessTokenProvider);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SpringDingTalkTemplate springDingTalkTemplate(
            DingTalkTemplate dingTalkTemplate) {
        return new SpringDingTalkTemplate(
                dingTalkTemplate.getDingTalkConfigProvider(),
                dingTalkTemplate.getDingTalkAccessTokenProvider());
    }

    /* -------------------------------------------------------------------- */
    /* 6. 6 Operations facades（6 × SDK operations 暴露到 Spring context）  */
    /* -------------------------------------------------------------------- */
    @Bean
    @ConditionalOnMissingBean
    public DingTalkAccountOperations dingTalkAccountOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForAccount();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkSnsOperations dingTalkSnsOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForSns();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkSsoOperations dingTalkSsoOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForSso();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkJsapiOperations dingTalkJsapiOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForJsapi();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkRobotOperations dingTalkRobotOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForRobot();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkUserOperations dingTalkUserOperations(DingTalkTemplate dingTalkTemplate) {
        return dingTalkTemplate.opsForUser();
    }

    /* -------------------------------------------------------------------- */
    /* helpers                                                              */
    /* -------------------------------------------------------------------- */
    private static String safe(@Nullable String s) { return s == null ? "<null>" : s; }

    private static int size(@Nullable List<?> l) { return l == null ? 0 : l.size(); }

    private static List<DingTalkCorpAppConfig> mapCorpApps(@Nullable List<DingTalkProperties.CorpApp> in) {
        if (in == null) return Collections.emptyList();
        List<DingTalkCorpAppConfig> out = new ArrayList<>(in.size());
        for (DingTalkProperties.CorpApp x : in) {
            DingTalkCorpAppConfig y = new DingTalkCorpAppConfig();
            y.setAgentId(x.getAgentId());
            y.setAppKey(x.getAppKey());
            y.setAppSecret(x.getAppSecret());
            out.add(y);
        }
        return out;
    }

    private static List<DingTalkPersonalMiniAppConfig> mapPersonalApps(
            @Nullable List<DingTalkProperties.PersonalMiniApp> in) {
        if (in == null) return Collections.emptyList();
        List<DingTalkPersonalMiniAppConfig> out = new ArrayList<>(in.size());
        for (DingTalkProperties.PersonalMiniApp x : in) {
            DingTalkPersonalMiniAppConfig y = new DingTalkPersonalMiniAppConfig();
            y.setAppId(x.getAppId());
            y.setAppSecret(x.getAppSecret());
            out.add(y);
        }
        return out;
    }

    private static List<DingTalkSuiteConfig> mapSuites(@Nullable List<DingTalkProperties.Suite> in) {
        if (in == null) return Collections.emptyList();
        List<DingTalkSuiteConfig> out = new ArrayList<>(in.size());
        for (DingTalkProperties.Suite x : in) {
            DingTalkSuiteConfig y = new DingTalkSuiteConfig();
            y.setSuiteId(x.getSuiteId());
            y.setAppId(x.getAppId());
            y.setSuiteKey(x.getSuiteKey());
            y.setSuiteSecret(x.getSuiteSecret());
            out.add(y);
        }
        return out;
    }

    private static List<DingTalkLoginConfig> mapLogins(@Nullable List<DingTalkProperties.Login> in) {
        if (in == null) return Collections.emptyList();
        List<DingTalkLoginConfig> out = new ArrayList<>(in.size());
        for (DingTalkProperties.Login x : in) {
            DingTalkLoginConfig y = new DingTalkLoginConfig();
            y.setAppId(x.getAppId());
            y.setAppSecret(x.getAppSecret());
            out.add(y);
        }
        return out;
    }

    private static List<DingTalkRobotConfig> mapRobots(@Nullable List<DingTalkProperties.Robot> in) {
        if (in == null) return Collections.emptyList();
        List<DingTalkRobotConfig> out = new ArrayList<>(in.size());
        for (DingTalkProperties.Robot x : in) {
            DingTalkRobotConfig y = new DingTalkRobotConfig();
            y.setRobotId(x.getRobotId());
            y.setAccessToken(x.getAccessToken());
            y.setSecretToken(x.getSecretToken());
            out.add(y);
        }
        return out;
    }
}
