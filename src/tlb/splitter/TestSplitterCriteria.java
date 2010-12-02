package tlb.splitter;

import java.util.List;
import java.io.File;

import tlb.TlbSuiteFile;
import tlb.utils.SystemEnvironment;

/**
 * @understands the criteria for splitting a given test suite 
 */
public abstract class TestSplitterCriteria {
    protected File dir;
    protected final SystemEnvironment env;

    protected TestSplitterCriteria(SystemEnvironment env) {
        this.env = env;
    }

    public abstract List<TlbSuiteFile> filterSuites(List<TlbSuiteFile> fileResources);

    public void setDir(File dir) {
        this.dir = dir;
    }
}
