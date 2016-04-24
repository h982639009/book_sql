package com.example.books.sql;

import java.sql.ResultSet;

import com.example.books.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQL {

	String dbName;
	String[] tableName;
	SQLiteDatabase db;
	Context context;

	public SQL(Context context, String dbName, String[] tableName) {
		this.context=context;
		this.dbName = dbName;
		this.tableName = tableName;
		initDb(context);
	}

	// 主活动中传入上下文信息
	void initDb(Context context) {
		createDb(context);
		createTable(context);
		initTableData();
	}

	public void createDb(Context context) {
		db = context.openOrCreateDatabase(SQLUtils.dbName, Context.MODE_PRIVATE, null);
	}

	public void createTable(Context context) {
		String table_books = "create table if not exists " + SQLUtils.tableName_book + "("
				+ SQLUtils.table_book_column[0] + " text primary key, " + SQLUtils.table_book_column[1] + " text, "
				+ SQLUtils.table_book_column[2] + " text, " + SQLUtils.table_book_column[3] + " text, "
				+ SQLUtils.table_book_column[4] + " text, " + "foreign key(" + SQLUtils.table_book_column[3] + ")"
				+ "references " + SQLUtils.tableName_printer + "(" + SQLUtils.table_printer_column[0] + ")" + ")";

		String table_printer = "create table if not exists " + SQLUtils.tableName_printer + "("
				+ SQLUtils.table_printer_column[0] + " text primary key, " + SQLUtils.table_printer_column[1] + " text"
				+ ")";

		db.execSQL(table_printer);
		db.execSQL(table_books);
	}

	// 初始化表数据，为表添加若干行
	public void initTableData() {
		boolean bools_books[] = { true, true, true, true, true };
		boolean bools_printers[] = { true, true };

		String[][] strings_books = { { "C语言程序设计", "Tom", "1999", "东方红出版社", "200" },
				{ "java语言程序设计", "雷雷", "2013", "东方红出版社", "250" }, };
		String[][] strings_printers = { { "东方红出版社", "湖北省" } };
		for (int i = 0; i < strings_printers.length; i++) {
			insertData_printer(bools_printers, strings_printers[i]);
		}

		for (int i = 0; i < strings_books.length; i++) {
			insertData_book(bools_books, strings_books[i]);
		}
	}

	public void insert() {

	}

	// 根据指定条件查询相应结果，并返回结果集 (单表查询book)
	public Cursor search_book(boolean[] bools, String[] strings, int search_Mode) {
		// 首先需要根据查询条件构建查询表达式
		String searchSQL = "select * from " + SQLUtils.tableName_book + " where ";
		String link_op;//取决于查找模式，如果是精确查找使用'=',否则，使用‘like'
		if(search_Mode==SQLUtils.SEARCH_EXACT){
			link_op=" = ";
		}else{
			link_op=" like ";
		}
		if (bools.length != strings.length 
				&& bools.length != SQLUtils.table_book_column.length) {
			return null;
		}
		int and_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (and_flag == 0) {
					searchSQL += " " + SQLUtils.table_book_column[i] 
							   + link_op 
							   + SQLUtils.quotationed(strings[i],search_Mode);
					and_flag = 1;
				} else {
					searchSQL += " and " + SQLUtils.table_book_column[i] 
							   + link_op 
							   + SQLUtils.quotationed(strings[i],search_Mode);
				}
			}
		}
		Utils.showToast(context, searchSQL);
		//return null;
		return db.rawQuery(searchSQL, null);
	}

	// 根据指定条件查询相应结果，并返回结果集 (单表查询printer)
	public Cursor search_printer(boolean[] bools, String[] strings, int search_mode) {
		// 首先需要根据查询条件构建查询表达式
		String searchSQL = "select * from " + SQLUtils.tableName_printer + " where ";
		String link_op;
		if(search_mode==SQLUtils.SEARCH_EXACT){
			link_op=" = ";
		}else{
			link_op=" like ";
		}
		if (bools.length != strings.length 
				&& bools.length != SQLUtils.table_printer_column.length) {
			return null;
		}
		int and_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (and_flag == 0) {
					searchSQL += " " + SQLUtils.table_book_column[i] 
							   + link_op 
							   + SQLUtils.quotationed(strings[i],search_mode);
					and_flag = 1;
				} else {
					searchSQL += " and " + SQLUtils.table_book_column[i] 
							   + link_op
							   + SQLUtils.quotationed(strings[i],search_mode);
				}
			}
		}

		return db.rawQuery(searchSQL, null);
	}

	public ResultSet search_join(boolean[] bools, String[] strings) {
		// 首先需要根据查询条件构建查询表达式
		// String searchSQL="select * from "+SQLUtils.tableName_book+" book, "
		// +SQLUtils.tableName_printer+" printer"
		// +" where book.printer=printer.printer ";
		// if(bools.length!=strings.length){
		// return null;
		// }
		// for(int i=0;i<bools.length;i++){
		// if(bools[i]==true){
		// searchSQL+="and "+SQLUtils.
		//
		// }
		// }
		//
		return null;
	}

	public boolean insertData_book(boolean[] bools, String[] strings) {
		String insertSql = "insert into " + SQLUtils.tableName_book + "(";
		int comma_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (comma_flag == 0) {
					insertSql += SQLUtils.table_book_column[i];
					comma_flag = 1;
				} else {
					insertSql += "," + SQLUtils.table_book_column[i];
				}
			}
		}
		insertSql += ")" + "values(";
		comma_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (comma_flag == 0) {
					insertSql += SQLUtils.quotationed(strings[i]);
					comma_flag = 1;
				} else {
					insertSql += "," + SQLUtils.quotationed(strings[i]);
				}
			}
		}
		insertSql += ")";
		try {
			db.execSQL(insertSql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean insertData_printer(boolean[] bools, String[] strings) {
		String insertSql = "insert into " + SQLUtils.tableName_printer + "(";
		int comma_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (comma_flag == 0) {
					insertSql += SQLUtils.table_printer_column[i];
					comma_flag = 1;
				} else {
					insertSql += "," + SQLUtils.table_printer_column[i];
				}
			}
		}
		insertSql += ")" + "values(";
		comma_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (comma_flag == 0) {
					insertSql += SQLUtils.quotationed(strings[i]);
					comma_flag = 1;
				} else {
					insertSql += "," + SQLUtils.quotationed(strings[i]);
				}
			}
		}
		insertSql += ")";
		try {
			db.execSQL(insertSql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean delete_book(boolean[] bools, String[] strings) {
		String deletesql = "delete from " + SQLUtils.tableName_book + " where ";
		int and_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (and_flag == 0) {
					deletesql += " " + SQLUtils.table_book_column[i] + "=" + SQLUtils.quotationed(strings[i]);
					and_flag = 1;
				} else {
					deletesql += " and " + SQLUtils.table_book_column[i] + "=" + SQLUtils.quotationed(strings[i]);
				}
			}
		}
		try {
			db.execSQL(deletesql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean delete_printer(boolean[] bools, String[] strings) {
		String deletesql = "delete from " + SQLUtils.tableName_printer + " where ";
		int and_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (and_flag == 0) {
					deletesql += " " + SQLUtils.table_printer_column[i] + "="
				                     + SQLUtils.quotationed(strings[i]);
					and_flag = 1;
				} else {
					deletesql += " and " + SQLUtils.table_printer_column[i] + "=" 
				                         + SQLUtils.quotationed(strings[i]);
				}
			}
		}
		try {
			db.execSQL(deletesql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
