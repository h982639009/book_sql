package com.example.books.sql;

public class SQLUtils {
	
	public static String dbName="db_book.db";
	public static String[] tableNames={"table_book","table_printer"};
	//两张表，一张表是书表，包括标题、作者、出版年份、出版机构、页数等属性（主码为标题，出版机构为外码）
	//另一张表是出版机构表，包括出版机构、出版地等属性（主码为出版机构）
	public static String tableName_book="table_book";
	public static String table_book_title="title";
	public static String table_book_author="author";
	public static String table_book_year="print_year";
	public static String table_book_printer="printer";
	public static String table_book_pageNo="pageNo";
	public static String table_book_column[]={"title",
			                           "author",
			                           "print_year",
			                           "printer",
			                           "pageNo"};
	
	public static String tableName_printer="table_printer";
	public static String table_printer_printer="printer";
	public static String table_printer_location="location";
	public static String table_printer_column[]={
            "printer",
            "location"};
	
	
	public static String quotationed(String string){
		return "'"+string+"'";
	}
	public static String quotationed(String string,int searchMode){
		if(searchMode==SQLUtils.SEARCH_EXACT){
			return "'"+string+"'";
		}else{
			return "'%"+string+"%'";
		}
	}
	
	public static int SEARCH_EXACT=1;
	public static int SEARCH_FUZZY=2;
}
