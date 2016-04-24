package com.example.books;

import android.content.Context;
import android.widget.Toast;

public class Utils {
	public static boolean isEmpty(String string){
		if(string==null || string.length()==0){
			return true;
		}else{
			return false;
		}
	}
	
	public static void showToast(Context context,String string){
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

}
