

import java.io.*;
import java.util.*;

import com.holub.database.Cursor;
import com.holub.database.Selector;

/** A table is a database-like table that provides support for
 *  queries.
 *
 * @include /etc/license.txt
 */

public interface Table extends Serializable, Cloneable
{
	/** Return a shallow copy of the table (the contents are not
	 *  copied.
	 */
	Object clone() throws CloneNotSupportedException;

	/** Return the table name that was passed to the constructor
	 *  (or read from the disk in the case of a table that
	 *  was loaded from the disk.) THis is a "getter," but
	 *  it's a harmless one since it's just giving back a
	 *  piece of information that it was given.
	 */
	String  name();

	/** Rename the table to the indicated name. This method
	 *  can also be used for naming the anonymous table that's
	 *  returned from {@link #select select(...)}
	 *  or one of its variants.
	 */
	void rename( String newName );

	
	boolean isDirty();

	/** Insert new values into the table corresponding to the
	 *  specified column names. For example, the value at
	 *  <code>values[i]</code> is put into the column specified
	 *  in <code>columnNames[i]</code>. Columns that are not
	 *  specified are initialized to <code>null</code>.
	 *
	 * @return the number of rows affected by the operation.
	 * @throws IndexOutOfBoundsException One of the requested columns
	 * 				doesn't exist in either table.
	 */
	int  insert( String[] columnNames, Object[] values );

	/** A convenience overload of {@link #insert(String[],Object[])} */

	int  insert( Collection columnNames, Collection values );

	/** In this version of insert, values must have as many elements as there
	 *  are columns, and the values must be in the order specified when the
	 *  Table was created.
	 * @return the number of rows affected by the operation.
	 */
	int  insert( Object[]	values );

	/** A convenience overload of {@link #insert(Object[])}
	 */

	int  insert( Collection values );

	/**
	 * Update cells in the table. The {@link Selector} object serves
	 * as a visitor whose <code>includeInSelect(...)</code> method
	 * is called for each row in the table. The return value is ignored,
	 * but the Selector can modify cells as it examines them. Its your
	 * responsibility not to modify primary-key and other constant
	 * fields.
	 * @return the number of rows affected by the operation.
	 */

	int  update( Selector where );

	/** Delete from the table all rows approved by the Selector.
	 * @return the number of rows affected by the operation.
	 */

	int  delete( Selector where );

	/** begin a transaction */
	public void begin();

	/** Commit a transaction.
	 *  @throws IllegalStateException if no {@link #begin} was issued.
	 *
	 *  @param all if false, commit only the innermost transaction,
	 *  		otherwise commit all transactions at all levels.
	 *  @see #THIS_LEVEL
	 *  @see #ALL
	 */
	public void commit( boolean all ) throws IllegalStateException;

	/** Roll back a transaction.
	 *  @throws IllegalStateException if no {@link #begin} was issued.
	 *  @param all if false, commit only the innermost transaction,
	 *  		otherwise commit all transactions at all levels.
	 *  @see #THIS_LEVEL
	 *  @see #ALL
	 */
	public void rollback( boolean all ) throws IllegalStateException;

	/** A convenience constant that makes calls to {@link #commit}
	
	 *  Use <code>commit(Table.THIS_LEVEL)</code> rather than
	 *  <code>commit(false)</code>, for example.
	 */
	public static final boolean THIS_LEVEL = false;

	/** A convenience constant that makes calls to {@link #commit}
	 *  and {@link  more readable when used as an
	 *  argument to those methods.
	 *  Use <code>commit(Table.ALL)</code> rather than
	 *  <code>commit(true)</code>, for example.
	 */
	public static final boolean ALL	= true;

	/*** **********************************************************************
	 *  Create an unmodifiable table that contains selected rows
	 *  from the current table. The {@link Selector} argument
	 *  specifies a strategy object that  determines which
	 *  rows will be included in the result.
	 *  <code>Table</code>.
	 *
	 *  If the <code>other</code> argument is present, this methods
	 * "joins" all rows
	 *  from the current table and the <code>other</code> table and
	 *  then selects rows from the "join."
	 *  If the two tables contain identically named columns, then
	 *  only the column from the current table is included in the
	 *  result.
	 * 	<p>
	 *  Joins are performed by creating the Cartesian product of the current
	 *  and "other" tables, using the Selector to determine which rows
	 *  of the product to include in the returned Table. For example,
	 *  If one table contains:
	 *  <pre>
	 *  a b
	 *  c d
	 *  </pre>
	 *  and the <code>other</code> table contains
	 *  <pre>
	 *  e f
	 *  g h
	 *  </pre>
	 *  then the Cartesian product is the table
	 *  <pre>
	 *  a b e f
	 *  a b g h
	 *  c d e f
	 *  c d g h
	 *  </pre>
	 *  In the case of a join, the selector is presented with rows from
	 *  this product.
	 *  <p>
	 *  The <code>Table</code> returned from {@link #select}
	 *  cannot be modified by you. The methods <code>Table</code>
	 *  methods that normally modify the
	 *  table (insert, update, delete, store) throw an
	 *  {@link UnsupportedOperationException} if call them.
	 *
	 * @param  where a selector that determines which rows to include
	 * 			in the result.
	 *			Use {@link Selector#ALL} to include all rows.
	 * @param  requestedColumns columns to include in the result.
	 * 			null for all columns.
	 * @param  other Other tables to join to this one. At most
	 * 			three other tables may be specified.
	 * 			This argument must be null if you're not doing a join.
	 * @throws IndexOutOfBoundsException One of the requested columns
	 * 				doesn't exist in either table.
	 *
	 * @return a Table that holds those rows from the Cartesian
	 * 		product of this table and the <code>other</code> table
	 * 		that were accepted by the {@link Selector}.
	 */

	Table select(Selector where, String[] requestedColumns, Table[] other);

	/** A more efficient version of
	 * <code>select(where, requestedColumns, null);</code>
	 */
	Table select(Selector where, String[] requestedColumns );

	/** A more efficient version of <code>select(where, null, null);</code>
	 */
	Table select(Selector where);

	/** A convenience method that translates Collections to arrays, then
	 *  calls {@link #select(Selector,String[],Table[])};
	 *  @param requestedColumns a collection of String objects
	 *  			representing the desired columns.
	 *	@param other a collection of additional Table objects to join to
	 *				the current one for the purposes of this SELECT
	 *				operation.
	 */
	Table select(Selector where, Collection requestedColumns,
												Collection other);

	/** Convenience method, translates Collection to String array, then
	 *  calls String-array version.
	 */
	Table select(Selector where, Collection requestedColumns );

	/** Return an iterator across the rows of the current table.
	 */
	Cursor rows();

	/** Build a representation of the Table using the
	 *  specified Exporter. Create an object from an
	 *  {@link Table.Importer} using the constructor with an
	 *  {@link Table.Importer} argument. The table's
	 *  "dirty" status is cleared (set false) on an export.
	 *  @see #isDirty
	 */
	void export( Table.Exporter importer ) throws IOException;

	/******************************************************************* 
	 * Used for exporting tables in various formats. Note that
	 *  I can add methods to this interface if the representation
	 *  requires it without impacting the Table's clients at all.
	 */
	public interface Exporter				//{=Table.Exporter}
	{	public void startTable()			throws IOException;
		public void storeMetadata(
					String tableName,
					int width,
					int height,
					Iterator columnNames )	throws IOException;
		public void storeRow(Iterator data) throws IOException;
		public void endTable()			 	throws IOException;
	}

	/******************************************************************* 
	 *  Used for importing tables in various formats.
	 *  Methods are called in the following order:
	 *  <ul>
	 *	<li><code>start()</code></li>
	 *	<li><code>loadTableName()</code></li>
	 *	<li><code>loadWidth()</code></li>
	 *	<li><code>loadColumnNames()</code></li>
	 *	<li><code>loadRow()</code> (multiple times)</li>
	 *	<li><code>done()</code></li>
	 *	</ul>
	 */
	public interface Importer				//{=Table.Importer}
	{	void 	 startTable()		throws IOException;
		String   loadTableName()	throws IOException;
		int 	 loadWidth()		throws IOException;
		Iterator loadColumnNames()	throws IOException;
		Iterator loadRow()			throws IOException;
		void 	 endTable()			throws IOException;
	}
}