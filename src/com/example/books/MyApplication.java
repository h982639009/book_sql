package com.example.books;

import com.example.books.sql.SQL;
import com.example.books.sql.SQLUtils;

public class MyApplication extends android.app.Application {
	 SQL sql;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sql=new SQL(getApplicationContext(), SQLUtils.dbName, SQLUtils.tableNames);
	}
	public  SQL getSQL(){
		return sql;
	}

}
