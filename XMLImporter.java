/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */


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

/***
 *	Pass this importer to a {@link Table} constructor (such
 *	as
 *	{link com.holub.database.ConcreteTable#ConcreteTable(Table.Importer)}
 *	to initialize
 *	a <code>Table</code> from
 *	a comma-sparated-value repressentation. For example:
 *	<PRE>
 *	Reader in = new FileReader( "people.csv" );
 *	people = new ConcreteTable( new CSVImporter(in) );
 *	in.close();
 *	</PRE>
 *	The input file for a table called "name" with
 *	columns "first," "last," and "addrId" would look
 *	like this:
 *	<PRE>
 *	name
 *	first,	last,	addrId
 *	Fred,	Flintstone,	1
 *	Wilma,	Flintstone,	1
 *	Allen,	Holub,	0
 *	</PRE>
 *	The first line is the table name, the second line
 *	identifies the columns, and the subsequent lines define
 *	the rows.
 *
 * @include /etc/license.txt
 *
 * @see Table
 * @see Table.Importer
 * @see CSVExporter
 */

public class XMLImporter implements Table.Importer
{
	private Document xml;
	private String[]        columnNames;
	private String          tableName;
	private Iterator<Node>   	it;

	public XMLImporter(String file)
	{
		InputSource is = null;
		try {
			is = new InputSource(new FileReader(file));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			this.xml = dbf.newDocumentBuilder().parse(is);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}

	}

	public List<Node> removeWhitespace(NodeList list){
		List<Node> retList = new ArrayList<>();
		for(int i = 0; i < list.getLength(); i++){
			if(list.item(i).getNodeName().equals("#text") == false){
				retList.add(list.item(i));
			}
		}
		return retList;
	}
	public void startTable()			throws IOException
	{
		Element element = this.xml.getDocumentElement();
		this.tableName = element.getNodeName();
		NodeList list = element.getChildNodes();
		List<Node> rows = removeWhitespace(list);
		this.it = rows.iterator();
		if(rows.size() > 0){
			Node firstRow = rows.get(0);
			List<Node> columns = removeWhitespace(firstRow.getChildNodes());
			this.columnNames = new String[columns.size()];
			for(int i = 0; i < columns.size(); i++){
				this.columnNames[i] = columns.get(i).getNodeName();
			}
		}
	}
	public String loadTableName()		throws IOException
	{	return tableName;
	}
	public int loadWidth()			    throws IOException
	{	return columnNames.length;
	}
	public Iterator loadColumnNames()	throws IOException
	{	return new ArrayIterator(columnNames);  //{=CSVImporter.ArrayIteratorCall}
	}

	public Iterator loadRow()			throws IOException {
		if(it.hasNext()){
			List<Node> rowNodes = removeWhitespace(it.next().getChildNodes());
			List<String> row = new ArrayList<>();
			for (Node rowNode : rowNodes) {
				row.add(rowNode.getTextContent());
			}
			return row.iterator();
		}
		return null;
	}

	public void endTable() throws IOException {}

	private Iterable<Node> nodeListToIterator(final NodeList nodeList) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {

					private int pos = 0;

					@Override
					public boolean hasNext() {
						return nodeList.getLength() > pos;
					}

					@Override
					public Node next() {
						return nodeList.item(pos++);
					}

					@Override
					public void remove() {
						throw new IllegalStateException();
					}
				};
			}

		};
	}
}