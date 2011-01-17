package tlb.server.repo;

import tlb.domain.SuiteLevelEntry;
import tlb.utils.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @understands persistence and retrieval of suite based data
 */
public abstract class SuiteEntryRepo<T extends SuiteLevelEntry> implements EntryRepo<T> {
    protected Map<String, T> suiteData;
    protected String namespace;
    protected EntryRepoFactory factory;
    protected String identifier;

    public SuiteEntryRepo() {
        super();
        suiteData = new ConcurrentHashMap<String, T>();
    }

    public Collection<T> list() {
        return suiteData.values();
    }

    public void update(T record) {
        suiteData.put(record.getName(), record);
    }

    public final void add(T entry) {
        throw new UnsupportedOperationException("add not allowed on repository");
    }

    public final void setFactory(EntryRepoFactory factory) {
        this.factory = factory;
    }

    public final void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setIdentifier(String type) {
        this.identifier = type;
    }

    public final void diskDump(Writer writer) throws IOException {
        for (Map.Entry<String, T> entry : suiteData.entrySet()) {
            String line = entry.getKey() + "=" + entry.getValue().dump();
            writer.append(line);
        }
        writer.close();
    }

    public void load(Reader reader) throws IOException, ClassNotFoundException {
        suiteData = readFromFile(reader);
    }

    private Map<String,T> readFromFile(Reader reader) {
        String contents = FileUtil.readIntoString(new BufferedReader(reader));
        String[] lines = contents.split("\n");
        Map<String, T> keyToValue= new HashMap<String, T>();
        for (String line : lines) {
            String[] keyValue = line.split("=");
            keyToValue.put(keyValue[0], parseSingleEntry(keyValue[1]));
        }
        return keyToValue;
    }

    protected abstract T parseSingleEntry(String string);
}
