package com.example.apple;

import android.content.Context;
import android.media.MediaPlayer;

public class music {
   private static MediaPlayer mp = null;

   
   /** Stop old song and start new one */
   public static void play(Context context, int resource) {
      stop(context);

      // Start music only if not disabled in preferences
      if (settings.getMusic(context)) {
         mp = MediaPlayer.create(context, resource);
         mp.setLooping(true);
         mp.start();
      }
   }
   

   /** Õ£÷π“Ù¿÷ */
   public static void stop(Context context) { 
      if (mp != null) {
         mp.stop();
         mp.release();
         mp = null;
      }
   }
}
