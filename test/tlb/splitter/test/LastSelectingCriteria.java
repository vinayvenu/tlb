package tlb.splitter.test;

import tlb.TlbSuiteFile;
import tlb.splitter.TestSplitterCriteria;
import tlb.utils.SystemEnvironment;

import java.util.Arrays;
import java.util.List;

public class LastSelectingCriteria extends TestSplitterCriteria {
    public LastSelectingCriteria(SystemEnvironment env) {
        super(env);
    }

    @Override
    public List<TlbSuiteFile> filterSuites(List<TlbSuiteFile> fileResources) {
        return Arrays.asList(fileResources.get(fileResources.size() - 1));
    }
}