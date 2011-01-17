package tlb.server.repo;

import tlb.domain.Entry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

/**
 * @understands storage and retrieval of records 
 */
public interface EntryRepo<T extends Entry> {
    Collection<T> list();

    Collection<T> list(String version) throws IOException, ClassNotFoundException;

    void update(T entry);

    void diskDump(Writer writer) throws IOException;

    void load(Reader reader) throws IOException, ClassNotFoundException;

    void add(T entry);

    void setFactory(EntryRepoFactory factory);

    void setNamespace(String namespace);

    void setIdentifier(String type);
}
