package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import com.holub.text.ParseFailure;

import java.io.*;

class ConcreteTableTest {
	Table name;
	Table address;
	Database database;

	public void setUp() throws IOException {
		name = TableFactory.create("name", new String[] { "first", "last", "addrId" });
	    address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
		database = new Database(new File("Dbase"));
	}
	
	@Test
	public void test_select_does_a_join_should_accept_select_star_operation() throws IOException, ParseFailure {
		// Arrange
		setUp();        
        String sqlQuery = "select * from address, name where address.addrId = name.addrId";
        String queryExpected = "<anonymous>\n" +
                "zip\tlast\tcity\tstreet\taddrId\tstate\tfirst\t\n" +
                "----------------------------------------\n" +
                "99998\tHolub\tBerkeley\t12 MyStreet\t0\tCA\tAllen\t\n" +
                "00000\tFlintstone\tBedrock\t34 Quarry Ln.\t1\tAZ\tFred\t\n" +
                "00000\tFlintstone\tBedrock\t34 Quarry Ln.\t1\tAZ\tWilma\t\n";
        
		// Act
        Table result = database.execute(sqlQuery);
        
		// Assert	
        assertEquals(queryExpected, result.toString());
	}

}
