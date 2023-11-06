package com.holub.database;

import java.util.HashSet;
import java.util.Iterator;

public class DistinctBuilder implements QueryBuilder{	
	Table table;
	boolean isDistinct;
	
    public DistinctBuilder(Table table, boolean isDistinct) {
    	this.table = table;
		this.isDistinct = isDistinct; 	
    }
	
	@Override
    public void build(){
		if (isDistinct) {
			UnmodifiableTable u_t = (UnmodifiableTable)table;
			ConcreteTable t = (ConcreteTable)(u_t.extract());
			HashSet<String> filterRow = new HashSet<>();

			Iterator<Object> iter = t.getRowSet().iterator();
			while(iter.hasNext()){
			    Object row = iter.next();
			    String rowString = ""; 

			    for(Object eachRow: (Object[])row){
			    	rowString += eachRow; 
			    }
			    
			    if(filterRow.contains(rowString)){
			        iter.remove();
			    } 
			    else {
			        filterRow.add(rowString);
			    }
			}
		}
    }
}
