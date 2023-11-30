package com.holub.database;


import com.holub.text.ParseFailure;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.SQLException;

class ConcreteTableTest extends TestCase {
    Table people = TableFactory.create("name", new String[] { "first", "last", "addrId" });
    Table address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
    Database database;

    private void setData(){
        try{
            people.insert(new Object[]{"firstA", "lastA", 1});
            people.insert(new Object[]{"firstB", "lastB", 1});
            people.insert(new Object[]{"firstC", "lastC", 0});
            people.insert(new Object[]{"firstD", "lastD", 0});

            address.insert(new Object[]{0, "streetA", "cityA", "stateA", "zipA"});
            address.insert(new Object[]{1, "streetB", "cityB", "stateB", "zipB"});

            database = new Database(new File("."));
        } catch (Exception e){}
    }
    @Test
    public void htmlExportTest() throws IOException, SQLException {
        setData();
        String expected = "<html>\n" +
                "<body>\n" +
                "\t<h1>address</h1>\n" +
                "\t<table>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<th>addrId</th>\n" +
                "\t\t\t<th>street</th>\n" +
                "\t\t\t<th>city</th>\n" +
                "\t\t\t<th>state</th>\n" +
                "\t\t\t<th>zip</th>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>0</td>\n" +
                "\t\t\t<td>streetA</td>\n" +
                "\t\t\t<td>cityA</td>\n" +
                "\t\t\t<td>stateA</td>\n" +
                "\t\t\t<td>zipA</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>1</td>\n" +
                "\t\t\t<td>streetB</td>\n" +
                "\t\t\t<td>cityB</td>\n" +
                "\t\t\t<td>stateB</td>\n" +
                "\t\t\t<td>zipB</td>\n" +
                "\t\t</tr>\n" +
                "\t</table>\n" +
                "</body>\n" +
                "</html>";
        StringWriter out = new StringWriter();
        address.export(new HTMLExporter(out));
        assertEquals(out.toString(), expected);
    }
    @Test
    public void xmlExportTest() throws IOException, SQLException {
        setData();
        String expected = "<?xml version=\"1.0\"?>\n" +
                "<address>\n" +
                "<columns><item>addrId</item><item>street</item><item>city" +
                "</item><item>state</item><item>zip</item></columns>\n" +
                "<item><addrId>0</addrId><street>streetA</street><city>cityA" +
                "</city><state>stateA</state><zip>zipA</zip></item>\n" +
                "<item><addrId>1</addrId><street>streetB</street><city>cityB" +
                "</city><state>stateB</state><zip>zipB</zip></item>\n" +
                "</address>\n";
        StringWriter out = new StringWriter();
        address.export(new XMLExporter(out));
        assertEquals(out.toString(), expected);
    }
    @Test
    public void xmlImportTest() throws IOException, SQLException {
        setData();
        StringWriter out = new StringWriter();
        address.export(new XMLExporter(out));

        Table importedTable = new ConcreteTable(new XMLImporter(new StringReader(out.toString())));
        assertEquals(importedTable.toString(), address.toString());
    }
    @Test
    public void selectJoinWithStar() throws IOException, ParseFailure {
        setData();
        String query = " select * from address, name where address.addrId = name.addrId";
        Table result = database.execute(query);
        String expected = "<anonymous>\n" +
                "addrId\tstreet\tcity\tstate\tzip\tfirst\tlast\t\n" +
                "----------------------------------------\n" +
                "0\tstreetA\tcityA\tstateA\tzipA\tfirstC\tlastC\t\n" +
                "0\tstreetA\tcityA\tstateA\tzipA\tfirstD\tlastD\t\n" +
                "1\tstreetB\tcityB\tstateB\tzipB\tfirstA\tlastA\t\n" +
                "1\tstreetB\tcityB\tstateB\tzipB\tfirstB\tlastB\t\n";
        assertEquals(result.toString(), expected);

    }
    @Test
    public void selectWithDistinct() throws IOException, ParseFailure {
        setData();
        String query = "select distinct addrId from name";
        Table result = database.execute(query);
        String expected = "<anonymous>\n" +
                "addrId\t\n" +
                "----------------------------------------\n" +
                "1\t\n" +
                "0\t\n";
        assertEquals(result.toString(), expected);
    }
    @Test
    public void selectWithOrderBy() throws IOException, ParseFailure {
        setData();
        String query = "select * from name order by addrId, last desc";
        Table result = database.execute(query);
        String expected = "<anonymous>\n" +
                "first\tlast\taddrId\t\n" +
                "----------------------------------------\n" +
                "firstD\tlastD\t0\t\n" +
                "firstC\tlastC\t0\t\n" +
                "firstB\tlastB\t1\t\n" +
                "firstA\tlastA\t1\t\n";
        assertEquals(result.toString(), expected);
    }
    @Test
    public void selectFromNestedQuery() throws IOException, ParseFailure {
        setData();
        String query = "select * from (select * from address, name where address.addrId = name.addrId) where addrId=1";
        Table result = database.execute(query);
        String expected = "<anonymous>\n" +
                "addrId\tstreet\tcity\tstate\tzip\tfirst\tlast\t\n" +
                "----------------------------------------\n" +
                "1\tstreetB\tcityB\tstateB\tzipB\tfirstA\tlastA\t\n" +
                "1\tstreetB\tcityB\tstateB\tzipB\tfirstB\tlastB\t\n";
        assertEquals(result.toString(), expected);
    }
}