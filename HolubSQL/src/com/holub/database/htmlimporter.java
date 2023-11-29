package com.holub.database;

import java.io.*;
import java.util.Iterator;

public class HTMLExporter implements Table.Exporter {
    private final Writer out;

    public HTMLExporter(Writer out) {
        this.out = out;
    }

    public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
        HTMLTable table = new HTMLTable(out);
        table.startTable();

        while (columnNames.hasNext()) {
            table.addColumn(columnNames.next());
        }
    }

    public void storeRow(Iterator data) throws IOException {
        HTMLTable table = new HTMLTable(out);
        table.startRow();

        while (data.hasNext()) {
            table.addCell(data.next());
        }

        table.endRow();
    }

    public void startTable() throws IOException {
        // Do nothing here; table is started in storeMetadata
    }

    public void endTable() throws IOException {
        // Do nothing here; table is ended implicitly
    }
}

class HTMLTable {
    private final Writer out;

    public HTMLTable(Writer out) {
        this.out = out;
    }

    public void startTable() throws IOException {
        out.write("<table>");
    }

    public void endTable() throws IOException {
        out.write("</table>");
    }

    public void startRow() throws IOException {
        out.write("<tr>");
    }

    public void endRow() throws IOException {
        out.write("</tr>");
    }

    public void addColumn(Object columnName) throws IOException {
        out.write(String.format("<th>%s</th>", formatElement(columnName)));
    }

    public void addCell(Object data) throws IOException {
        out.write(String.format("<td>%s</td>", formatElement(data)));
    }

    private String formatElement(Object element) {
        return (element != null ? element.toString() : "");
    }
}
