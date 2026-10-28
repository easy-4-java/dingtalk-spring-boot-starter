package io.github.easy4j.dingtalk.spring.boot.autoconfigure;

import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkCorpAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkLoginConfig;
import io.github.easy4j.dingtalk.config.DingTalkPersonalMiniAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.config.DingTalkSuiteConfig;
import io.github.easy4j.dingtalk.spring.boot.autoconfigure.properties.DingTalkProperties;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkPropertiesMapperTest {

    @Test
    void mapsAllFiveNestedLists() {
        DingTalkProperties p = new DingTalkProperties();
        p.setCorpId("corp1");
        p.setCorpSecret("secret1");
        List<DingTalkProperties.CorpApp> cas = new ArrayList<>();
        DingTalkProperties.CorpApp ca = new DingTalkProperties.CorpApp();
        ca.setAgentId("a1"); ca.setAppKey("k1"); ca.setAppSecret("s1"); cas.add(ca);
        p.setCorpApps(cas);

        List<DingTalkProperties.PersonalMiniApp> apps = new ArrayList<>();
        DingTalkProperties.PersonalMiniApp pa = new DingTalkProperties.PersonalMiniApp();
        pa.setAppId("id1"); pa.setAppSecret("ps1"); apps.add(pa);
        p.setApps(apps);

        List<DingTalkProperties.Suite> suites = new ArrayList<>();
        DingTalkProperties.Suite su = new DingTalkProperties.Suite();
        su.setSuiteId("sid1"); su.setAppId("aid1");
        su.setSuiteKey("sk1"); su.setSuiteSecret("ss1"); suites.add(su);
        p.setSuites(suites);

        List<DingTalkProperties.Login> logins = new ArrayList<>();
        DingTalkProperties.Login lg = new DingTalkProperties.Login();
        lg.setAppId("la1"); lg.setAppSecret("ls1"); logins.add(lg);
        p.setLogins(logins);

        List<DingTalkProperties.Robot> robots = new ArrayList<>();
        DingTalkProperties.Robot rb = new DingTalkProperties.Robot();
        rb.setRobotId("r1"); rb.setAccessToken("rat"); rb.setSecretToken("rst");
        robots.add(rb);
        p.setRobots(robots);

        DingTalkConfig c = DingTalkPropertiesMapper.toConfig(p);
        assertEquals("corp1", c.getCorpId());
        assertEquals("secret1", c.getCorpSecret());
        List<DingTalkCorpAppConfig> cCorpApps = c.getCorpApps();
        assertNotNull(cCorpApps); assertEquals(1, cCorpApps.size());
        assertEquals("a1", cCorpApps.get(0).getAgentId());
        assertEquals("k1", cCorpApps.get(0).getAppKey());
        assertEquals("s1", cCorpApps.get(0).getAppSecret());

        List<DingTalkPersonalMiniAppConfig> cApps = c.getApps();
        assertEquals(1, cApps.size());
        assertEquals("id1", cApps.get(0).getAppId());

        List<DingTalkSuiteConfig> cSuites = c.getSuites();
        assertEquals(1, cSuites.size());
        assertEquals("sid1", cSuites.get(0).getSuiteId());

        List<DingTalkLoginConfig> cLogins = c.getLogins();
        assertEquals(1, cLogins.size());
        assertEquals("la1", cLogins.get(0).getAppId());

        List<DingTalkRobotConfig> cRobots = c.getRobots();
        assertEquals(1, cRobots.size());
        assertEquals("r1", cRobots.get(0).getRobotId());
    }

    @Test
    void nullListsBecomeEmptyImmutableLists() {
        DingTalkProperties p = new DingTalkProperties();
        p.setCorpId("corp2");
        DingTalkConfig c = DingTalkPropertiesMapper.toConfig(p);
        assertEquals("corp2", c.getCorpId());
        assertNotNull(c.getCorpApps());
        assertTrue(c.getCorpApps().isEmpty());
        assertTrue(c.getApps().isEmpty());
        assertTrue(c.getSuites().isEmpty());
        assertTrue(c.getLogins().isEmpty());
        assertTrue(c.getRobots().isEmpty());
    }
}
