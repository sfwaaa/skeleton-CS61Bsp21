package byow.Persistent;

import byow.World.Vector2Int;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

//simple persistent data
public class GamePrefs {
    private final File SAVED_WORLD= Path.of(System.getProperty("user.dir"),"savedWorld.txt").toFile();
    //some code from proj2 LOL
    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    private static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }
    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    private static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }
    /** Write OBJ to FILE. */
    private static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }
    /** Returns a byte array containing the serialized contents of OBJ. */
    private static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            //excp.printStackTrace();
            return new byte[0];
        }
    }

    public static class WorldData implements Serializable{
        public Long seed;
        public Integer playerX;
        public Integer playerY;
        public WorldData(long seed,Vector2Int playerPosition){
            this.seed=seed;
            playerX=playerPosition.x;
            playerY=playerPosition.y;
        }
    }

    private WorldData savedData=null;

    public boolean hasSavedData(){
        return SAVED_WORLD.exists();
    }
    public void save(long seed, Vector2Int playerPosition){
       WorldData data=new WorldData(seed,playerPosition);
       writeObject(SAVED_WORLD,data);
    }
    public long loadSeed(){
        if (savedData==null){
            savedData=readObject(SAVED_WORLD,WorldData.class);
        }
        return savedData.seed;
    }
    public Vector2Int loadPlayerPosition(){
        if (savedData==null){
            savedData=readObject(SAVED_WORLD,WorldData.class);
        }
        return new Vector2Int(savedData.playerX,savedData.playerY);
    }
}
