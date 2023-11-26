package com.holub.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("연결")
class HTMLExporterTest {
    Table table;

    @org.junit.jupiter.api.BeforeAll
    void loadDummyData(){
        try {
            Reader reader = new FileReader("C:\\DP2023\\Holup\\Dbase\\address.csv");
            Table.Importer importer = new CSVImporter(reader);
            table = new ConcreteTable(importer);
        }catch(Exception e){
            fail(e);
        }
    }



    @org.junit.jupiter.api.Test
    void HTMLExport()
    {
        Writer writer = new StringWriter();
        String answer = "<table><th>addrId</td><th>street</td><th>city</td><th>state</td><th>zip</td><tr><td>0</td><td>12 MyStreet</td><td>Berkeley</td><td>CA</td><td>99998</td></tr><tr><td>1</td><td>34 Quarry Ln.</td><td>Bedrock</td><td>AZ</td><td>00000</td></tr></table>";
        HTMLExporter htmlExporter = new HTMLExporter(writer);
        try{
            table.export(htmlExporter);
            System.out.println(writer.toString());
            assertEquals(answer, writer.toString());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                writer.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}