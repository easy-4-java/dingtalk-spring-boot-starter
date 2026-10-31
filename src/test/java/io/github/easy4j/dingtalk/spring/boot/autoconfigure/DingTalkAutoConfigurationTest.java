package io.github.easy4j.dingtalk.spring.boot.autoconfigure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.config.DingTalkCorpAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkLoginConfig;
import io.github.easy4j.dingtalk.config.DingTalkPersonalMiniAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.config.DingTalkSuiteConfig;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccountService;
import io.github.easy4j.dingtalk.service.DingTalkJsapiService;
import io.github.easy4j.dingtalk.service.DingTalkRobotService;
import io.github.easy4j.dingtalk.service.DingTalkService;
import io.github.easy4j.dingtalk.service.DingTalkSnsService;
import io.github.easy4j.dingtalk.service.DingTalkSsoService;
import io.github.easy4j.dingtalk.service.DingTalkUserService;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkService;
import io.github.easy4j.dingtalk.spring.boot.autoconfigure.properties.DingTalkProperties;
import io.github.easy4j.dingtalk.storage.DingTalkConfigStorage;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DingTalkAutoConfiguration}.
 */
class DingTalkAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DingTalkAutoConfiguration.class));

    @Test
    void debugConfigurationLogReportsMappedValues() {
        Logger logger = (Logger) LoggerFactory.getLogger(DingTalkAutoConfiguration.class);
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
        logger.setLevel(Level.DEBUG);
        try {
            DingTalkProperties properties = new DingTalkProperties();
            properties.setCorpId("configured-corp");
            DingTalkConfig config = new DingTalkAutoConfiguration().dingTalkConfig(properties);
            assertThat(config.getCorpId()).isEqualTo("configured-corp");
            assertThat(events.list)
                    .extracting(ILoggingEvent::getFormattedMessage)
                    .anySatisfy(message -> assertThat(message)
                            .contains("corpId=configured-corp", "apps=0", "robots=0"));
        } finally {
            logger.setLevel(previous);
            logger.detachAppender(events);
            events.stop();
        }
    }

    @Test
    void autoConfiguration_oneServiceOneStorageFacade() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DingTalkConfigProvider.class);
            assertThat(context).hasSingleBean(DefaultDingTalkConfigProvider.class);
            assertThat(context).hasSingleBean(DingTalkAccessTokenProvider.class);
            assertThat(context).hasSingleBean(DefaultDingTalkAccessTokenProvider.class);
            assertThat(context).hasSingleBean(DingTalkConfigStorage.class);
            assertThat(context).hasSingleBean(DingTalkService.class);
            assertThat(context).doesNotHaveBean("springDingTalkTemplate");
        });
    }

    @Test
    void customServiceBacksOffAutoConfigurationService() {
        contextRunner.withUserConfiguration(CustomServiceConfig.class).run(context -> {
            assertThat(context).hasSingleBean(DingTalkService.class);
            assertThat(context.getBean(DingTalkService.class)).isInstanceOf(CustomService.class);
        });
    }

    @Test
    void autoConfiguration_shouldBindProperties() {
        contextRunner
                .withPropertyValues(
                        "dingtalk.corp-id=testCorp",
                        "dingtalk.corp-secret=testSecret"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(DingTalkProperties.class);
                    DingTalkProperties props = context.getBean(DingTalkProperties.class);
                    assertThat(props.getCorpId()).isEqualTo("testCorp");
                    assertThat(props.getCorpSecret()).isEqualTo("testSecret");
                });
    }

    @Test
    void autoConfiguration_shouldNotOverrideCustomConfigProvider() {
        contextRunner
                .withBean("customConfigProvider", DingTalkConfigProvider.class,
                        () -> new DingTalkConfigProvider() {
                            @Override public DingTalkConfig getDingTalkConfig(String corpId) { return null; }
                            @Override public DingTalkCorpAppConfig getDingTalkCorpAppConfig(String corpId, String agentId) { return null; }
                            @Override public DingTalkPersonalMiniAppConfig getDingTalkPersonalMiniAppConfig(String corpId, String appId) { return null; }
                            @Override public DingTalkSuiteConfig getDingTalkSuiteConfig(String corpId, String suiteId) { return null; }
                            @Override public DingTalkLoginConfig getDingTalkLoginConfig(String corpId, String appId) { return null; }
                            @Override public DingTalkRobotConfig getDingTalkRobotConfig(String corpId, String robotId) { return null; }
                            @Override public boolean hasAppKey(String appKey) { return false; }
                            @Override public String getCorpId(String appKey) { return null; }
                            @Override public String getCorpSecret(String corpId) { return null; }
                            @Override public String getAppSecret(String corpId, String appKey) { return null; }
                        })
                .run(context -> {
                    assertThat(context).hasSingleBean(DingTalkConfigProvider.class);
                    assertThat(context).doesNotHaveBean(DefaultDingTalkConfigProvider.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomServiceConfig {
        @Bean
        public DingTalkService dingTalkService() {
            return new CustomService();
        }
    }

    static class CustomService implements DingTalkService {
        @Override public DingTalkAccountService opsForAccount() { return null; }
        @Override public DingTalkSnsService opsForSns() { return null; }
        @Override public DingTalkSsoService opsForSso() { return null; }
        @Override public DingTalkJsapiService opsForJsapi() { return null; }
        @Override public DingTalkRobotService opsForRobot() { return null; }
        @Override public DingTalkUserService opsForUser() { return null; }
        @Override public String getCorpId(String appKey) { return null; }
        @Override public String getCorpSecret(String corpId) { return null; }
        @Override public String getAppSecret(String corpId, String appKey) { return null; }
        @Override public boolean hasAppKey(String appKey) { return false; }
        @Override public String getAccessToken(String corpId, String appKey) { return null; }
        @Override public String getSnsAccessToken(String corpId, String appId) { return null; }
        @Override public String getSign(String secret, Long timestamp) { return null; }
        @Override public DingTalkConfigProvider getDingTalkConfigProvider() { return null; }
        @Override public DingTalkAccessTokenProvider getDingTalkAccessTokenProvider() { return null; }
        @Override public DingTalkConfigStorage getConfigStorage() { return null; }
    }
}
