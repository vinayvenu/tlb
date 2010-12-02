package tlb.server.repo;

import org.junit.Before;
import org.junit.Test;
import tlb.TestUtil;
import tlb.TlbConstants;
import tlb.domain.SuiteTimeEntry;
import tlb.utils.SystemEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static tlb.server.repo.EntryRepoFactory.LATEST_VERSION;

public class SmoothingSuiteTimeRepoTest {
    private SuiteTimeRepo repo;
    protected File tmpDir;
    private EntryRepoFactory factory;
    protected HashMap<String,String> env;

    @Before
    public void setUp() throws Exception {
        tmpDir = TestUtil.createTempFolder();
        factory = new EntryRepoFactory(env());
        repo = factory.createSmoothingSuiteTimeRepo("name", LATEST_VERSION);
    }

    private SystemEnvironment env() {
        env = new HashMap<String, String>();
        env.put(TlbConstants.Server.SMOOTHING_FACTOR, "0.05");
        env.put(TlbConstants.Server.TLB_STORE_DIR, tmpDir.getAbsolutePath());
        return new SystemEnvironment(env);
    }

    @Test
    public void shouldLoadASmoothingRepo() throws InterruptedException, ClassNotFoundException, IOException {
        assertThat(repo, is(SuiteTimeRepo.class));
    }

    @Test
    public void shouldNotAllowCreationWithSmoothingFactorHigherThanOne() {
        env.put(TlbConstants.Server.SMOOTHING_FACTOR, "1.1");
        try {
            new EntryRepoFactory(new SystemEnvironment(env)).createSmoothingSuiteTimeRepo("foo", LATEST_VERSION);
            fail("should not have allowed creation");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("smoothing factor must be a value between 0 and 1"));
        }
    }
    
    @Test
    public void shouldNotAllowCreationWithSmoothingFactorLesserThanZero() {
        env.put(TlbConstants.Server.SMOOTHING_FACTOR, "-0.01");
        try {
            new EntryRepoFactory(new SystemEnvironment(env)).createSmoothingSuiteTimeRepo("foo", LATEST_VERSION);
            fail("should not have allowed creation");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("smoothing factor must be a value between 0 and 1"));
        }
    }

    @Test
    public void shouldSmoothenOnUpdate() {
        repo.update(new SuiteTimeEntry("foo.bar.Baz", 10));
        SuiteTimeEntry fetched = new ArrayList<SuiteTimeEntry>(repo.list()).get(0);
        assertThat(fetched, is(new SuiteTimeEntry("foo.bar.Baz", 10)));
        repo.update(new SuiteTimeEntry("foo.bar.Baz", 100));
        fetched = new ArrayList<SuiteTimeEntry>(repo.list()).get(0);
        assertThat(fetched, is(new SuiteTimeEntry("foo.bar.Baz", 15)));
        repo.update(new SuiteTimeEntry("foo.bar.Baz", 2));
        fetched = new ArrayList<SuiteTimeEntry>(repo.list()).get(0);
        assertThat(fetched, is(new SuiteTimeEntry("foo.bar.Baz", 14)));
    }
}