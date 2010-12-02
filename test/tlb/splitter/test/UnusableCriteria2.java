package tlb.splitter.test;

import tlb.TlbSuiteFile;
import tlb.splitter.TestSplitterCriteria;
import tlb.utils.SystemEnvironment;

import java.util.List;

public class UnusableCriteria2 extends TestSplitterCriteria {
    public UnusableCriteria2(SystemEnvironment env) {
        super(env);
    }

    @Override
    public List<TlbSuiteFile> filterSuites(List<TlbSuiteFile> fileResources) {
        throw new RuntimeException("Unusable criteira #2 won't work!");
    }
}