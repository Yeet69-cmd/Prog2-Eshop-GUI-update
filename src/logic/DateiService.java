package logic;

import java.io.*;
public class DateiService {

    public static void speichern(Object objekt, String dateiname) throws IOException {

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dateiname))) {
            out.writeObject(objekt);
        }
    }
    public static Object laden(String dateiname) throws IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(dateiname));
        Object objekt = in.readObject();
        in.close();

        return objekt;
    }
}
