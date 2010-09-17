package com.github.tlb.balancer;

import com.github.tlb.TlbConstants;
import com.github.tlb.orderer.FailedFirstOrderer;
import com.github.tlb.service.TalkToTlbServer;
import com.github.tlb.splitter.CountBasedTestSplitterCriteria;
import com.github.tlb.utils.SystemEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BalancerInitializerTest {
    protected BalancerInitializer initializer;
    private HashMap<String, String> systemEnv;

    @Before
    public void setUp() {
        systemEnv = new HashMap<String, String>();
        initializer = new BalancerInitializer(new SystemEnvironment(systemEnv));
    }

    @Test
    public void shouldCreateApplicationContextWithNecessaryObjects() {
        systemEnv.put(TlbConstants.TLB_CRITERIA, CountBasedTestSplitterCriteria.class.getCanonicalName());
        systemEnv.put(TlbConstants.TLB_ORDERER, FailedFirstOrderer.class.getCanonicalName());
        systemEnv.put(TlbConstants.TALK_TO_SERVICE, TalkToTlbServer.class.getCanonicalName());
        systemEnv.put(TlbConstants.Balancer.TLB_BALANCER_PORT, "614");
        systemEnv.put(TlbConstants.TlbServer.URL, "http://foo.bar.com:7019");
        ConcurrentMap<String,Object> map = initializer.application().getContext().getAttributes();
        assertThat(map.get(TlbClient.SPLITTER), is(CountBasedTestSplitterCriteria.class));
        assertThat(map.get(TlbClient.ORDERER), is(FailedFirstOrderer.class));
        assertThat(map.get(TlbClient.TALK_TO_SERVICE), is(TalkToTlbServer.class));
        assertThat(map.get(TlbClient.APP_COMPONENT), is(Component.class));
        assertThat(map.get(TlbClient.APP_COMPONENT), sameInstance((Object) initializer.init()));
    }

    @Test
    public void shouldInitializeTlbToRunOnConfiguredPort() {
        systemEnv.put(TlbConstants.Balancer.TLB_BALANCER_PORT, "4321");
        assertThat(initializer.appPort(), is(4321));
    }
}
