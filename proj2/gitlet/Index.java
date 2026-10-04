package gitlet;

import java.io.File;
import java.util.TreeMap;

/**
 * Represents Stage Area, provides methods to manipulate the stage area.
 */
public class Index {

    private final File indexFile;
    /**
     * The repository that holds this index.
     */
    private Repository repository;
    /**
     * A mapping from file name to sha1.
     * sha1==null represents for deletion.
     */
    private TreeMap<String,String> map;

    public Index(File index,Repository repo){
        this.repository=repo;
        this.indexFile=index;
        retrieveIndex();
    }

    /**
     * Add a file to stage area according to the given commit.
     * If "stage for deletion" ,the param "file.exist()"should be false.
     * @param file file (absolute) path
     * @param fileName file name,should be consistent with the name in file
     * @param sha1InCommit the sha1 of the file in the commit,
     *                     empty if the file isn't tracked by the commit
     */
    public void add(File file,String fileName,String sha1InCommit){
        boolean maybeDeletion=!file.exists();
        //if (deletion or not exists)
        if (maybeDeletion){
            //if(the file is being tracked)
            if (sha1InCommit !=null){
                //stage deletion
                this.map.put(fileName,"");
                return;
            }
            else {
                return;
            }
        }

        // not deletion
        //if(file not exists in index)
        if (!map.containsKey(fileName)){
            stageFile(file);
            return;
        }

        String stagedSha1=map.get(fileName);
        byte[] serializedFile=Utils.serialize(file);
        String fileSha1=Utils.sha1((Object) serializedFile);

        //if (file exists in index && equal to the index)
        if (stagedSha1.equals(fileSha1)){
            return;
        }
        // if(file sha1 equal commit sha1) remove it
        if (fileSha1.equals(sha1InCommit)){
            remove(fileName);
            return;
        }
        //overwrite it
        stageFile(file,serializedFile);
    }

    /**
     * Remove staged file from stage area and returns its sha1
     * @param fileName file name of the staged file
     * @return sha1 of the blob
     */
    public String remove(String fileName){
        if (!map.containsKey(fileName)){
            return null;
        }
        String sha1= map.remove(fileName);
        File removedFile=repository.getObjectsDirectory();
        removedFile=Utils.join(removedFile,sha1);
        repository.removeFile(removedFile);//just delete it immediately for simplicity
        return sha1;
    }

    private void retrieveIndex(){
        if (!this.indexFile.exists()){
            this.map=new TreeMap<>();
            return;
        }
        map=Utils.readObject(indexFile,TreeMap.class);
    }

    /**
     * Store the given file in the repoitory and update the mapping
     * @param file
     */
    private void stageFile(File file){

    }
    /**
     * Store the given file in the repoitory and update the mapping
     * @param file
     */
    private void stageFile(File file,byte[] blob){

    }
}
