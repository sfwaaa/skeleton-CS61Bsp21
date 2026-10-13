package gitlet;

import java.nio.file.Path;
import java.util.TreeMap;

/**
 * Represents Stage Area, provides methods to manipulate the stage area.
 */
public class Index {

    /**
     * The path ./gitlet/index
     */
    private final Path indexPath;
    private final RepositoryIO io;

    /**
     * A mapping from file name to sha1.
     * sha1=="" (empty string) represents for deletion.
     * It is really really very much simpler than the real .git/index
     */
    private TreeMap<String,String> map;

    public Index(Path gitletRepository,RepositoryIO io){
        this.io=io;
        this.indexPath =io.joinPath(gitletRepository,"index");
        retrieveIndex();
    }

    /**
     * Add a file to stage area according to the given commit.
     * If "staging for deletion" ,the param "file.exist()"should be false.
     * @param fileName file name,should be consistent with the name in file
     * @param sha1InCommit the sha1 of the file in the commit,
     *                     empty string if the file isn't tracked by the commit
     */
    public void add(String fileName,String sha1InCommit){
        boolean maybeDeletion=!io.exists(fileName);
        //if (deletion or not exists)
        if (maybeDeletion){
            //if(the file is being tracked)
            if (sha1InCommit !=null){
                //stage deletion
                this.map.put(fileName,"");
                this.saveIndex();
                return;
            }
            else {
                return;
            }
        }

        // not deletion
        //Note that we compute the sha1 of the file content
        //but in real git,.git/index stores much info to quickly tell whether the file is modified.
        byte[] fileContent=io.read(fileName);
        String fileSha1=io.sha1(fileContent);

        // if(file sha1 equal commit sha1) remove it
        if (fileSha1.equals(sha1InCommit)){
            remove(fileName);
            return;
        }
        //if(file not exists in index)
        if (!map.containsKey(fileName)){
           stageFile(fileName);
            return;
        }

        String stagedSha1=map.get(fileName);
        //if (file exists in index && equal to the index)
        if (stagedSha1.equals(fileSha1)){
            return;
        }
        //overwrite it
        stageFile(fileName);
    }

    /**
     * Remove the staged file from the stage area
     * @param fileName name of the file to unstage
     * @return true if the file is staged and removed successfully
     */
    public boolean restore(String fileName){
        return this.remove(fileName)!=null;
    }


    /**
     * Return the difference that the stage area holds
     * @return the shallow clone of the mapping from file name to blobs.
     * Note that a mapping to empty string means deletion.
     */
    public TreeMap<String,String> getStagedContent(){
        return (TreeMap<String, String>) this.map.clone();
    }

    /**
     * clear staged content and remove the index file
     */
    public void clear(){
        this.map=new TreeMap<>();//Although it had better delete the blobs...
        io.deleteFile(indexPath);
    }
    public boolean hasStagedContent(){
        if (this.map==null){
            retrieveIndex();
        }
        return !this.map.isEmpty();
    }

    private void retrieveIndex(){
        if (!io.exists(indexPath)){
            this.map=new TreeMap<>();
            return;
        }
        map=io.deserialize(io.read(indexPath),TreeMap.class);
    }

    /**
     * Save current Index to the repo.
     * Call it after any modification to the index.
     */
    private void saveIndex(){
        byte[] content= io.serialize(this.map);
        io.writeToFile(content,indexPath);
    }

    /**
     * Stage the given blob of the file in the working directory and
     * update the mapping of the index
     * @param fileName file name
     */
    private void stageFile(String fileName){
        String sha1= io.writeBlob(fileName);

        String previousSha1= map.put(fileName,sha1);
        saveIndex();
        /*
        if (previousSha1==null|| previousSha1.isEmpty()){
            return;
        }
        //io.deleteBlob(sha1);
        */

    }

    /**
     * Remove the staged file from stage area ,delete the corresponding blob and
     * returns its sha1.
     * Do nothing if the file not staged
     * @param fileName file name of the staged file
     * @return sha1 of the blob,null if the file is unstaged,empty if staged for deletion.
     */
    private String remove(String fileName){
        if (!map.containsKey(fileName)){
            return null;
        }
        String sha1= map.remove(fileName);
        saveIndex();
        //io.deleteBlob(sha1);//just delete it immediately for simplicity.NO SHOULDN'T DELETE IT
        return sha1;
    }
}
