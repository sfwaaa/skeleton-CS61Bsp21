package gitlet;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Provide method to read/write files,
 * hide the details of interacting with file system,
 * and AVOID using Utils.java everywhere.
 * It also manages how blobs and commits is stored.
 */
public class RepositoryIO {

    /*Path*/
    public Path joinPath(String first,String... others){
        return null;
    }
    public Path joinPath(Path first,String... others){
        return null;
    }
    public Commit[] getAllCommit(){
        return null;
    }
    public String[] plainFileNamesIn(Path directory){
        return null;
    }

    /*sha1*/
    public String sha1(Object... objs){
        return null;
    }
    public String abbreviateSha1(String sha1){
        return null;
    }


    /*read/write/delete*/
    public String writeBlob(Path srcFilePath, Path dstDirectoryPath){
        return null;//return sha1
    }
    public String writeBlob(byte[] src, Path dstDirectoryPath){
        return null;//return sha1
    }

    public byte[] read(Path src){
        return null;//return fileContent
    }
    public byte[] read(String sha1){
        return null;//return fileContent
    }
    public byte[] readWithAbbr(String sha1Abbr){
        return null;//return fileContent
    }

    public boolean delete(Path filePath){
        return false;
    }


    /*Conversion*/

    public String deserialize(byte[] content){

        return null;
    }
    public <T extends Serializable> T deserialize(byte[] content,Class<T> expectedClass){
        return null;
    }
    public byte[] serialize(Serializable obj){
        return null;
    }
}
