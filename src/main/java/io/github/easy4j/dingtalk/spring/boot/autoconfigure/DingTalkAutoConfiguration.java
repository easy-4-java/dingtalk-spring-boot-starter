package io.github.easy4j.dingtalk.spring.boot.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import com.dingtalk.api.DingTalkClient;
import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkService;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkService;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import io.github.easy4j.dingtalk.storage.InMemoryDingTalkConfigStorage;
import io.github.easy4j.dingtalk.spring.boot.autoconfigure.properties.DingTalkProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * Auto-configuration for DingTalk integration.
 * <p>
 * The starter is a thin adapter: beans are assembled into exactly one
 * {@link DingTalkService} facade plus a shared token-cache storage; individual
 * operation beans are intentionally removed from the Spring context so that
 * callers go through the single facade — matching the documented blueprint.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DingTalkClient.class })
@EnableConfigurationProperties({ DingTalkProperties.class })
public class DingTalkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DingTalkConfig dingTalkConfig(DingTalkProperties properties) {
        DingTalkConfig cfg = DingTalkPropertiesMapper.toConfig(properties);
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

    @Bean
    @ConditionalOnMissingBean
    public DingTalkConfigProvider dingTalkConfigProvider(DingTalkConfig dingTalkConfig) {
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(dingTalkConfig);
        provider.init();
        log.debug("DingTalkConfigProvider registered: class={}", provider.getClass().getSimpleName());
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkConfigStorage dingTalkConfigStorage() {
        return new InMemoryDingTalkConfigStorage();
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkAccessTokenProvider dingTalkAccessTokenProvider(
            DingTalkConfigProvider dingTalkConfigProvider,
            DingTalkConfigStorage storage) {
        DefaultDingTalkAccessTokenProvider provider =
                new DefaultDingTalkAccessTokenProvider(dingTalkConfigProvider, storage);
        log.debug("DingTalkAccessTokenProvider registered: class={}", provider.getClass().getSimpleName());
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean(DingTalkService.class)
    public DingTalkService dingTalkService(DingTalkConfigProvider configProvider,
                                           DingTalkAccessTokenProvider tokenProvider,
                                           DingTalkConfigStorage storage) {
        return new DefaultDingTalkService(configProvider, tokenProvider);
    }

    private static String safe(@Nullable String s) { return s == null ? "<null>" : s; }

    private static int size(@Nullable List<?> l) { return l == null ? 0 : l.size(); }
}
