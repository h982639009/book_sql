package com.example.books.sql;

import java.sql.ResultSet;

import com.example.books.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.DropBoxManager;

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
		//dropTable(context);
		createTable(context);
		initTableData();
	}

	public void createDb(Context context) {
		db = context.openOrCreateDatabase(SQLUtils.dbName, Context.MODE_PRIVATE, null);
	}
	
	public void dropTable(Context context){
		String table_books ="drop table "+SQLUtils.tableName_book;
		String table_printer ="drop table "+SQLUtils.tableName_printer;
		
	}

	public void createTable(Context context) {
		String table_books = "create table if not exists " + SQLUtils.tableName_book + "("
				+ SQLUtils.table_book_column[0] + " text primary key, " + SQLUtils.table_book_column[1] + " text, "
				+ SQLUtils.table_book_column[2] + " int, " + SQLUtils.table_book_column[3] + " text, "
				+ SQLUtils.table_book_column[4] + " int, " + "foreign key(" + SQLUtils.table_book_column[3] + ")"
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
	//search_Mode表示精确或者模糊查找
	//link_condition_op表示条件间的连接
	//order_or_not 表示是否需要排序
	//search_column 按某一列进行排序
	//search_order 升序或降序
	public Cursor search_book(boolean[] bools, String[] strings, int search_Mode,
			int link_condition_op,int order_or_not,int search_column,int search_order ) {
		// 首先需要根据查询条件构建查询表达式
		String searchSQL = "select * from " + SQLUtils.tableName_book + " where ";
		String link_op;//取决于查找模式，如果是精确查找使用'=',否则，使用‘like'
		String link_con_op;//取决于条件连接模式
		
		if(search_Mode==SQLUtils.SEARCH_EXACT){
			link_op=" = ";
		}else{
			link_op=" like ";
		}
		
		if(link_condition_op==SQLUtils.AND_CONDITION){
			link_con_op=" and ";
		}else{
			link_con_op=" or ";
		}
		
		if (bools.length != strings.length 
				&& bools.length != SQLUtils.table_book_column.length) {
			return null;
		}
		int and_flag = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] == true) {
				if (and_flag == 0) {
					if(i==2 || i==4){//年份和页数要单独处理
						String[] sub=strings[i].split(";");
						if(sub.length==2){
							//填了两项，使用between
							searchSQL += " " + SQLUtils.table_book_column[i] 
									   + " between " 
									   + sub[0] + " and " +sub[1];
							
						}else{
							searchSQL += " " + SQLUtils.table_book_column[i] 
									   + " = " 
									   + sub[0] ;
						}
					}else{
						searchSQL += " " + SQLUtils.table_book_column[i] 
								   + link_op 
								   + SQLUtils.quotationed(strings[i],search_Mode);
					}
					and_flag = 1;
				} else {
					if(i==2 || i==4){//年份和页数要单独处理
						String[] sub=strings[i].split(";");
						if(sub.length==2){
							//填了两项，使用between
							searchSQL += link_con_op + SQLUtils.table_book_column[i] 
									   + " between " 
									   + sub[0] + " and " +sub[1];
							
						}else{
							searchSQL += link_con_op + SQLUtils.table_book_column[i] 
									   + " = " 
									   + sub[0] ;
						}
					}else{
						searchSQL += link_con_op + SQLUtils.table_book_column[i] 
								   + link_op 
								   + SQLUtils.quotationed(strings[i],search_Mode);
					}
				}
			}
		}
		
		if(order_or_not==SQLUtils.NEED_ORDER_Y){
			searchSQL += " order by " + SQLUtils.table_book_column[search_column];
			if(search_order==SQLUtils.ORDER_DECREASE){
				searchSQL+= " desc";
			}
		}
			
		Utils.showToast(context, searchSQL);
		//return null;
		 Cursor cursor;
		try{
		    cursor = db.rawQuery(searchSQL, null);
		    Utils.showToast(context, "搜索书籍表成功！");
		}catch(Exception e){
			Utils.showToast(context, "啊哦~搜索书籍表时遇到了错误！");
			cursor=null;
		}
		return cursor;
	}

	// 根据指定条件查询相应结果，并返回结果集 (单表查询printer)
	public Cursor search_printer(boolean[] bools, String[] strings, int search_mode,
			int link_condition_op,int order_or_not,int search_column,int search_order ) {
		// 首先需要根据查询条件构建查询表达式
		String searchSQL = "select * from " + SQLUtils.tableName_printer + " where ";
		String link_op;
		String link_con_op;
		
		if(search_mode==SQLUtils.SEARCH_EXACT){
			link_op=" = ";
		}else{
			link_op=" like ";
		}
		
		if(link_condition_op==SQLUtils.AND_CONDITION){
			link_con_op=" and ";
		}else{
			link_con_op=" or ";
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
					searchSQL += link_con_op + SQLUtils.table_book_column[i] 
							   + link_op
							   + SQLUtils.quotationed(strings[i],search_mode);
				}
			}
		}
		
		if(order_or_not==SQLUtils.NEED_ORDER_Y){
			searchSQL += " order by " + SQLUtils.table_printer_column[search_column];
			if(search_order==SQLUtils.ORDER_DECREASE){
				searchSQL+= " desc";
			}
		}
		
		Cursor cursor;
		try{
			cursor= db.rawQuery(searchSQL, null);
			Utils.showToast(context, "搜索出版社表成功啦！~");
		}catch(Exception e){
			Utils.showToast(context, "啊哦~搜索出版社表时出现了错误！");
			cursor =null;
		}
		return cursor;
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
			Utils.showToast(context, "插入到书籍表成功啦！~");
			return true;
		} catch (Exception e) {
			Utils.showToast(context, "啊哦~插入到书籍表时遇到了错误！~");
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
			Utils.showToast(context, "插入到出版社表成功！~");
			return true;
		} catch (Exception e) {
			Utils.showToast(context, "啊哦~插入到书籍表时失败啦！~");
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
			Utils.showToast(context, "从书籍表删除记录成功啦！~");
			return true;
		} catch (Exception e) {
			Utils.showToast(context, "啊哦~尝试从书籍表删除记录失败了！~");
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
			Utils.showToast(context, "从出版社表删除记录成功啦！~");
			return true;
		} catch (Exception e) {
			Utils.showToast(context, "啊哦~尝试从出版社表删除记录遇到了错误！~");
			return false;
		}
	}
	
	//将columnindex对应列（从0开始计数）的属性值修改为str
	public boolean update_book(int columnindex,String str,String primarykey) {
		String updatesql = "update " + SQLUtils.tableName_book 
				           + " set " 
				           + SQLUtils.table_book_column[columnindex]
				           +" = "
				           + SQLUtils.quotationed(str)
				           +" where title="
				           + SQLUtils.quotationed(primarykey)
				           ;
		
	
		try {
			db.execSQL(updatesql);
			Utils.showToast(context, "更新书籍表成功啦！~");
			return true;
		} catch (Exception e) {
			Utils.showToast(context, "啊哦~尝试更新书籍表时遇到了错误！~");
			return false;
		}
	}
	
	//将columnindex对应列（从0开始计数）的属性值修改为str
		public boolean update_printer(int columnindex,String str,String primarykey) {
			String updatesql = "update " + SQLUtils.tableName_printer 
					           + " set " 
					           + SQLUtils.table_printer_column[columnindex]
					           +" = "
					           + SQLUtils.quotationed(str)
					           +" where printer="
					           + SQLUtils.quotationed(primarykey)
					           ;
			
		
			try {
				db.execSQL(updatesql);
				Utils.showToast(context, "更新出版社表成功啦！~");
				return true;
			} catch (Exception e) {
				Utils.showToast(context, "啊哦~尝试更新出版社表时遇到了错误！~");
				return false;
			}
		}
	

}
