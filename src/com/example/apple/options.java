package com.example.apple;

import android.content.Context;
import android.content.SharedPreferences;

public class options {

   private static final String SUDOKU_OPTIONS = shudu.class.getName(); 
   private static final String OPT_MUSIC = "music"; 
   private static final boolean OPT_MUSIC_DEF = true;
   private static final String OPT_HINTS = "hints";
   private static final boolean OPT_HINTS_DEF = true;

   private static SharedPreferences getShuduPreferences( 
         Context context) {
      return context.getSharedPreferences(SUDOKU_OPTIONS,
            Context.MODE_PRIVATE);
   }

   public static boolean getMusic(Context context) { 
      return getShuduPreferences(context).getBoolean(
            OPT_MUSIC, OPT_MUSIC_DEF);
   }

   public static boolean getHints(Context context) {
      return getShuduPreferences(context).getBoolean(
            OPT_HINTS, OPT_HINTS_DEF);
   }

   public static boolean putMusic(Context context, boolean value) { 
      return getShuduPreferences(context)
            .edit()
            .putBoolean(OPT_MUSIC, value)
            .commit();
   }

   public static boolean putHints(Context context, boolean value) {
      return getShuduPreferences(context)
            .edit()
            .putBoolean(OPT_HINTS, value)
            .commit();
   }
}
