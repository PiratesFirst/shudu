package com.example.apple;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class shudu extends Activity implements OnClickListener {

	private static final String TAG = "shudu";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		View continueButton = findViewById(R.id.continue_button);
		
		/*由于该类继承了view的监听，  因此设置监听的参数只需传本类的对象this即可  .
		 * public void setOnClickListener (View.OnClickListener l) */
		continueButton.setOnClickListener(this);
		View newButton = findViewById(R.id.new_game);
		
		/**由于使用this作为接收方，因此shudu类需要实现onClickListener接口，并定义onClick()*/
		newButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.about);
		aboutButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit);
		exitButton.setOnClickListener(this);
	}
	/**v	is the view that was clicked.*/
	public void onClick(View v){
		/*getId() Returns this view's identifier.*/
		switch (v.getId()){
	    case R.id.continue_button:
	          continueGame(game.DIFFICULTY_CONTINUE);
	          break;
		case R.id.about:
			/*Intent(Context packageContext, Class<?> cls)
				Create an intent for a specific component.*/
			Intent i = new Intent(this, about.class);
			startActivity(i);
			break;
		case R.id.new_game:
			openNewGameDialog();
			break;
		case R.id.exit:
			/*finish方法结束当前活动，并将控制权交给android应用程序栈中的下一个活动*/
			finish();
			break;
		}
	}
	
	/*Initialize the contents of the Activity's standard options menu，
	 * You must return true for the menu to be displayed;
	 *  if you return false it will not be shown*/
	public boolean onCreateOptionsMenu(Menu menu){
		 super.onCreateOptionsMenu(menu);
	     getMenuInflater().inflate(R.menu.menu, menu);
	     return true;
	}

	/**返回值表示是否对菜单的选择事件进行处理
如果已经处理过则返回true，否则返回false
*/
	public boolean onOptionsItemSelected(MenuItem item){
		Log.d(TAG, "menuitem start");
		super.onOptionsItemSelected(item);
		switch (item.getItemId()){
		case R.id.itemsettings:
			startActivity(new Intent(this, settings.class));
			return true;
		}
		return false;
	}
	
	void openNewGameDialog(){
		/*AlertDialog.Builder(Context context)
          Constructor using a context for this builder and the AlertDialog it creates.*/
		new AlertDialog.Builder(this).setTitle(R.string.new_game_title)
		/*setItems()方法让添加一个可选列表，这个方法需要两个参数：一个是要显示的列表项数组，
		 * 另一个是定义用户选择项目时所以执行的操作的DialogInterface.OnClickListener监听器*/
		.setItems(R.array.difficulty, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				continueGame(which);
			}
		}).show();
	}
	
	public void continueGame(int which){
		Log.d(TAG,"clicked on" + which);
		Intent intent = new Intent(this, game.class);
		/*putExtra(key,value);
		作用：以键/值对形式在intent对象中保存基本数据类型的数据。
*/
		intent.putExtra(game.KEY_DIFFICULTY, which);
		startActivity(intent);
	}
}
