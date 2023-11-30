package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

public class XMLExporter implements Table.Exporter {
    private final ArrayList<String> columnNames = new ArrayList<String>();
    private final Writer out;
    private String tableName;

    public XMLExporter( Writer out ) {
        this.out = out;
    }
    public void startTable() throws IOException {
        out.write("<?xml version=\"1.0\"?>\n");
    }
    public void storeMetadata( String tableName, int width, int height, Iterator columnNames ) throws IOException {
        this.tableName = tableName == null ? "anonymous" : tableName;
        out.write("<" + tableName + ">\n");
        out.write("<columns>");
        while(columnNames.hasNext()){
            String columnName = columnNames.next().toString();
            this.columnNames.add(columnName);
            out.write(String.format("<item>%s</item>", columnName));
        }
        out.write("</columns>\n");
    }

    public void storeRow( Iterator data ) throws IOException {
        out.write("<item>");

        int i = 0;
        while(data.hasNext()) {
            Object datum = data.next();
            Object columnName = columnNames.get(i);
            out.write(String.format("<%s>%s</%s>", columnName, datum, columnName));
            i++;
        }
        out.write("</item>\n");
    }
    public void endTable()   throws IOException {
        out.write("</" + tableName + ">\n");
    }
}