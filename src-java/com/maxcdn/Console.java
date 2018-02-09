package com.maxcdn;

public class Console {
	public static boolean shoudlog = true;
	public static void log(Object obj){
		if(shoudlog)
		System.out.println(obj);
	}
}
