package com.github.tlb.twist;

import com.github.tlb.TlbSuiteFile;
import com.github.tlb.utils.SuiteFileConvertor;
import org.junit.Test;
import org.junit.After;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.github.tlb.splitter.TestSplitterCriteria;
import com.github.tlb.TlbFileResource;

public class LoadBalancedTwistSuiteTest {

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File("destination"));
    }

    @Test
    public void shouldLoadScenarioExecutorFromClassPath() throws Exception {
        TestSplitterCriteria criteria = mock(TestSplitterCriteria.class);

        File folder = folder("folder");
        final SuiteFileConvertor convertor = new SuiteFileConvertor();
        when(criteria.filterSuites(any(List.class))).thenReturn(convertor.toTlbSuiteFiles(scenarioResource(folder,1, 2)));

        LoadBalancedTwistSuite suite = new LoadBalancedTwistSuite(criteria);

        suite.balance(folder.getAbsolutePath(), "destination");

        File destination = new File("destination");
        assertThat(destination.exists(), is(true));
        assertThat(destination.isDirectory(), is(true));
        assertThat(FileUtils.listFiles(destination, null, false).size(), is(2));
    }

    private File folder(String name) {
        File folder = new File(name);
        folder.mkdir();
        folder.deleteOnExit();
        return folder;
    }

    private List<TlbFileResource> scenarioResource(File folder, int... names) throws IOException {
        List<TlbFileResource> resources = new ArrayList<TlbFileResource>();
        for (int name : names) {
            File file = new File(folder.getAbsolutePath(), "base" + name);
            file.createNewFile();
            file.deleteOnExit();
            resources.add(new SceanrioFileResource(file));
        }
        return resources;
    }
}
