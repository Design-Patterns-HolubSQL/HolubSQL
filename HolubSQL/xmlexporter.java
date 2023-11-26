package com.holub.database;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class XMLExporterTest {

    ConcreteTable createDummy() {
        File testFile = new File("Holup\\Dbase\\address.csv");
        try {
            FileReader reader = new FileReader(testFile);
            CSVImporter importer = new CSVImporter(reader);
            ConcreteTable table = new ConcreteTable(importer);
            return table;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void XMLexport() {
        ConcreteTable table = createDummy();
        String ans = "<address>\n" +
                "\t<row>\n" +
                "\t\t<addrId>0</addrId>\n" +
                "\t\t<street>12 MyStreet</street>\n" +
                "\t\t<city>Berkeley</city>\n" +
                "\t\t<state>CA</state>\n" +
                "\t\t<zip>99998</zip>\n" +
                "\t</row>\n" +
                "\t<row>\n" +
                "\t\t<addrId>1</addrId>\n" +
                "\t\t<street>34 Quarry Ln.</street>\n" +
                "\t\t<city>Bedrock</city>\n" +
                "\t\t<state>AZ</state>\n" +
                "\t\t<zip>00000</zip>\n" +
                "\t</row>\n" +
                "</address>";
        try {
            Writer w = new StringWriter();
            XMLExporter exporter = new XMLExporter(w);
            table.export(exporter);
            System.out.println(w.toString());
            assertEquals(w.toString(), ans);
            w.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}'