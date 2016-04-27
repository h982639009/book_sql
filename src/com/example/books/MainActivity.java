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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	Spinner spinner_op,spinner_table,spinner_mode,spinner_link_op;
	LinearLayout table_book,table_printer;
	LinearLayout table_book_layout_year,table_book_layout_pageNo;
	Button btn_apply;
	EditText ed_title,ed_author,ed_year_start,ed_year_end,ed_book_printer,ed_pageNo_start;
	EditText ed_pageNo_end,ed_printer_printer,ed_location;
	CheckBox cb_sort;
	Spinner spinner_sort_type,spinner_sort_order;//分别表示按哪一列排序和按照升序或降序
	
	LinearLayout layout_condition,layout_sort;
	LinearLayout layout_gene_expr;
	CheckBox cb_expr;
	
	//当前操作：0-3分别表示插入、查询、更新、删除
	int currentOP=0;
	//当前操作表名称：0-2分别表示书籍表、出版社表、连接表
	int currentTable=0;
	int currentMode;
	int currentLinkOP=SQLUtils.AND_CONDITION;//条件与或者条件或
	
	int need_order=SQLUtils.NEDD_ORDER_N;
	int incre_or_dcre=SQLUtils.ORDER_INCREASE;
	int order_column=0;
	int need_expr=SQLUtils.GENE_EXPR_N;//默认不产生表达式
	
	SQL sql;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化数据库
		initViewReference();
		//sql=new SQL(MainActivity.this,SQLUtils.dbName,SQLUtils.tableNames);
		sql=((MyApplication)getApplication()).getSQL();
		
	}
	
	void initViewReference(){
		spinner_op=(Spinner) findViewById(R.id.spinner_op);
		spinner_table=(Spinner) findViewById(R.id.spinner_table);
		spinner_mode=(Spinner)findViewById(R.id.spinner_mode);
		spinner_link_op=(Spinner)findViewById(R.id.spinner_link_op);
		spinner_sort_order=(Spinner)findViewById(R.id.sort_spinner_order);
		spinner_sort_type=(Spinner)findViewById(R.id.sort_spinner_type);
		
		cb_sort=(CheckBox)findViewById(R.id.sort_cb);
		layout_condition=(LinearLayout)findViewById(R.id.layout_condition);
		layout_sort=(LinearLayout)findViewById(R.id.layout_sort);
		
		layout_gene_expr=(LinearLayout)findViewById(R.id.layout_gene_expr);
		cb_expr=(CheckBox)findViewById(R.id.expr_cb);
		
//		table_book=(LinearLayout) findViewById(R.id.include_table_book)
//				.findViewById(R.id.layout_table_book);
//		table_printer=(LinearLayout) findViewById(R.id.include_table_printer)
//				.findViewById(R.id.layout_table_printer);
		
		table_book=(LinearLayout) findViewById(R.id.layout_table_book);
		table_printer=(LinearLayout) findViewById(R.id.layout_table_printer);
		btn_apply=(Button) findViewById(R.id.btn_apply);
		
		ed_title=(EditText) findViewById(R.id.table_book_ed_title);
		ed_author=(EditText) findViewById(R.id.table_book_ed_author);
		ed_year_start=(EditText)findViewById(R.id.table_book_ed_year_start);
		ed_year_end=(EditText)findViewById(R.id.table_book_ed_year_end);
		ed_book_printer=(EditText)findViewById(R.id.table_book_ed_printer);
		ed_pageNo_start=(EditText)findViewById(R.id.table_book_ed_pageNo_start);
		ed_pageNo_end=(EditText)findViewById(R.id.table_book_ed_pageNo_end);
		
		ed_printer_printer=(EditText)findViewById(R.id.table_printer_ed_printer);
		ed_location=(EditText)findViewById(R.id.table_printer_ed_location);
		
		table_book_layout_year=(LinearLayout)findViewById(R.id.table_book_layout_year);
		table_book_layout_pageNo=(LinearLayout)findViewById(R.id.table_book_layout_pageNo);
		
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
		spinner_link_op.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
				R.array.link_op, android.R.layout.simple_spinner_item));
		
		spinner_sort_order.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
				R.array.sort_order, android.R.layout.simple_spinner_item));
		
		
		
		spinner_table.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentTable=position;
				switch (position) {
				case 0:
					table_printer.setVisibility(View.GONE);
					table_book.setVisibility(View.VISIBLE);
					spinner_sort_type.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
							R.array.book_column, android.R.layout.simple_spinner_item));
					//showToast("position 0");
					break;
					
				case 1:
					table_book.setVisibility(View.GONE);
					table_printer.setVisibility(View.VISIBLE);
					spinner_sort_type.setAdapter(ArrayAdapter.createFromResource(MainActivity.this,
							R.array.printer_column, android.R.layout.simple_spinner_item));
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
					//插入操作时，需要隐藏部分组件，例如出版年份和页码的第二个方框
					table_book_layout_pageNo.setVisibility(View.GONE);
					table_book_layout_year.setVisibility(View.GONE);
					//隐藏条件操作和排序操作（插入不需要这些操作）
					layout_condition.setVisibility(View.GONE);
					layout_sort.setVisibility(View.GONE);
					break;
				case 1:
					btn_apply.setText("查询");
					table_book_layout_pageNo.setVisibility(View.VISIBLE);
					table_book_layout_year.setVisibility(View.VISIBLE);
					layout_condition.setVisibility(View.VISIBLE);
					layout_sort.setVisibility(View.VISIBLE);
					break;
				case 2:
					btn_apply.setText("更新");
					table_book_layout_pageNo.setVisibility(View.VISIBLE);
					table_book_layout_year.setVisibility(View.VISIBLE);
					layout_condition.setVisibility(View.VISIBLE);
					layout_sort.setVisibility(View.GONE);
					break;
				case 3:
					btn_apply.setText("删除");
					table_book_layout_pageNo.setVisibility(View.VISIBLE);
					table_book_layout_year.setVisibility(View.VISIBLE);
					layout_condition.setVisibility(View.VISIBLE);
					layout_sort.setVisibility(View.GONE);
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
		
		spinner_link_op.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){	
					currentLinkOP=SQLUtils.AND_CONDITION;
				}else{
					currentLinkOP=SQLUtils.OR_CONDITION;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		spinner_sort_order.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){	
					incre_or_dcre=SQLUtils.ORDER_INCREASE;
				}else{
					incre_or_dcre=SQLUtils.ORDER_DECREASE;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		spinner_sort_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				order_column=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		cb_sort.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					need_order=SQLUtils.NEED_ORDER_Y;
				}else{
					need_order=SQLUtils.NEDD_ORDER_N;
				}
				
			}
		});
		
		cb_expr.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					need_expr=SQLUtils.GENE_EXPR_Y;
				}else{
					need_expr=SQLUtils.GENE_EXPR_N;
				}
			}
		});
		btn_apply.setOnClickListener(new OnClickListener() {
			Cursor cursor;
			String str_title,str_author,str_year_start,str_year_end,str_printer;
			String str_pageNo_start,str_pageNo_end;
			
			String str_year,str_pageNo;
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
					
					str_year_start=ed_year_start.getText().toString();
					str_year_end=ed_year_end.getText().toString();
					if(!Utils.isEmpty(str_year_start) && !Utils.isEmpty(str_year_end) ){
						str_year=str_year_start+";"+str_year_end;
					}
					
					if(Utils.isEmpty(str_year_start)){
						str_year=str_year_end;
					}
					
					if(Utils.isEmpty(str_year_end)){
						str_year=str_year_start;
					}
					
					
					str_printer=ed_book_printer.getText().toString();
					
					str_pageNo_start=ed_pageNo_start.getText().toString();
					str_pageNo_end=ed_pageNo_end.getText().toString();
					//str_pageNo=str_pageNo_start+";"+str_pageNo_end;
					if(!Utils.isEmpty(str_pageNo_start) && !Utils.isEmpty(str_pageNo_end) ){
						str_pageNo=str_pageNo_start+";"+str_pageNo_end;
					}
					
					if(Utils.isEmpty(str_pageNo_start)){
						str_pageNo=str_pageNo_end;
					}
					
					if(Utils.isEmpty(str_pageNo_end)){
						str_pageNo=str_pageNo_start;
					}
					
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
						//showToast("插入成功");
					}else{
						sql.insertData_printer(bools_printer, strings_printer);
						//showToast("插入成功");
					}
					break;
				case 1://查询
					if(currentTable==0){
						cursor=sql.search_book(bools_book, strings_book,currentMode,currentLinkOP,
								need_order,order_column,incre_or_dcre);
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
						cursor=sql.search_printer(bools_printer, strings_printer,currentMode,currentLinkOP,
								need_order,order_column,incre_or_dcre);
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
						//showToast("delete success!");
					}else if(currentTable==1){
						sql.delete_printer(bools_printer,strings_printer);
						//showToast("delete success!");
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
