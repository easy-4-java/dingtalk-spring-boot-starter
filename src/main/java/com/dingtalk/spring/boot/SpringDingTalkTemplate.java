package com.dingtalk.spring.boot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

/**
 * Spring-aware wrapper around the pure-SDK {@link DingTalkTemplate}.
 * <p>
 * Responsibilities specific to the {@code starter} layer (never leak Spring into the SDK):
 * <ul>
 *   <li>Run {@link DefaultDingTalkConfigProvider#init()} once the
 *       application context is fully ready via {@link InitializingBean#afterPropertiesSet()}.</li>
 *   <li>Expose the Spring {@link ApplicationContext} to custom
 *       {@link DingTalkUserIdProvider} / custom beans that opt-in to context awareness.</li>
 *   <li>Record the assigned Spring bean name (useful for logs / metrics).</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Slf4j
public class SpringDingTalkTemplate extends DingTalkTemplate
        implements InitializingBean, ApplicationContextAware, BeanNameAware {

    @Nullable
    @Getter
    private ApplicationContext applicationContext;

    @Nullable
    @Getter
    private String beanName;

    public SpringDingTalkTemplate(DingTalkConfigProvider configProvider,
                                  DingTalkAccessTokenProvider accessTokenProvider) {
        super(configProvider, accessTokenProvider);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() {
        DingTalkConfigProvider p = getDingTalkConfigProvider();
        if (p instanceof DefaultDingTalkConfigProvider defaultProvider) {
            log.debug("SpringDingTalkTemplate[{}]: initialize DefaultDingTalkConfigProvider", beanName);
            defaultProvider.init();
        }
    }
}
