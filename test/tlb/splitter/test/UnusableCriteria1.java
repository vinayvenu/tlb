package tlb.splitter.test;

import tlb.TlbSuiteFile;
import tlb.splitter.TestSplitterCriteria;
import tlb.utils.SystemEnvironment;

import java.util.List;

public class UnusableCriteria1 extends TestSplitterCriteria {
    public UnusableCriteria1(SystemEnvironment env) {
        super(env);
    }

    @Override
    public List<TlbSuiteFile> filterSuites(List<TlbSuiteFile> fileResources) {
        throw new RuntimeException("Unusable criteira #1 won't work!");
    }
}
