package gitlet;

import com.puppycrawl.tools.checkstyle.checks.indentation.PackageDefHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manage how blobs and commits are stored.
 * Provide method to read/write files,
 * hide the details of interacting with file system,
 * and AVOID using Utils.java ANYWHERE ELSE.
 * All io operation should be performed vid this class
 */
public class RepositoryIO {
    //but in this class,use Util or public method arbitrarily;
    /**
     * The number of digits we extract from sha1 to name the directory.
     */
    private static final int SHA1_DIR_NAME_LENGTH=2;
    /**
     * The number of digits we take to abbreviate the sha1.
     */
    private static final int ABBREVIATED_SHA1_LENGTH=7;

    private final Path gitletDirectory;
    private final Path workingDirectory;
    private final Path objectsDirectory;
    private final Path commitsDirectory;
    public RepositoryIO(Path workingDirectory){
        this.workingDirectory=workingDirectory;
        //just hard code it...
        this.gitletDirectory=joinPath(workingDirectory,".gitlet");
        this.objectsDirectory=joinPath(gitletDirectory,"objects");
        this.commitsDirectory=joinPath(objectsDirectory,"commits");
    }
    public void InitDirectories(){
        mkdir(gitletDirectory);
        mkdir(objectsDirectory);
        mkdir(commitsDirectory);
    }

    /*Path*/
    public Path joinPath(String first,String... others){
        return Path.of(first,others);
    }
    public Path joinPath(Path first,String... others){
        return Path.of(first.toString(),others);
    }

    /**
     * Get sorted file names of the directory
     * @param directory directory that contains files
     * @return sorted array of file names
     */
    public String[] plainFileNamesIn(Path directory){
        return Utils.plainFilenamesIn(directory.toFile()).toArray(new String[0]);
    }
    public String[] namesIn(Path directory){
        return directory.toFile().list();
    }

    /*sha1*/

    /**
     * Compute the sha1 of the serialized content
     * @param content serialized content
     * @return sha1
     */
    public String sha1(byte[] content){
        return Utils.sha1(content);
    }

    /**
     * Get the abbreviated sha1
     * @param sha1 sha1 of the blob,shouldn't be too short or null.
     * @return the abbreviated sha1
     */
    public String abbreviateSha1(String sha1){
        return sha1.substring(0,ABBREVIATED_SHA1_LENGTH);
    }


    /*read/write/delete*/

    /**
     * Write the content to the given file.
     * @param content serialized content
     * @param dstFilePath file path
     */
    public void writeToFile(byte[] content,Path dstFilePath){
        Utils.writeContents(dstFilePath.toFile(), (Object) content);
    }

    /**
     * Read content from the src path,and save it as a blob in the repository.
     * @param fileName file name in the working directory.
     * @return sha1 of the blob
     */
    public String writeBlob(String fileName){
        Path filePath=joinPath(workingDirectory,fileName);
        return writeBlob(read(filePath));
    }
    /**
     * Save the serialized content as a blob in the repository.
     * @param src serialized content
     * @return sha1 of the blob
     */
    public String writeBlob(byte[] src){
        return this.writeBlob(src,this.objectsDirectory);
    }
    /**
     * Save the commit as a blob in the repository.
     * @param commit commit
     * @return sha1 of the commit
     */
    public String writeCommit(Commit commit){
        return this.writeBlob(serialize(commit),this.commitsDirectory);//return sha1
    }


    /**
     * Read content as byte array.
     * @param src filePath,shouldn't be directory or null
     * @return content as byte array.
     */
    public byte[] read(Path src){
        //for working directory
        return Utils.readContents(src.toFile());
    }

    /**
     * Get the content as byte array of the blob
     * @param sha1 blob sha1
     * @return The content,null if not found
     */
    public byte[] readBlob(String sha1){
        return readBlob(sha1,this.objectsDirectory);//return fileContent
    }
    /**
     * Get the content as byte array of the blob
     * @param sha1Abbr blob abbreviated sha1
     * @return The content,null if not found
     */
    public byte[] readBlobWithAbbr(String sha1Abbr){
        return readBlobWithAbbr(sha1Abbr,this.objectsDirectory);//return fileContent
    }
    /**
     * Get the commit
     * @param sha1 blob sha1
     * @return The commit,null if not found
     */
    public Commit readCommit(String sha1){
        byte[] content=readBlob(sha1,this.commitsDirectory);
        if (content==null){
            return null;
        }
        return deserialize(content,Commit.class);
    }
    /**
     * Get the commit
     * @param sha1Abbr blob abbreviated sha1
     * @return The commit,null if not found
     */
    public Commit readCommitWithAbbr(String sha1Abbr){
        byte[] content=readBlobWithAbbr(sha1Abbr,this.commitsDirectory);
        if (content==null){
            return null;
        }
        return deserialize(content,Commit.class);
    }

    /**
     * Remove a file(not directory) with the given path.
     * DON'T call this unless you know what you're doing!
     * @param filePath file path
     * @return true if deleted
     */
    public boolean deleteFile(Path filePath){
        File file=filePath.toFile();
        if (!file.exists()||file.isDirectory()){
            return false;
        }
        return file.delete();
    }
    /**
     * Remove an empty directory with the given path.
     * DON'T call this unless you know what you're doing!
     * @param filePath file path
     * @return true if deleted
     */
    public boolean deleteDirectory(Path filePath){
        File file=filePath.toFile();
        if (!file.exists()||!file.isDirectory()){
            return false;
        }
        return file.delete();
    }

    public Commit[] getAllCommit(){
        return null;
    }

    /**
     * make a new directory
     * @param dirPath directory path,should not exist and should be a directory path
     * @return true if made
     */
    public boolean mkdir(Path dirPath){
        return dirPath.toFile().mkdir();
    }

    /*Conversion*/

    /**
     * Return the string represented by the content
     * @param content serialized content,should not be null
     * @return the string represented by the content
     */
    public String deserialize(byte[] content){
        return new String(content, StandardCharsets.UTF_8);
    }
    /**
     * Return the object represented by the content
     * @param content serialized content,should not be null
     * @param expectedClass object class
     * @return the object
     * @param <T> object type
     */
    public <T extends Serializable> T deserialize(byte[] content,Class<T> expectedClass){
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new ByteArrayInputStream(content));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        }catch (IOException | ClassCastException
                | ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /**
     * Serialize the given object
     * @param obj object
     * @return serialized content
     */
    public byte[] serialize(Serializable obj){
        return Utils.serialize(obj);
    }

    /**
     * Write the content to the repository,store it as a loosen object
     * (a file in a directory)
     * @param content serialized content
     * @param directoryPath directory path
     * @return sha1 of the content
     */
    private String writeBlob(byte[] content,Path directoryPath){
        String sha1=sha1(content);
        Path blobDir=this.joinPath(directoryPath,sha1.substring(0,SHA1_DIR_NAME_LENGTH));
        this.mkdir(blobDir);
        Path blob=this.joinPath(blobDir,sha1.substring(SHA1_DIR_NAME_LENGTH));
        Utils.writeContents(blob.toFile(), (Object) content);
        return sha1;
    }

    /**
     * Read the blob represented by the given sha1
     * @param sha1 sha1 of the blob
     * @param directoryPath the directory that contains the blob
     * @return the content,null if not found
     */
    private byte[] readBlob(String sha1,Path directoryPath){
        Path blobDir=this.joinPath(directoryPath,sha1.substring(0,SHA1_DIR_NAME_LENGTH));
        File blob=this.joinPath(blobDir,sha1.substring(SHA1_DIR_NAME_LENGTH)).toFile();
        if (!blob.exists()){
            return null;
        }
        return Utils.readContents(blob);
    }
    /**
     * Read the blob represented by the given sha1
     * @param sha1Abbr sha1 of the blob
     * @param directoryPath the directory that contains the blob
     * @return the content,null if not found
     */
    private byte[] readBlobWithAbbr(String sha1Abbr,Path directoryPath){
        Path blobDir=this.joinPath(directoryPath,sha1Abbr.substring(0,SHA1_DIR_NAME_LENGTH));

        String[] blobNames=this.namesIn(blobDir);
        if (blobNames==null){
            return null;
        }
        String sha1Prefix=sha1Abbr.substring(SHA1_DIR_NAME_LENGTH);
        //this iteration won't take so long :P
        for (String blobName : blobNames) {
            if (blobName.startsWith(sha1Prefix)) {
                File blob = joinPath(blobDir, blobName).toFile();
                return Utils.readContents(blob);
            }
        }
        return null;
    }
}
