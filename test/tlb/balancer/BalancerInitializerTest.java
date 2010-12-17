package tlb.balancer;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import tlb.TlbConstants;
import tlb.orderer.FailedFirstOrderer;
import tlb.service.TalkToTlbServer;
import tlb.splitter.CountBasedTestSplitterCriteria;
import tlb.utils.SystemEnvironment;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static tlb.TestUtil.updateEnv;

public class BalancerInitializerTest {
    protected BalancerInitializer initializer;
    private HashMap<String, String> systemEnv;
    private SystemEnvironment env;

    @Before
    public void setUp() {
        systemEnv = new HashMap<String, String>();
        env = new SystemEnvironment(systemEnv);
        initializer = new BalancerInitializer(env);
    }

    @Test
    public void shouldCreateApplicationContextWithNecessaryObjects() throws NoSuchFieldException, IllegalAccessException {
        updateEnv(env, TlbConstants.TLB_CRITERIA, CountBasedTestSplitterCriteria.class.getCanonicalName());
        updateEnv(env, TlbConstants.TLB_ORDERER, FailedFirstOrderer.class.getCanonicalName());
        updateEnv(env, TlbConstants.TALK_TO_SERVICE, TalkToTlbServer.class.getCanonicalName());
        updateEnv(env, TlbConstants.Balancer.TLB_BALANCER_PORT, "614");
        updateEnv(env, TlbConstants.TlbServer.URL, "http://foo.bar.com:7019");
        ConcurrentMap<String,Object> map = initializer.application().getContext().getAttributes();
        assertThat(map.get(TlbClient.SPLITTER), is(CountBasedTestSplitterCriteria.class));
        assertThat(map.get(TlbClient.ORDERER), is(FailedFirstOrderer.class));
        assertThat(map.get(TlbClient.TALK_TO_SERVICE), is(TalkToTlbServer.class));
        assertThat(map.get(TlbClient.APP_COMPONENT), is(Component.class));
        assertThat(map.get(TlbClient.APP_COMPONENT), sameInstance((Object) initializer.init()));
    }

    @Test
    public void shouldInitializeTlbToRunOnConfiguredPort() throws NoSuchFieldException, IllegalAccessException {
        updateEnv(env, TlbConstants.Balancer.TLB_BALANCER_PORT, "4321");
        assertThat(initializer.appPort(), is(4321));
    }
}
