package tlb.splitter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tlb.*;
import tlb.service.TalkToGoServer;
import tlb.utils.SuiteFileConvertor;
import tlb.utils.SystemEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class JobFamilyAwareSplitterCriteriaTest {
    private TestUtil.LogFixture logFixture;

    @Before
    public void setUp() {
        logFixture = new TestUtil.LogFixture();
    }

    @After
    public void tearDown() {
        logFixture.stopListening();
    }

    @Test
    public void testFilterShouldPublishNumberOfSuitesSelectedForRunning() {
        HashMap<String, String> envMap = new HashMap<String, String>();
        envMap.put(TlbConstants.Cruise.CRUISE_JOB_NAME, "build-1");
        TalkToGoServer toCruise = mock(TalkToGoServer.class);
        when(toCruise.totalPartitions()).thenReturn(3);

        JobFamilyAwareSplitterCriteria criteria = new JobFamilyAwareSplitterCriteria(new SystemEnvironment(envMap)) {
            protected List<TlbSuiteFile> subset(List<TlbSuiteFile> fileResources) {
                TlbSuiteFile foo = new TlbSuiteFileImpl("foo");
                TlbSuiteFile bar = new TlbSuiteFileImpl("bar");
                return Arrays.asList(foo, bar);
            }
        };
        criteria.talksToService(toCruise);
        logFixture.startListening();
        final SuiteFileConvertor convertor = new SuiteFileConvertor();
        final List<TlbSuiteFile> suiteFiles = convertor.toTlbSuiteFiles(new ArrayList<TlbFileResource>());
        List<TlbFileResource> resources = convertor.toTlbFileResources(criteria.filterSuites(suiteFiles));
        logFixture.assertHeard("got total of 0 files to balance");
        logFixture.assertHeard("total jobs to distribute load [ 3 ]");
        logFixture.assertHeard("assigned total of 2 files to [ build-1 ]");
        assertThat(resources.size(), is(2));
        verify(toCruise).publishSubsetSize(2);
    }
}
