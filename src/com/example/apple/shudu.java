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
		
		/*���ڸ���̳���view�ļ�����  ������ü����Ĳ���ֻ�贫����Ķ���this����  .
		 * public void setOnClickListener (View.OnClickListener l) */
		continueButton.setOnClickListener(this);
		View newButton = findViewById(R.id.new_game);
		
		/**����ʹ��this��Ϊ���շ������shudu����Ҫʵ��onClickListener�ӿڣ�������onClick()*/
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
			/*finish����������ǰ�����������Ȩ����androidӦ�ó���ջ�е���һ���*/
			finish();
			break;
		}
	}
	
	/*Initialize the contents of the Activity's standard options menu��
	 * You must return true for the menu to be displayed;
	 *  if you return false it will not be shown*/
	public boolean onCreateOptionsMenu(Menu menu){
		 super.onCreateOptionsMenu(menu);
	     getMenuInflater().inflate(R.menu.menu, menu);
	     return true;
	}

	/**����ֵ��ʾ�Ƿ�Բ˵���ѡ���¼����д���
����Ѿ�������򷵻�true�����򷵻�false
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
		/*setItems()���������һ����ѡ�б����������Ҫ����������һ����Ҫ��ʾ���б������飬
		 * ��һ���Ƕ����û�ѡ����Ŀʱ����ִ�еĲ�����DialogInterface.OnClickListener������*/
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
		���ã��Լ�/ֵ����ʽ��intent�����б�������������͵����ݡ�
*/
		intent.putExtra(game.KEY_DIFFICULTY, which);
		startActivity(intent);
	}
}
