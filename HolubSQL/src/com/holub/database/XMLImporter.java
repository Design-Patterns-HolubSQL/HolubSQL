// 필요한 패키지 및 클래스를 import합니다.
import com.holub.tools.ArrayIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// XMLImporter 클래스는 Table.Importer를 구현하여 XML 데이터를 테이블로 변환하는 역할을 합니다.
public class XMLImporter implements Table.Importer {
    // XML 문서를 나타내는 객체
    private Document xml;
    // 테이블의 열 이름을 저장하는 배열
    private String[] columnNames;
    // 테이블의 이름
    private String tableName;
    // 테이블의 행을 나타내는 노드에 대한 반복자
    private Iterator<Node> rowIterator;

    // 생성자: 파일에서 XML을 읽어와 Document 객체로 파싱합니다.
    public XMLImporter(String file) {
        try (FileReader fileReader = new FileReader(file)) {
            InputSource is = new InputSource(fileReader);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            this.xml = dbf.newDocumentBuilder().parse(is);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            // 에러가 발생하면 RuntimeException으로 감싸서 런타임 예외로 처리합니다.
            throw new RuntimeException(e);
        }
    }

    // removeWhitespace 메서드: NodeList에서 공백을 제거한 노드들을 추출합니다.
    private List<Node> removeWhitespace(NodeList list) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            // 텍스트 노드가 아니거나 내용이 비어 있지 않은 경우만 추가합니다.
            if (!(node.getNodeType() == Node.TEXT_NODE && node.getTextContent().trim().isEmpty())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    // startTable 메서드: 테이블의 시작을 처리합니다.
    public void startTable() throws IOException {
        Element element = this.xml.getDocumentElement();
        this.tableName = element.getNodeName();
        NodeList rows = element.getChildNodes();
        List<Node> nonEmptyRows = removeWhitespace(rows);
        this.rowIterator = nonEmptyRows.iterator();

        if (!nonEmptyRows.isEmpty()) {
            Node firstRow = nonEmptyRows.get(0);
            List<Node> columns = removeWhitespace(firstRow.getChildNodes());
            this.columnNames = new String[columns.size()];
            // 첫 번째 행의 열 이름을 배열에 저장합니다.
            for (int i = 0; i < columns.size(); i++) {
                this.columnNames[i] = columns.get(i).getNodeName();
            }
        }
    }

    // loadTableName 메서드: 테이블의 이름을 반환합니다.
    public String loadTableName() throws IOException {
        return tableName;
    }

    // loadWidth 메서드: 테이블의 폭(열의 개수)을 반환합니다.
    public int loadWidth() throws IOException {
        return columnNames.length;
    }

    // loadColumnNames 메서드: 테이블의 열 이름을 Iterator로 반환합니다.
    public Iterator loadColumnNames() throws IOException {
        return new ArrayIterator(columnNames);
    }

    // loadRow 메서드: 현재 행의 데이터를 반환하는 Iterator를 생성하여 반환합니다.
    public Iterator loadRow() throws IOException {
        if (rowIterator.hasNext()) {
            Node rowNode = rowIterator.next();
            List<Node> cells = removeWhitespace(rowNode.getChildNodes());
            List<String> row = new ArrayList<>();
            // 현재 행의 각 셀의 데이터를 리스트에 저장합니다.
            for (Node cellNode : cells) {
                row.add(cellNode.getTextContent());
            }
            return row.iterator();
        }
        return null;
    }

    // endTable 메서드: 테이블의 끝을 처리합니다. (여기서는 아무 작업도 수행하지 않음)
    public void endTable() throws IOException {
        // 테이블의 끝을 처리하는 메서드입니다. (여기서는 아무 작업도 수행하지 않음)
    }
}
