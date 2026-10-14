package gitlet;


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

    /**
     * The .gitlet directory.
     * */
    private final Path gitletDirectory;
    /**
     * The directory that user store his files.
     */
    private final Path workingDirectory;
    /**
     * The directory objects/ that stores all objects, including blobs and commits.
     */
    private final Path objectsDirectory;
    /**
     * The directory objects/commits that stores all commits.
     * (in order to make log command implementation easier,separate the commits from the blobs)
     */
    private final Path commitsDirectory;

    public RepositoryIO(Path workingDirectory){
        this.workingDirectory=workingDirectory;
        //just hard code it...
        this.gitletDirectory=joinPath(workingDirectory,".gitlet");
        this.objectsDirectory=joinPath(gitletDirectory,"objects");
        this.commitsDirectory=joinPath(objectsDirectory,"commits");
    }

    public void initRepository(){
        mkdir(gitletDirectory);
        mkdir(objectsDirectory);
        mkdir(commitsDirectory);
    }

    /*File and Directory*/
    public Path joinPath(String first,String... others){
        return Path.of(first,others);
    }
    public Path joinPath(Path first,String... others){
        return Path.of(first.toString(),others);
    }

    /**
     * Does the file/directory associated with the path exist?
     * @param path path
     * @return true if exists.
     */
    public boolean exists(Path path){
        return path.toFile().exists();
    }
    /**
     * Does the file in the working directory exist?
     * @param fileName file name
     * @return true if exists.
     */
    public boolean exists(String fileName){
        return this.exists(joinPath(workingDirectory,fileName));
    }

    /**
     * Determines whether the gitlet repository exists.
     * @return true if existing.
     */
    public boolean existsRepository(){
        return this.gitletDirectory.toFile().exists();
    }

    /**
     * Get sorted file names of the directory
     * @param directory directory that contains files
     * @return sorted array of file names
     */
    public String[] plainFileNamesIn(Path directory){
        return Utils.plainFilenamesIn(directory.toFile()).toArray(new String[0]);
    }

    /**
     * Get all files/directories names in the given directory
     * @param directory directory path,shouldn't be a file path
     * @return files/directories names in random order
     */
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
    //java disallow multiple return values,so I have to make this due to my immature architecture
    public String fullSha1OfCommit(String sha1Abbr){
        if (sha1Abbr.length()<ABBREVIATED_SHA1_LENGTH){
            return null;
        }
        String resultSha1=sha1Abbr.substring(0,SHA1_DIR_NAME_LENGTH);
        Path blobDir=this.joinPath(commitsDirectory,resultSha1);

        String[] blobNames=this.namesIn(blobDir);
        if (blobNames==null){
            return null;
        }
        String sha1Prefix=sha1Abbr.substring(SHA1_DIR_NAME_LENGTH);
        //this iteration won't take so long :P
        for (String blobName : blobNames) {
            if (blobName.startsWith(sha1Prefix)) {
                resultSha1=resultSha1+blobName;
                return resultSha1;
            }
        }
        return null;
    }

    /*read/write/delete*/

    /**
     * Write the content to the given file.
     * @param content content as byte array
     * @param dstFilePath file path
     */
    public void writeToFile(byte[] content,Path dstFilePath){
        Utils.writeContents(dstFilePath.toFile(), (Object) content);
    }
    /**
     * Write the content to the given file.
     * @param content content as byte array or String
     * @param dstFilePath file path
     */
    public void writeToFile(Path dstFilePath, Object... content){
        Utils.writeContents(dstFilePath.toFile(),content);
    }

    /**
     * Read content from file in the working directory,
     * and save it as a blob in the repository.
     * @param fileName file name in the working directory.
     * @return sha1 of the blob
     */
    public String writeBlob(String fileName){
        Path filePath=joinPath(workingDirectory,fileName);
        return writeBlob(read(filePath));
    }
    /**
     * Save the content as a blob in the repository.
     * @param src content as byte array
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
        return Utils.readContents(src.toFile());
    }

    /**
     * Read content from file in the working directory
     * @param fileName file name in the working directory
     * @return content as byte array
     */
    public byte[] read(String fileName){
        return this.read(joinPath(this.workingDirectory,fileName));
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
     * Remove a blob from the repository
     * @param sha1 full sha1 of the blob
     * @return true if the blob is deleted
     */ //NOTE : deleting blob shouldn't be used in gitlet.
    @Deprecated
    public boolean deleteBlob(String sha1){
        Path blobDir=this.joinPath(objectsDirectory,sha1.substring(0,SHA1_DIR_NAME_LENGTH));
        Path blob=this.joinPath(blobDir,sha1.substring(SHA1_DIR_NAME_LENGTH));
        boolean succeed= this.deleteFile(blob);
        deleteDirectory(blobDir);
        return succeed;
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

    /**
     * Get commit iterator that iterates through all commits in random order
     * @return commit iterator
     */
    public CommitIterator commitIterator(){
        return new CommitIterator(commitsDirectory,this);
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
     * convert string to byte array so that it can be written/read by the repository io
     * @param string string
     * @return string as byte array
     */
    public byte[] stringToByteArray(String string){
        return string.getBytes(StandardCharsets.UTF_8);
    }
    /**
     * convert byte array to string so that it can be written/read by the repository io
     * @param array byte array
     * @return string represented by byte array
     */
    public String byteArrayToString(byte[] array){
        return new String(array,StandardCharsets.UTF_8);
    }

    /**
     * Write the content to the repository,store it as a loosen object
     * (a file in a directory)
     * @param content content as byte array
     * @param directoryPath directory path
     * @return sha1 of the content
     */
    private String writeBlob(byte[] content,Path directoryPath){
        String sha1=sha1(content);
        Path blobDir=this.joinPath(directoryPath,sha1.substring(0,SHA1_DIR_NAME_LENGTH));
        if(!exists(blobDir)){
            this.mkdir(blobDir);
        }
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
        if (sha1Abbr.length()<ABBREVIATED_SHA1_LENGTH){
            return null;
        }
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
