package com.example.books;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.books.sql.SQL;
import com.example.books.sql.SQLUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Spinner spinner_op,spinner_table,spinner_mode;
	LinearLayout table_book,table_printer;
	Button btn_apply;
	EditText ed_title,ed_author,ed_year,ed_book_printer,ed_pageNo;
	EditText ed_printer_printer,ed_location;
	//当前操作：0-3分别表示插入、查询、更新、删除
	int currentOP=0;
	//当前操作表名称：0-2分别表示书籍表、出版社表、连接表
	int currentTable=0;
	int currentMode;
	
	SQL sql;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化数据库
		initViewReference();
		sql=new SQL(MainActivity.this,SQLUtils.dbName,SQLUtils.tableNames);
		
	}
	
	void initViewReference(){
		spinner_op=(Spinner) findViewById(R.id.spinner_op);
		spinner_table=(Spinner) findViewById(R.id.spinner_table);
		spinner_mode=(Spinner)findViewById(R.id.spinner_mode);
		
//		table_book=(LinearLayout) findViewById(R.id.include_table_book)
//				.findViewById(R.id.layout_table_book);
//		table_printer=(LinearLayout) findViewById(R.id.include_table_printer)
//				.findViewById(R.id.layout_table_printer);
		
		table_book=(LinearLayout) findViewById(R.id.layout_table_book);
		table_printer=(LinearLayout) findViewById(R.id.layout_table_printer);
		btn_apply=(Button) findViewById(R.id.btn_apply);
		
		ed_title=(EditText) findViewById(R.id.table_book_ed_title);
		ed_author=(EditText) findViewById(R.id.table_book_ed_author);
		ed_year=(EditText)findViewById(R.id.table_book_ed_year);
		ed_book_printer=(EditText)findViewById(R.id.table_book_ed_printer);
		ed_pageNo=(EditText)findViewById(R.id.table_book_ed_pageNo);
		
		ed_printer_printer=(EditText)findViewById(R.id.table_printer_ed_printer);
		ed_location=(EditText)findViewById(R.id.table_printer_ed_location);
		
		
		if(table_book==null || table_printer==null){
			showToast("NULL");
		}
		
		if(spinner_op==null){
			showToast("spin NULL");
		}
		
		spinner_op.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
				R.array.operation, android.R.layout.simple_spinner_item));
		spinner_table.setAdapter(ArrayAdapter.createFromResource(MainActivity.this, 
				R.array.table, android.R.layout.simple_spinner_item));
		spinner_mode.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
				R.array.mode, android.R.layout.simple_spinner_item));
		
		spinner_table.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentTable=position;
				switch (position) {
				case 0:
					table_printer.setVisibility(View.GONE);
					table_book.setVisibility(View.VISIBLE);
					showToast("position 0");
					break;
					
				case 1:
					table_book.setVisibility(View.GONE);
					table_printer.setVisibility(View.VISIBLE);
					break;
					
				default:
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		spinner_op.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentOP=position;
				switch (position) {
				case 0:
					btn_apply.setText("插入");
					break;
				case 1:
					btn_apply.setText("查询");
					break;
				case 2:
					btn_apply.setText("更新");
					break;
				case 3:
					btn_apply.setText("删除");
					break;
				default:
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		spinner_mode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){	
					currentMode=SQLUtils.SEARCH_EXACT;
				}else{
					currentMode=SQLUtils.SEARCH_FUZZY;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		btn_apply.setOnClickListener(new OnClickListener() {
			Cursor cursor;
			String str_title,str_author,str_year,str_printer,str_pageNo;
			String str_location;
			boolean[] bools_book=new boolean[SQLUtils.table_book_column.length];
			String[] strings_book=new String[SQLUtils.table_book_column.length];
			
			boolean[] bools_printer=new boolean[SQLUtils.table_printer_column.length];
			String[] strings_printer=new String[SQLUtils.table_printer_column.length];
			
			List<String> strList=new ArrayList<String>();
			@Override
			public void onClick(View v) {
				
				if(currentTable==0){//操作book表
					str_title=ed_title.getText().toString();
					str_author=ed_author.getText().toString();
					str_year=ed_year.getText().toString();
					str_printer=ed_book_printer.getText().toString();
					str_pageNo=ed_pageNo.getText().toString();
					
					strings_book[0]=str_title;
					strings_book[1]=str_author;
					strings_book[2]=str_year;
					strings_book[3]=str_printer;
					strings_book[4]=str_pageNo;
					
					for(int i=0;i<bools_book.length;i++){
						if(Utils.isEmpty(strings_book[i])){
							bools_book[i]=false;
						}else{
							bools_book[i]=true;
						}
					}
				}else if(currentTable==1){//操作printer表
					str_printer=ed_printer_printer.getText().toString();
					str_location=ed_location.getText().toString();
					
					strings_printer[0]=str_printer;
					strings_printer[1]=str_location;
					
					for(int i=0;i<bools_printer.length;i++){
						if(Utils.isEmpty(strings_printer[i])){
							bools_printer[i]=false;
						}else{
							bools_printer[i]=true;
						}
					}
				}else if(currentOP==2){//连接查询
					//暂时不实现
				}
				
				switch (currentOP) {
				case 0://插入
					if(currentTable==0){
						sql.insertData_book(bools_book, strings_book);
						showToast("插入成功");
					}else{
						sql.insertData_printer(bools_printer, strings_printer);
						showToast("插入成功");
					}
					break;
				case 1://查询
					if(currentTable==0){
						cursor=sql.search_book(bools_book, strings_book,currentMode);
						if(cursor==null){
							break;
						}
						strList=new ArrayList<String>();
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
						Intent intent =new Intent(MainActivity.this,ResultActivity.class);
						Bundle bundle=new Bundle();
						bundle.putInt("columnNo", SQLUtils.table_book_column.length);
						bundle.putSerializable("strlist", (Serializable) strList);
						intent.putExtras(bundle);
						MainActivity.this.startActivity(intent);
					}else if(currentTable==1){
						cursor=sql.search_printer(bools_printer, strings_printer,currentMode);
						strList=new ArrayList<String>();
						for(int i=0;i<SQLUtils.table_printer_column.length;i++){
							strList.add(SQLUtils.table_printer_column[i]);
						}
						while(cursor.moveToNext()){
							//showToast("find:"+cursor.getString(0));
							strList.add(cursor.getString(0));
							strList.add(cursor.getString(1));
						}
						Intent intent =new Intent(MainActivity.this,ResultActivity.class);
						Bundle bundle=new Bundle();
						bundle.putInt("columnNo", SQLUtils.table_printer_column.length);
						bundle.putSerializable("strlist", (Serializable) strList);
						intent.putExtras(bundle);
						MainActivity.this.startActivity(intent);
					}else if(currentTable==2){
						//暂时没有实现连接查询
					}
					
					break;
				case 2://更新
//					sql.delete_book(bools_book, strings_book);
					break;
				case 3://删除
					if(currentTable==0){
						sql.delete_book(bools_book, strings_book);
						showToast("delete success!");
					}else if(currentTable==1){
						sql.delete_printer(bools_printer,strings_printer);
						showToast("delete success!");
					}else if(currentTable==2){
						
					}
					break;
					
				default:
					break;
				}
				
			}
		});
		
		
		
	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showToast(String string){
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
	}
}
