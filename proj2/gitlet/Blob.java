package gitlet;

/**
 * Represents the file content pointed by gitlet REFs.
 * It's immutable since created.
 */
@Deprecated
public class Blob {
    public String sha1;
    public String fileName;
}
