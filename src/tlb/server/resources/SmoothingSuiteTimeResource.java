package tlb.server.resources;

import tlb.server.repo.EntryRepo;
import tlb.server.repo.EntryRepoFactory;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import java.io.IOException;

/**
 * @understands smoothed run time of suite reported by jobs
 */
public class SmoothingSuiteTimeResource extends SuiteTimeResource {
    public SmoothingSuiteTimeResource(Context context, Request request, Response response) {
        super(context, request, response);
    }

    @Override
    protected EntryRepo getRepo(EntryRepoFactory repoFactory, String key) throws ClassNotFoundException, IOException {
        return repoFactory.createSmoothingSuiteTimeRepo(key, EntryRepoFactory.LATEST_VERSION);
    }
}