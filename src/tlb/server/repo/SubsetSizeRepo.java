package tlb.server.repo;

import tlb.domain.SubsetSizeEntry;
import tlb.utils.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @understands storage and retrival of size of subset of total suites run by job
 */
public class SubsetSizeRepo implements EntryRepo<SubsetSizeEntry> {
    private List<SubsetSizeEntry> entries;

    public SubsetSizeRepo() {
        entries = new ArrayList<SubsetSizeEntry>();
    }

    public Collection<SubsetSizeEntry> list() {
        return entries;
    }

    public Collection<SubsetSizeEntry> list(String version) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("versioning not allowed");
    }

    public void update(SubsetSizeEntry entry) {
        throw new UnsupportedOperationException("update not allowed on repository");
    }

    public void diskDump(Writer writer) throws IOException {
        for (SubsetSizeEntry entry : entries) {
            writer.append(entry.dump());
        }
        writer.close();
    }

    public void load(Reader reader) throws IOException, ClassNotFoundException {
        entries = readFromFile(reader);
    }

    private List<SubsetSizeEntry> readFromFile(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        return SubsetSizeEntry.parse(FileUtil.readIntoString(bufferedReader));
    }

    public void add(SubsetSizeEntry entry) {
        entries.add(entry);
    }

    public void setFactory(EntryRepoFactory factory) {
        //doesn't need
    }

    public void setNamespace(String namespace) {
        //doesn't need
    }

    public void setIdentifier(String type) {
        //doesn't need
    }
}
