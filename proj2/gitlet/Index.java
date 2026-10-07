package gitlet;

import java.io.File;
import java.util.TreeMap;

/**
 * Represents Stage Area, provides methods to manipulate the stage area.
 */
public class Index {

    /**
     * The path ./gitlet/index
     */
    private final File indexFile;
    /**
     * The repository that holds this index.
     */
    private final Repository repository;
    /**
     * A mapping from file name to sha1.
     * sha1=="" (empty string) represents for deletion.
     * It is really really very much simpler than the real .git/index
     */
    private TreeMap<String,String> map;

    public Index(File index,Repository repo){
        this.repository=repo;
        this.indexFile=index;
        retrieveIndex();
    }

    /**
     * Add a file to stage area according to the given commit.
     * If "staging for deletion" ,the param "file.exist()"should be false.
     * @param file file path in the working directory
     * @param fileName file name,should be consistent with the name in file
     * @param sha1InCommit the sha1 of the file in the commit,
     *                     empty string if the file isn't tracked by the commit
     */
    public void add(File file,String fileName,String sha1InCommit){
        boolean maybeDeletion=!file.exists();
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
        byte[] fileContent=Utils.readContents(file);
        String fileSha1=Utils.sha1((Object) fileContent);

        // if(file sha1 equal commit sha1) remove it
        if (fileSha1.equals(sha1InCommit)){
            remove(fileName);
            return;
        }
        //if(file not exists in index)
        if (!map.containsKey(fileName)){
           stageFile(fileName,fileSha1,fileContent);
            return;
        }

        String stagedSha1=map.get(fileName);
        //if (file exists in index && equal to the index)
        if (stagedSha1.equals(fileSha1)){
            return;
        }
        //overwrite it
        stageFile(fileName,fileSha1,fileContent);
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
        this.map=null;
        this.repository.removeFile(indexFile);
    }

    private void retrieveIndex(){
        if (!this.indexFile.exists()){
            this.map=new TreeMap<>();
            return;
        }
        map=Utils.readObject(indexFile,TreeMap.class);
    }

    /**
     * Save current Index to the repo.
     * Call it after any modification to the index.
     */
    private void saveIndex(){
        byte[] content= Utils.serialize(this.map);
        repository.writeFile(this.indexFile,content);
    }

    /**
     * Stage the given file in the working directory and
     * update the mapping of the index
     * @param file file path
     */
    /*
    private void stageFile(File file,String fileName){
        if (file==null||!file.exists()){
            return;
        }
        byte[] content=Utils.readContents(file);
        String sha1=Utils.sha1(content);
        this.stageFile(fileName,sha1,content);
    }*/

    /**
     * Stage the given blob of the file in the working directory and
     * update the mapping of the index
     * @param fileName file name
     * @param sha1 sha1 of the file
     * @param blob the serialized file content
     */
    private void stageFile(String fileName,String sha1,byte[] blob){
        File blobPath=Utils.join(repository.getObjectsDirectory(),sha1);
        this.repository.writeFile(blobPath,blob);

        String previousSha1= map.put(fileName,sha1);
        saveIndex();

        if (previousSha1==null|| previousSha1.isEmpty()){
            return;
        }
        File discardBlob=Utils.join
                (repository.getObjectsDirectory(),previousSha1);
        this.repository.removeFile(discardBlob);
    }

    /**
     * Remove the staged file from stage area ,delete the responding blob and
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

        if (sha1.isBlank()){
            return sha1;
        }
        File removedFile=repository.getObjectsDirectory();
        removedFile=Utils.join(removedFile,sha1);
        repository.removeFile(removedFile);//just delete it immediately for simplicity

        return sha1;
    }
}
