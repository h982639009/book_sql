package com.example.books.sql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.example.books.R;
import com.example.books.ResultActivity;
import com.example.books.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SQL {

	String dbName;
	String[] tableName;
	SQLiteDatabase db;
	Context context;
	Context mainActivity_context;
	final int DIALOG_INSERT =0;
	final int DIALOG_DELETE =1;
	final int DIALOG_UPDATE =2;
	final int DIALOG_SEARCH =3;
	
	Cursor cursor;

	public SQL(Context context, String dbName, String[] tableName) {
		this.context=context;
		this.dbName = dbName;
		this.tableName = tableName;
		initDb(context);
	}
	
	public void setMainActivityContext(Context context){
		this.mainActivity_context=context;
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
			//不生成表达式
			insertData_book(bools_books, strings_books[i],SQLUtils.GENE_EXPR_N);
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
			int link_condition_op,int order_or_not,int search_column,int search_order,
			int expr) {
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
		 //Cursor cursor;
		if(expr==SQLUtils.GENE_EXPR_Y){
			showDialog(searchSQL, DIALOG_SEARCH);
		}else{
			try{
			    cursor = db.rawQuery(searchSQL, null);
			    Utils.showToast(context, "搜索书籍表成功！");
			}catch(Exception e){
				Utils.showToast(context, "啊哦~搜索书籍表时遇到了错误！");
				cursor=null;
			}
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
		
		//Cursor cursor;
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

	//返回值没有什么用了..!
	public boolean insertData_book(boolean[] bools, String[] strings,int expr) {
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
		
		if(expr==SQLUtils.GENE_EXPR_Y){
			showDialog(insertSql,DIALOG_INSERT);
			return false; 
		}else{
			try {
				db.execSQL(insertSql);
				Utils.showToast(context, "插入到书籍表成功啦！~");
				return true;
			} catch (Exception e) {
				Utils.showToast(context, "啊哦~插入到书籍表时遇到了错误！~");
				return false;
			}
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

	//返回值没用了。。。！
	public boolean delete_book(boolean[] bools, String[] strings,int expr) {
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
		
		if(expr==DIALOG_DELETE){
			showDialog(deletesql, DIALOG_DELETE);
			return false;
		}else{
			try {
				db.execSQL(deletesql);
				Utils.showToast(context, "从书籍表删除记录成功啦！~");
				return true;
			} catch (Exception e) {
				Utils.showToast(context, "啊哦~尝试从书籍表删除记录失败了！~");
				return false;
			}
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
	//返回值没用..!
	public boolean update_book(int columnindex,String str,String primarykey,int expr) {
		String updatesql = "update " + SQLUtils.tableName_book 
				           + " set " 
				           + SQLUtils.table_book_column[columnindex]
				           +" = "
				           + SQLUtils.quotationed(str)
				           +" where title="
				           + SQLUtils.quotationed(primarykey)
				           ;
		
		if(expr==SQLUtils.GENE_EXPR_Y){
			showDialog(updatesql,DIALOG_UPDATE );
			return false;
		}else{
			try {
				db.execSQL(updatesql);
				Utils.showToast(context, "更新书籍表成功啦！~");
				return true;
			} catch (Exception e) {
				Utils.showToast(context, "啊哦~尝试更新书籍表时遇到了错误！~");
				return false;
			}
		}
	}
	
	//默认不生成表达式
	public boolean update_book(int columnindex,String str,String primarykey) {
		update_book(columnindex, str, primarykey,SQLUtils.GENE_EXPR_N);
		return false;
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
		
		boolean ret;
		//flag 0:exec
		//flag 1:query
		public void showDialog(final String sql,final int flag){
			
			LayoutInflater inflater=LayoutInflater.from(mainActivity_context);
			View dialog=inflater.inflate(R.layout.sql_dialog, null);
			TextView textView=(TextView) dialog.findViewById(R.id.sql_dialog_text);
			textView.setText(sql);
			AlertDialog.Builder builder=new AlertDialog.Builder(mainActivity_context);
			builder.setCancelable(false)
			       .setTitle("您将要执行如下sql语句")
			       .setView(dialog)
			       .setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (flag) {
						case DIALOG_INSERT:
							try{
								db.execSQL(sql);
								Utils.showToast(context, "插入成功");
							}catch (Exception e) {
								Utils.showToast(context, "插入失败");
							}
							break;
						case DIALOG_DELETE:
							try{
								db.execSQL(sql);
								Utils.showToast(context, "删除成功");
							}catch (Exception e) {
								Utils.showToast(context, "删除失败");
							}
							break;
						case DIALOG_UPDATE:
							try{
								db.execSQL(sql);
								Utils.showToast(context, "更新成功");
							}catch (Exception e) {
								Utils.showToast(context, "更新失败");
							}
							break;
						case DIALOG_SEARCH:
							try{
								cursor=db.rawQuery(sql,null);
								Utils.showToast(context, "查询成功");
								
								if(cursor==null){
									break;
								}
								ArrayList<String> strList=new ArrayList<String>();
								for(int i=0;i<SQLUtils.table_book_column.length;i++){
									strList.add(SQLUtils.table_book_column[i]);
								}
								while(cursor.moveToNext()){
									//showToast("find:"+cursor.getString(0));
									strList.add(cursor.getString(0));
									strList.add(cursor.getString(1));
									strList.add(cursor.getString(2));
									strList.add(cursor.getString(3));
									strList.add(cursor.getString(4));
									
								}
								Intent intent =new Intent(mainActivity_context,ResultActivity.class);
								Bundle bundle=new Bundle();
								bundle.putInt("columnNo", SQLUtils.table_book_column.length);
								bundle.putSerializable("strlist", (Serializable) strList);
								intent.putExtras(bundle);
								mainActivity_context.startActivity(intent);
							}catch (Exception e) {
								Utils.showToast(context, "查询失败");
							}
							break;

						default:
							break;
						}
					}
				})
			       .setNegativeButton("取消",new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
			       .show();
			
		}
	

}
