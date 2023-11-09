package com.holub.database;

public class QueryBuilderDirector {
	private QueryBuilder queryBuilder;
	
	public void setQueryBuilder(QueryBuilder qb) {
		queryBuilder = qb;
	}
	
	public void customizedTable() {
		queryBuilder.build();
	}
}
