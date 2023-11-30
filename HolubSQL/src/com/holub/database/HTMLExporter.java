package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public abstract class HtmlExporter implements Table.Exporter {
    private final Writer out;

    private String tableName;
    private int width;
    private int height;

    public HtmlExporter(Writer out) {
        this.out = out;
    }

    public void startTable() throws IOException {
        out.write("<html>\n");
        out.write("<body>\n");
    }

    public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
        this.tableName = tableName == null ? "anonymous" : tableName;
        this.width = width;
        this.height = height;

        out.write(String.format("\t<h1>%s</h1>\n", this.tableName));
        out.write("\t<table>\n");
        out.write("\t\t<tr>\n");

        while (columnNames.hasNext()) {
            Object columnName = columnNames.next();
            out.write(String.format("\t\t\t<th>%s</th>\n", columnName));
        }

        out.write("\t\t</tr>\n");
    }

    public void storeRow(Iterator data) throws IOException {
        out.write("\t\t<tr>\n");

        while (data.hasNext()) {
            Object datum = data.next();
            out.write(String.format("\t\t\t<td>%s</td>\n", datum));
        }

        out.write("\t\t</tr>\n");
    }

    public void endTable() throws IOException {
        out.write("\t</table>\n");
        out.write("</body>\n");
        out.write("</html>");
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this, null, null, height, width);
    }

    // Add methods from the htmlexporter class here if available
}