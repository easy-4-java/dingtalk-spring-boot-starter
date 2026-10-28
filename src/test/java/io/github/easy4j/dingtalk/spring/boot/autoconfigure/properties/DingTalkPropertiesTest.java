package io.github.easy4j.dingtalk.spring.boot.autoconfigure.properties;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DingTalkProperties} and its nested config POJOs.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class DingTalkPropertiesTest {

    @Test
    void prefix_shouldBeDingtalk() {
        assertThat(DingTalkProperties.PREFIX).isEqualTo("dingtalk");
    }

    @Test
    void dingTalkProperties_settersAndGetters() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("corp1");
        props.setCorpSecret("secret1");

        DingTalkProperties.CorpApp corpApp = new DingTalkProperties.CorpApp();
        corpApp.setAgentId("a1");
        corpApp.setAppKey("k1");
        corpApp.setAppSecret("s1");
        props.setCorpApps(Arrays.asList(corpApp));

        DingTalkProperties.PersonalMiniApp miniApp = new DingTalkProperties.PersonalMiniApp();
        miniApp.setAppId("ma1");
        miniApp.setAppSecret("ms1");
        props.setApps(Arrays.asList(miniApp));

        DingTalkProperties.Suite suite = new DingTalkProperties.Suite();
        suite.setSuiteId("sid1");
        suite.setAppId("aid1");
        suite.setSuiteKey("sk1");
        suite.setSuiteSecret("ss1");
        props.setSuites(Arrays.asList(suite));

        DingTalkProperties.Login login = new DingTalkProperties.Login();
        login.setAppId("la1");
        login.setAppSecret("ls1");
        props.setLogins(Arrays.asList(login));

        DingTalkProperties.Robot robot = new DingTalkProperties.Robot();
        robot.setRobotId("rid1");
        robot.setAccessToken("at1");
        robot.setSecretToken("st1");
        props.setRobots(Arrays.asList(robot));

        assertThat(props.getCorpId()).isEqualTo("corp1");
        assertThat(props.getCorpSecret()).isEqualTo("secret1");
        assertThat(props.getCorpApps()).hasSize(1);
        assertThat(props.getApps()).hasSize(1);
        assertThat(props.getSuites()).hasSize(1);
        assertThat(props.getLogins()).hasSize(1);
        assertThat(props.getRobots()).hasSize(1);
    }

    @Test
    void dingTalkCorpApp_settersAndGetters() {
        DingTalkProperties.CorpApp props = new DingTalkProperties.CorpApp();
        props.setAgentId("a1");
        props.setAppKey("k1");
        props.setAppSecret("s1");
        assertThat(props.getAgentId()).isEqualTo("a1");
        assertThat(props.getAppKey()).isEqualTo("k1");
        assertThat(props.getAppSecret()).isEqualTo("s1");
        assertThat(props.toString()).contains("agentId=a1");
    }

    @Test
    void dingTalkPersonalMiniApp_settersAndGetters() {
        DingTalkProperties.PersonalMiniApp props = new DingTalkProperties.PersonalMiniApp();
        props.setAppId("a1");
        props.setAppSecret("s1");
        assertThat(props.getAppId()).isEqualTo("a1");
        assertThat(props.getAppSecret()).isEqualTo("s1");
        assertThat(props.toString()).contains("appId=a1");
    }

    @Test
    void dingTalkSuite_settersAndGetters() {
        DingTalkProperties.Suite props = new DingTalkProperties.Suite();
        props.setSuiteId("sid1");
        props.setAppId("aid1");
        props.setSuiteKey("sk1");
        props.setSuiteSecret("ss1");
        assertThat(props.getSuiteId()).isEqualTo("sid1");
        assertThat(props.getAppId()).isEqualTo("aid1");
        assertThat(props.getSuiteKey()).isEqualTo("sk1");
        assertThat(props.getSuiteSecret()).isEqualTo("ss1");
        assertThat(props.toString()).contains("suiteId=sid1");
    }

    @Test
    void dingTalkLogin_settersAndGetters() {
        DingTalkProperties.Login props = new DingTalkProperties.Login();
        props.setAppId("a1");
        props.setAppSecret("s1");
        assertThat(props.getAppId()).isEqualTo("a1");
        assertThat(props.getAppSecret()).isEqualTo("s1");
        assertThat(props.toString()).contains("appId=a1");
    }

    @Test
    void dingTalkRobot_settersAndGetters() {
        DingTalkProperties.Robot props = new DingTalkProperties.Robot();
        props.setRobotId("r1");
        props.setAccessToken("at1");
        props.setSecretToken("st1");
        assertThat(props.getRobotId()).isEqualTo("r1");
        assertThat(props.getAccessToken()).isEqualTo("at1");
        assertThat(props.getSecretToken()).isEqualTo("st1");
        assertThat(props.toString()).contains("robotId=r1");
    }
}
