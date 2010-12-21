package tlb.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.junit.Before;
import org.junit.Test;
import tlb.TestUtil;
import tlb.TlbFileResource;
import tlb.TlbSuiteFile;
import tlb.orderer.TestOrderer;
import tlb.splitter.CountBasedTestSplitterCriteria;
import tlb.splitter.JobFamilyAwareSplitterCriteria;
import tlb.utils.FileUtil;
import tlb.utils.SuiteFileConvertor;
import tlb.utils.SystemEnvironment;

import java.io.File;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static tlb.TlbConstants.Go.GO_SERVER_URL;
import static tlb.TlbConstants.TLB_CRITERIA;

public class LoadBalancedFileSetTest {
    private LoadBalancedFileSet fileSet;
    private File projectDir;
    private FileUtil fileUtil;

    @Before
    public void setUp() throws Exception {
        SystemEnvironment env = new SystemEnvironment(new HashMap<String, String>());
        fileSet = new LoadBalancedFileSet(env);
        fileUtil = new FileUtil(env);
        projectDir = TestUtil.createTempFolder();
        initFileSet(fileSet);
    }

    private void initFileSet(FileSet fileSet) {
        fileSet.setDir(projectDir);
        fileSet.setProject(new Project());
    }

    @Test
    public void shouldReturnAllFilesWhenThereIsNothingToSplit() {
        File newFile = TestUtil.createFileInFolder(projectDir, "abc");

        Iterator files = fileSet.iterator();

        assertThat(files.hasNext(), is(true));
        assertThat(((FileResource) files.next()).getFile(), is(newFile));
        assertThat(files.hasNext(), is(false));
    }

    @Test
    public void shouldReturnAListOfFilesWhichMatchAGivenMatcher() {
        TestUtil.createFileInFolder(projectDir, "excluded");
        File included = TestUtil.createFileInFolder(projectDir, "included");

        JobFamilyAwareSplitterCriteria criteria = mock(JobFamilyAwareSplitterCriteria.class);
        TlbFileResource fileResource = new JunitFileResource(included);
        final SuiteFileConvertor convertor = new SuiteFileConvertor();
        when(criteria.filterSuites(any(List.class))).thenReturn(convertor.toTlbSuiteFiles(Arrays.asList(fileResource)));

        fileSet = new LoadBalancedFileSet(criteria, TestOrderer.NO_OP);
        fileSet.setDir(projectDir);
        initFileSet(fileSet);
        Iterator files = fileSet.iterator();

        assertThat(files.hasNext(), is(true));
        assertThat(((FileResource) files.next()).getFile(), is(included));
        assertThat(files.hasNext(), is(false));
    }

    @Test
    public void shouldReturnAListOfFilesInOrderReturnedByReorderer() {
        TestUtil.createFileInFolder(projectDir, "excluded");
        JunitFileResource resourceOne = new JunitFileResource(TestUtil.createFileInFolder(projectDir, "C"));
        JunitFileResource resourceTwo = new JunitFileResource(TestUtil.createFileInFolder(projectDir, "B"));
        JunitFileResource resourceThree = new JunitFileResource(TestUtil.createFileInFolder(projectDir, "A"));

        JobFamilyAwareSplitterCriteria criteria = mock(JobFamilyAwareSplitterCriteria.class);

        final SuiteFileConvertor convertor = new SuiteFileConvertor();
        when(criteria.filterSuites(any(List.class))).thenReturn(convertor.toTlbSuiteFiles(Arrays.asList((TlbFileResource) resourceOne, resourceTwo, resourceThree)));

        fileSet = new LoadBalancedFileSet(criteria, new TestOrderer(new SystemEnvironment()) {
            public int compare(TlbSuiteFile o1, TlbSuiteFile o2) {
                return 1;
            }
        });
        fileSet.setDir(projectDir);
        initFileSet(fileSet);
        Iterator files = fileSet.iterator();

        assertThat(files.hasNext(), is(true));
        List<FileResource> resources = new ArrayList<FileResource>();

        while (files.hasNext()) {
            resources.add((FileResource) files.next());
        }
        List<FileResource> expectedResourcesOrder = new ArrayList<FileResource>();
        expectedResourcesOrder.add(resourceThree.getFileResource());
        expectedResourcesOrder.add(resourceTwo.getFileResource());
        expectedResourcesOrder.add(resourceOne.getFileResource());
        assertThat(resources, is(expectedResourcesOrder));
    }

    @Test
    public void shouldUseSystemPropertyToInstantiateCriteria() {
        fileSet = new LoadBalancedFileSet(initEnvironment("tlb.splitter.CountBasedTestSplitterCriteria"));
        fileSet.setDir(projectDir);
        assertThat(fileSet.getSplitterCriteria(), instanceOf(CountBasedTestSplitterCriteria.class));
    }

    @Test
    public void shouldSetFileSetDir() throws Exception{
        JobFamilyAwareSplitterCriteria criteria = mock(JobFamilyAwareSplitterCriteria.class);
        fileSet = new LoadBalancedFileSet(criteria, TestOrderer.NO_OP);
        fileSet.setDir(projectDir);
        verify(criteria).setDir(projectDir);
        assertThat(fileSet.getDir(), is(projectDir));
    }

    private SystemEnvironment initEnvironment(String strategyName) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(TLB_CRITERIA, strategyName);
        map.put(GO_SERVER_URL, "https://localhost:8154/cruise");
        return new SystemEnvironment(map);
    }
}
