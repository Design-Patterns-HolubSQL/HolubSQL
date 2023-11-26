package com.holub.database;

import sun.util.BuddhistCalendar;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XMLImporterTest {

    ConcreteTable createDummy() {
        File testFile = new File("Holup\\Dbase\\address.xml");
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

    @org.junit.jupiter.api.Test
    void XMLImport() throws IOException {
        try {
            String testFile = "Holup\\Dbase\\address.xml";
            XMLImporter importer = new XMLImporter(testFile);

            importer.startTable();

            assertEquals(importer.loadTableName(), "address");
            assertEquals(importer.loadWidth(), 5);

            Iterator it = importer.loadColumnNames();
            String[] columnNames = {"addrId", "street", "city", "state", "zip"};
            for(int i = 0; i < columnNames.length; i++){
                if(it.hasNext()){
                    assertEquals(it.next(), columnNames[i]);
                }
                else
                    assertNotNull(it.next());
            }
            String[][] row = {{"0", "12 MyStreet", "Berkeley", "CA", "99998"},
                    {"1", "34 Quarry Ln.", "Bedrock", "AZ", "00000"}};
            for(int i = 0; i < 2; i++) {
                Iterator curRow = importer.loadRow();
                if (it != null) {
                    for(int j = 0; j < 5; j++){
                        if(curRow.hasNext()){
                             assertEquals(curRow.next(), row[i][j]);
                        }
                        else
                            assertNotNull(curRow.next());
                    }

                } else{
                    assertNotNull(it);
                }
            }
            System.out.println(new ConcreteTable(importer).toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}