package com.thoughtworks.cruise.tlb.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResource;
import com.thoughtworks.cruise.tlb.TlbFileResource;

import java.io.File;

public class JunitFileResource implements TlbFileResource {
    private FileResource fileResource;

    public JunitFileResource(File file) {
        this(new FileResource(file));
    }

    public JunitFileResource(Project project, String fileName) {
        this(new FileResource(project, fileName));
    }

    public JunitFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }

    public String getName() {
        return fileResource.getName();
    }

    public File getFile() {
        return fileResource.getFile();
    }

    public void setBaseDir(File file) {
        fileResource.setBaseDir(file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JunitFileResource that = (JunitFileResource) o;
        return fileResource.equals(that.fileResource);

    }

    @Override
    public int hashCode() {
        return fileResource.hashCode();
    }
}