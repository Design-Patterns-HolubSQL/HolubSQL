package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.holub.text.ParseFailure;

import java.io.*;

class DistinctBuilderTest {
	Database database;

	public void setUp() throws IOException {
		database = new Database(new File("Dbase"));
	}
	
	@Test
	public void test_distinct_builder_if_not_use_distinct_operation_should_return_duplicated_rows() throws IOException, ParseFailure {
		// Arrange
		setUp();        
        String sqlQuery = "select last from name";
        String queryExpected = "<anonymous>\n" +
                "last\t\n" +
                "----------------------------------------\n" +
                "Flintstone	\n" +
                "Flintstone	\n" +
                "Holub	\n";
        
		// Act
        Table result = database.execute(sqlQuery);
        
		// Assert	
        assertEquals(queryExpected, result.toString());
	}
	
	@Test
	public void test_distinct_builder_should_accept_distinct_operation() throws IOException, ParseFailure {
		// Arrange
		setUp();        
        String sqlQuery = "select distinct last from name";
        String queryExpected = "<anonymous>\n" +
                "last\t\n" +
                "----------------------------------------\n" +
                "Flintstone	\n" +
                "Holub	\n";
        
		// Act
        Table result = database.execute(sqlQuery);
        
		// Assert	
        assertEquals(queryExpected, result.toString());
	}

}
