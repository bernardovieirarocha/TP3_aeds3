package Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import Interfaces.Registro;

/**
 * FileManager: Generic class that represents a record file.
 */
public class FileManager<T extends Registro> {
    final int HEADER_SIZE = 4;

    RandomAccessFile file;
    String fileName;
    Constructor<T> constructor;
    HashExtensivel<ParIDEndereco> directIndex;

    public FileManager(String na, Constructor<T> c) throws Exception {
        File d = new File("dados");
        if (!d.exists())
            d.mkdir();

        d = new File("dados/" + na);
        if (!d.exists())
            d.mkdir();

        this.fileName = "dados/" + na + "/" + na + ".db";
        this.constructor = c;
        file = new RandomAccessFile(this.fileName, "rw");
        if (file.length() < HEADER_SIZE) {
            // initialize file, creating its header
            file.writeInt(0); // last ID
            file.writeLong(-1); // list of records marked for deletion
        }

        directIndex = new HashExtensivel<>(
                ParIDEndereco.class.getConstructor(),
                4,
                "dados/" + na + "/" + na + ".d.db", // directory
                "dados/" + na + "/" + na + ".c.db" // buckets
        );
    } // end FileManager ( )

    /**
     * Creates a new record in the file
     * 
     * @param obj object to be inserted
     * @return id of created record
     * @throws Exception
     */
    public int create(T obj) throws Exception {
        file.seek(0);
        int nextID = file.readInt() + 1;
        file.seek(0);
        file.writeInt(nextID);
        obj.setId(nextID);

        file.seek(file.length());
        long address = file.getFilePointer();
        byte[] b = obj.toByteArray();

        file.writeByte(' ');
        file.writeShort(b.length);
        file.write(b);

        directIndex.create(new ParIDEndereco(nextID, address));
        return obj.getId();
    } // end create ( )

    /**
     * Reads a record from the file
     * 
     * @param id id of record to be read
     * @return read object
     * @throws Exception
     */
    public T read(int id) throws Exception {
        T obj;
        short size;
        byte[] b;
        byte tombstone;
        ParIDEndereco pid = directIndex.read(id);
        if (pid != null) {
            file.seek(pid.getEndereco());
            obj = constructor.newInstance();
            tombstone = file.readByte();

            if (tombstone == ' ') {
                size = file.readShort();
                b = new byte[size];
                file.read(b);
                obj.fromByteArray(b);
                if (obj.getId() == id) {
                    return obj;
                } // end if
            } // end if
        } // end while
        // end if
        return null;
    } // end read ( )

    /**
     * Deletes a record from the file
     * 
     * @param id id of record to be deleted
     * @return true if record was deleted, false otherwise
     * @throws Exception
     */
    public boolean delete(int id) throws Exception {
        T obj;
        short size;
        byte[] b;
        byte tombstone;

        ParIDEndereco pie = directIndex.read(id);
        if(pie!=null) {
            file.seek(pie.getEndereco());
            obj = constructor.newInstance();
            tombstone = file.readByte();
            if(tombstone==' ') {
                size = file.readShort();
                b = new byte[size];
                file.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id) {
                    if(directIndex.delete(id)) {
                        file.seek(pie.getEndereco());
                        file.write('*');
                       // addDeleted(size, pie.getEndereco());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Updates a record in the file
     * 
     * @param newObj object to be updated
     * @return true if record was updated, false otherwise
     * @throws Exception
     */
    public boolean update(T newObj) throws Exception {
        boolean result = false;
        T obj;
        short size;
        byte[] b;
        byte tombstone;
        ParIDEndereco pie = directIndex.read(newObj.getId());
        if (pie != null) {
            file.seek(pie.getEndereco());
            obj = constructor.newInstance();
            tombstone = file.readByte();

            if (tombstone == ' ') {
                size = file.readShort();
                b = new byte[size];
                file.read(b);
                obj.fromByteArray(b);

                if (obj.getId() == newObj.getId()) {
                    byte[] b2 = newObj.toByteArray();
                    short size2 = (short) b2.length;

                    if (size2 <= size) { // overwrite record
                        file.seek(pie.getEndereco() + 3);
                        file.write(b2);
                    } else { // move new record to end
                        file.seek(pie.getEndereco());
                        file.write('*');
                        file.seek(file.length());
                        long newAddress = file.getFilePointer();
                        file.writeByte(' ');
                        file.writeShort(size2);
                        file.write(b2);
                        directIndex.update(new ParIDEndereco(newObj.getId(), newAddress));
                    } // end if
                    result = true;
                } // end if
            } // end if
        } // end if
        return result;
    } // end update ( )

    /**
     * Closes the file
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        file.close();
    } // end close ( )

    public int ultimoId() throws IOException {
        file.seek(0);
        int id = file.readInt();
        return id;
    }

} // end class FileManager
