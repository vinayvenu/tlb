package tlb.utils;

import static tlb.TlbConstants.TLB_TMP_DIR;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class FileUtil {
    private SystemEnvironment env;
    public static final Logger logger = Logger.getLogger(FileUtil.class.getName());

    public FileUtil(SystemEnvironment env) {
        this.env = env;
    }

    public String tmpDir() {
        return env.tmpDir();
    }

    public String classFileRelativePath(String testClass) {
        return testClass.replaceAll("\\.", "/") + ".class";
    }

    public File getUniqueFile(String seedString) {
        String fileName = DigestUtils.md5Hex(seedString);
        File file = new File(new File(tmpDir()), fileName);
        logger.info(String.format("unique file name %s translated to %s", seedString, file.getAbsolutePath()));
        return file;
    }

    public static List<File> toFileList(Iterator<File> reports) {
        List<File> foo = new ArrayList<File>();
        while (reports.hasNext()) {
            foo.add(reports.next());
        }
        return foo;
    }

    public static String stripExtension(String fileName) {
        int index = fileName.lastIndexOf(".") == -1 ? fileName.length() : fileName.lastIndexOf(".");
        return fileName.substring(0, index);
    }
}
