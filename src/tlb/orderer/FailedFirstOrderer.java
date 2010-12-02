package tlb.orderer;

import tlb.TlbSuiteFile;
import tlb.domain.SuiteResultEntry;
import tlb.service.TalkToService;
import tlb.splitter.TalksToService;
import tlb.utils.SystemEnvironment;
import tlb.utils.FileUtil;

import java.util.List;
import java.util.ArrayList;

/**
 * @understands ordering to bring failed tests first
 */
public class FailedFirstOrderer extends TestOrderer implements TalksToService {
    private TalkToService toService;
    private List<String> failedTestFiles;
    private FileUtil fileUtil;

    public FailedFirstOrderer(SystemEnvironment environment) {
        super(environment);
        fileUtil = new FileUtil(environment);
    }

    public int compare(TlbSuiteFile o1, TlbSuiteFile o2) {
        if (failedTestFiles == null) {
            failedTestFiles = new ArrayList<String>();
            for (SuiteResultEntry failedSuiteEntry : toService.getLastRunFailedTests()) {
                if (failedSuiteEntry.hasFailed()) failedTestFiles.add(failedSuiteEntry.getName());
            }
        }
        if (failedTestFiles.contains(o1.getName()))
            return -1;
        if (failedTestFiles.contains(o2.getName()))
            return 1;
        return 0;
    }

    public void talksToService(TalkToService service) {
        toService = service;
    }
}
