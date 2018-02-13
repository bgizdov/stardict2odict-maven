package org.odict.stardict2odict;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This is not a runnable app, refer package org.yage.dict.star please!
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Processing dictionary file...");
        StarDict dict = new StarDictParser().parse("/Users/tjnickerson/Desktop/stardict-babylon-Babylon_Chinese_S_English-2.4.2.tar.bz2");;

        System.out.println("Converting to XML (this may take awhile)");
        String xml = new ODictXMLConverter().convert(dict);

        System.out.println("Writing data to file");
        BufferedWriter writer = new BufferedWriter(new FileWriter("dict.xml"));
        writer.write(xml);
        writer.close();
    }
}
