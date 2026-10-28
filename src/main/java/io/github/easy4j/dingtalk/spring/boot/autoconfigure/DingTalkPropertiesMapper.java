package io.github.easy4j.dingtalk.spring.boot.autoconfigure;

import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkCorpAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkLoginConfig;
import io.github.easy4j.dingtalk.config.DingTalkPersonalMiniAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.config.DingTalkSuiteConfig;
import io.github.easy4j.dingtalk.spring.boot.autoconfigure.properties.DingTalkProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.lang.Nullable;

/**
 * Package-private mapper that converts the Spring-specific {@link DingTalkProperties}
 * tree into the pure-SDK {@link DingTalkConfig} graph.  Keeping this mapper here
 * (in the starter, not the SDK) ensures the SDK remains zero-Spring and the
 * auto-configuration class stays small and readable.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
final class DingTalkPropertiesMapper {

    private DingTalkPropertiesMapper() {}

    static DingTalkConfig toConfig(DingTalkProperties properties) {
        DingTalkConfig cfg = new DingTalkConfig();
        cfg.setCorpId(properties.getCorpId());
        cfg.setCorpSecret(properties.getCorpSecret());
        cfg.setCorpApps(mapCorpApps(properties.getCorpApps()));
        cfg.setApps(mapPersonalApps(properties.getApps()));
        cfg.setSuites(mapSuites(properties.getSuites()));
        cfg.setLogins(mapLogins(properties.getLogins()));
        cfg.setRobots(mapRobots(properties.getRobots()));
        return cfg;
    }

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
