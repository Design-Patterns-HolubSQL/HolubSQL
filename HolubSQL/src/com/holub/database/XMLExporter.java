import java.io.*;
import java.util.*;

public class XMLExporter implements Table.Exporter {
    private final Writer out;
    private List<Object> columnNames;
    private String tableName;

    public XMLExporter(Writer out) {
        this.out = out;
        this.columnNames = new ArrayList<>();
    }

    public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
        this.tableName = (tableName == null) ? "anonymous" : tableName;
        while (columnNames.hasNext()) {
            this.columnNames.add(columnNames.next());
        }
        out.write("<" + this.tableName + ">\n");
    }

    public void storeRow(Iterator data) throws IOException {
        out.write("\t<row>\n");
        Iterator columnNamesIterator = this.columnNames.iterator();
        while (data.hasNext() && columnNamesIterator.hasNext()) {
            Object datum = data.next();
            Object columnName = columnNamesIterator.next();
            if (datum != null) {
                writeTaggedElement(columnName.toString(), datum.toString());
            }
        }
        out.write("\t</row>\n");
    }

    private void writeTaggedElement(String tag, String content) throws IOException {
        out.write("\t\t<" + tag + ">" + content + "</" + tag + ">\n");
    }

    public void startTable() throws IOException { /* nothing to do */ }

    public void endTable() throws IOException {
        out.write("</" + this.tableName + ">");
    }
}
