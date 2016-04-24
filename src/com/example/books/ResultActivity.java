package com.example.books;

import java.util.ArrayList;

import com.example.books.sql.SQLUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

public class ResultActivity extends Activity{
	GridView gridview;
	CheckBox cb_title,cb_author,cb_year,cb_book_printer,cb_printer_printer,cb_location,cb_pageNo;
	int columns;
	ArrayList<String> strList;
	GridViewAdapter adapter;
	boolean book_bools[]=new boolean[SQLUtils.table_book_column.length];
	boolean printer_bools[]=new boolean[SQLUtils.table_printer_column.length];
	
	final int TABLE_BOOK=1;
	final int TABLE_PRINT=2;
	
	int currentTable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			columns=bundle.getInt("columnNo");	
			strList=(ArrayList<String>) bundle.getSerializable("strlist");
			Utils.showToast(ResultActivity.this,strList.size()+":"+strList.get(0));
		}else{
			Utils.showToast(ResultActivity.this, "bundle null");
		}
		
		initBool();
		
		
		gridview=(GridView) findViewById(R.id.result_gridView);
		gridview.setNumColumns(columns);
		adapter=new GridViewAdapter(ResultActivity.this, strList, R.layout.item_gridview);
		gridview.setAdapter(adapter);
		addGridViewListener();
		
		cb_title=(CheckBox)findViewById(R.id.cb_title);
		cb_author=(CheckBox)findViewById(R.id.cb_author);
		cb_year=(CheckBox)findViewById(R.id.cb_year);
		cb_book_printer=(CheckBox)findViewById(R.id.cb_book_printer);
		cb_location=(CheckBox)findViewById(R.id.cb_location);
		cb_pageNo=(CheckBox)findViewById(R.id.cb_pageNo);
		cb_printer_printer=(CheckBox)findViewById(R.id.cb_printer_printer);
		
		if(columns==SQLUtils.table_book_column.length){
			cb_title.setVisibility(View.VISIBLE);
			cb_author.setVisibility(View.VISIBLE);
			cb_year.setVisibility(View.VISIBLE);
			cb_book_printer.setVisibility(View.VISIBLE);
			cb_pageNo.setVisibility(View.VISIBLE);
			
			cb_location.setVisibility(View.GONE);
			cb_printer_printer.setVisibility(View.GONE);
			
			currentTable=TABLE_BOOK;
		}else if(columns==SQLUtils.table_printer_column.length){
			cb_title.setVisibility(View.GONE);
			cb_author.setVisibility(View.GONE);
			cb_year.setVisibility(View.GONE);
			cb_book_printer.setVisibility(View.GONE);
			cb_pageNo.setVisibility(View.GONE);
			
			cb_location.setVisibility(View.VISIBLE);
			cb_printer_printer.setVisibility(View.VISIBLE);
			
			currentTable=TABLE_PRINT;
		}
		
		OnCheckedChangeListener listener=new OnCheckedChangeListener() {
			ArrayList<String> list;
			int columnNO=0;
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//每次调用该方法时，都要重新给变量赋初值，否则会保持上一次的值（因为使用的是同一个对象）
				list=new ArrayList<String>();
				columnNO=0;
				
				switch(buttonView.getId()){
				case R.id.cb_title:
					book_bools[0]=isChecked;
					break;
				case R.id.cb_author:
					book_bools[1]=isChecked;
					break;
				case R.id.cb_year:
					book_bools[2]=isChecked;
					break;
				case R.id.cb_book_printer:
					book_bools[3]=isChecked;
					break;
				case R.id.cb_pageNo:
					book_bools[4]=isChecked;
					break;
				case R.id.cb_printer_printer:
					printer_bools[0]=isChecked;
					break;
				case R.id.cb_location:
					printer_bools[1]=isChecked;
					break;
				}
				
				
				if(currentTable==TABLE_BOOK){
					for(int i=0;i<book_bools.length;i++){
						if(book_bools[i]){
							columnNO++;
						}
					}
					Utils.showToast(ResultActivity.this, ""+columnNO);
					//list=new ArrayList<String>();
					for(int i=0;i<strList.size();i++){
						if(selectOrNot_book(i,book_bools))
						{
							list.add(strList.get(i));
						}
					}
					adapter.list=list;
				}else if(currentTable==TABLE_PRINT){
					for(int i=0;i<printer_bools.length;i++){
						if(printer_bools[i]){
							columnNO++;
						}
					}
					//list=new ArrayList<String>();
					for(int i=0;i<printer_bools.length;i++){
						if(selectOrNot_printer(i,printer_bools)){
							list.add(strList.get(i));
						}
					}
					adapter.list=list;
				}
				
				columns=columnNO;
				gridview.setNumColumns(columns);
				adapter.notifyDataSetChanged();
				
			}	
		};
        addCheckedListener(listener);
	}
	
	void addCheckedListener(OnCheckedChangeListener listener){
		cb_title.setOnCheckedChangeListener(listener);
		cb_author.setOnCheckedChangeListener(listener);
		cb_year.setOnCheckedChangeListener(listener);
		cb_book_printer.setOnCheckedChangeListener(listener);
		cb_pageNo.setOnCheckedChangeListener(listener);
		
		cb_printer_printer.setOnCheckedChangeListener(listener);
		cb_location.setOnCheckedChangeListener(listener);
	}
	
	boolean selectOrNot_book(int index,boolean[] book_bools){
		int len=book_bools.length;
		for(int i=0;i<book_bools.length;i++ ){
			if(book_bools[i]==true && (index+len-i) % len==0){
				return true;
			}
		}
		return false;
	}
	
	boolean selectOrNot_printer(int index,boolean[] print_bools){
		int len=print_bools.length;
		for(int i=0;i<print_bools.length;i++ ){
			if(print_bools[i]==true && (index+len-i) % len==0){
				return true;
			}
		}
		return false;
	}
	
	void initBool(){
		for(int i=0;i<book_bools.length;i++){
			book_bools[i]=true;
		}
		
		for(int i=0;i<printer_bools.length;i++){
			printer_bools[i]=true;
		}
	}
	
	void addGridViewListener(){
		//gridview.setONItem
	}
}
