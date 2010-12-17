package tlb.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tlb.TlbConstants.TLB_TMP_DIR;

/**
 * @understands reading the environment variables of the system on which tlb runs
 */
public class SystemEnvironment {
    private static final Pattern REF = Pattern.compile(".*\\$\\{(.+?)\\}.*");
    public static final String TMP_DIR = "java.io.tmpdir";

    private Map<String, String> variables;

    public static final Logger logger = Logger.getLogger(SystemEnvironment.class.getName());

    public SystemEnvironment(Map<String, String> variables) {
        this.variables = new HashMap<String, String>();
        for (Map.Entry<String, String> ent : variables.entrySet()) {
            this.variables.put(ent.getKey(), ent.getValue());
        }
    }

    public SystemEnvironment() {
        this(System.getenv());
    }

    public String val(String key) {
        String value = variables.get(key);
        value = substituteRefs(value);
        return value;
    }

    private String substituteRefs(String value) {
        if (value == null) return null;
        final Matcher matcher = REF.matcher(value);
        if (matcher.find()) {
            final String ref = matcher.group(1);
            return substituteRefs(value.replace(String.format("${%s}", ref), val(ref)));
        }
        return value;
    }

    public String val(String key, String defaultValue) {
        String value = val(key);
        return value == null ? defaultValue : value;
    }

    public String getDigest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(variables);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return DigestUtils.md5Hex(out.toByteArray());
    }

    public String tmpDir() {
        String tmpParent = val(TLB_TMP_DIR);
        if (tmpParent == null) {
            tmpParent = System.getProperty(SystemEnvironment.TMP_DIR);
            logger.warning(String.format("defaulting tlb tmp directory to %s", tmpParent));
        }
        logger.info(String.format("using %s as tlb temp directory", tmpParent));
        File tmpDir = new File(tmpParent, getDigest());
        createDirIfNecessary(tmpDir);
        return tmpDir.getAbsolutePath();
    }

    private void createDirIfNecessary(final File tmpDirectory) {
        final String tmpDir = tmpDirectory.getAbsolutePath();
        logger.info(String.format("checking for existance of directory %s as tlb tmpdir", tmpDir));
        if (tmpDirectory.exists()) {
            if (! tmpDirectory.isDirectory()) {
                String fileInsteedOfDirectoryMessage = String.format("tlb tmp dir %s is a file, it must be a directory", tmpDir);
                logger.warning(fileInsteedOfDirectoryMessage);
                throw new IllegalStateException(fileInsteedOfDirectoryMessage);
            }
            logger.info(String.format("directory %s exists, creation not required", tmpDir));
        } else {
            logger.info(String.format("directory %s doesn't exist, creating it now", tmpDir));
            try {
                FileUtils.forceMkdir(tmpDirectory);
            } catch (IOException e) {
                logger.log(Level.WARNING, String.format("could not create directory %s", tmpDirectory.getAbsolutePath()), e);
                throw new RuntimeException(e);
            }
            logger.info(String.format("created directory %s, which is to be used as tlb tmp dir.", tmpDir));
        }
    }
}
