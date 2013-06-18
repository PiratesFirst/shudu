package com.example.apple;

import android.app.Activity;
import android.os.Bundle;

public class about extends Activity{
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}

}

/*onCreate Called when the activity is starting. This is where most initialization
 *  should go: calling setContentView(int) to inflate the activity's UI,
 *   using findViewById(int) to programmatically interact with widgets in the UI
 *   , calling managedQuery(android.net.Uri, String[], String, String[], String)
 *    to retrieve cursors for data being displayed, etc.
 */

/*You can call finish() from within this function, 
 * in which case onDestroy() will be immediately called 
 * without any of the rest of the activity lifecycle
 *  (onStart(), onResume(), onPause(), etc) executing.
 */

/*Derived classes must call through to the super
 *  class's implementation of this method. If
 *   they do not, an exception will be thrown.
* Parameters savedInstanceState	
* If the activity is being re-initialized after previously
*  being shut down then this Bundle contains the data it 
* most recently supplied in onSaveInstanceState(Bundle).
*  Note: Otherwise it is null.
*  在super.onXXX前不要执行任何代码*/