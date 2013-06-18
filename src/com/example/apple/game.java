
package com.example.apple;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class game extends Activity {
   private static final String TAG = "shudu";

   public static final String KEY_DIFFICULTY =
		      "com.example.apple.difficulty";
   
   private static final String PREF_PUZZLE = "puzzle" ;
   
   public static final int DIFFICULTY_EASY = 0;
   public static final int DIFFICULTY_MEDIUM = 1;
   public static final int DIFFICULTY_HARD = 2;
   
   protected static final int DIFFICULTY_CONTINUE = -1;
   

   private int puzzle[] = new int[9 * 9];

   private final String easyPuzzle =
      "360000000004230800000004200" +
      "070460003820000014500013020" +
      "001900000007048300000000045";
   private final String mediumPuzzle =
      "650000070000506000014000005" +
      "007009000002314700000700800" +
      "500000630000201000030000097";
   private final String hardPuzzle =
      "009000000080605020501078000" +
      "000000700706040102004000000" +
      "000720903090301080000000600";

   private gameView puzzleView;

   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate");
      
/**getIntExtra(key,intDefaultValue);
���ã���ȡ�����intent�����еļ���Ϊkey��int���͵����ݡ�����ȡ��������һ��Ĭ��ֵintDefaultValue��*/
      int diff = getIntent().getIntExtra(KEY_DIFFICULTY,
            DIFFICULTY_EASY);
      puzzle = getPuzzle(diff);
      calculateUsedTiles();

      puzzleView = new gameView(this);
      setContentView(puzzleView);
      puzzleView.requestFocus();

      
      // ...
      // �ô��뽫DIFFICULTY_CONTINUE��ֵѹ��KEY_DIFFICULTY������������Ϸ��ϵͳ��ѡ�������Ϸ
      getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
   }
	public boolean onCreateOptionsMenu(Menu menu){
		Log.d(TAG, "menu start");
		/*getMenuInflater()��������һ��MenuInflater���ʵ�������ڴ�xml�ļ��ж�ȡ�˵����壬
		 * ������ת��Ϊʵ�ʵ���ͼ*/
		getMenuInflater().inflate(R.menu.restart, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	public boolean onOptionsItemSelected(MenuItem item){
		Log.d(TAG, "menuitem start");
		super.onOptionsItemSelected(item);
		switch (item.getItemId()){
		case R.id.settings:
			startActivity(new Intent(this, settings.class));
			return true;
		case R.id.restart:
			Intent intent = new Intent(this, game.class);
			startActivity(intent);
			return true;
		}
		return false;
	}
   

   @Override
   protected void onResume() {
      super.onResume();
      music.play(this, R.raw.game);
   }

   
   @Override
   protected void onPause() {
      super.onPause();
      Log.d(TAG, "onPause");
      music.stop(this);

      // getPreferences(MODE_PRIVATE):��PRIVATE��ʽ��SharedPreferences����
      //edit(): ���SharedPreferences ��Editor����
      //putString: �޸Ķ��������ֵ
      //commit():���±����޸ĺ��ֵ
      getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE,
            toPuzzleString(puzzle)).commit();
   }
   
   
   
   /** ����intent����������Ϣѡ���Ѷ��б�ѡ����߼�����Ϸѡ�� */
   private int[] getPuzzle(int diff) {
      String puz;
      switch (diff) {
      case DIFFICULTY_CONTINUE:
         puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE,
               easyPuzzle);
         break;
         // ...
         
      case DIFFICULTY_HARD:
         puz = hardPuzzle;
         break;
      case DIFFICULTY_MEDIUM:
         puz = mediumPuzzle;
         break;
      case DIFFICULTY_EASY:
      default:
         puz = easyPuzzle;
         break;
         
      }
      /**���ַ���puzת�������*/
      return fromPuzzleString(puz);
   }
   

   /** ������ת��Ϊ�ַ��� */
   static private String toPuzzleString(int[] puz) {
      StringBuilder buf = new StringBuilder();
      for (int element : puz) {
         buf.append(element);
      }
      return buf.toString();
   }

   /** ���ַ���ת��Ϊ����*/
   static protected int[] fromPuzzleString(String string) {
      int[] puz = new int[string.length()];
      for (int i = 0; i < puz.length; i++) {
         puz[i] = string.charAt(i) - '0';
      }
      return puz;
   }

   /**���ظ������꣨x,y����Ӧ����puzzle�е�ֵ */
   private int getTile(int x, int y) {
      return puzzle[y * 9 + x];
   }

   /**���������꣨x,y����Ӧpuzzle�����е�ֵ����Ϊvalue */
   private void setTile(int x, int y, int value) {
      puzzle[y * 9 + x] = value;
   }

   /** ��getTile���ص�ֵת��Ϊ�ַ������أ������õ�������0���򷵻�""*/
   protected String getTileString(int x, int y) {
      int v = getTile(x, y);
      if (v == 0)
         return "";
      else
    	  /**String.valueOf(int i) : �� int ���� i ת�����ַ��� ���÷��������غ�����֧�ֲ�ͬ������ת�����ַ���*/
         return String.valueOf(v);
   }

   /** ����һ��x��y�����Լ�һ����Ԫ�����ֵ��ֻ�и����ֶԸ�����λ�õĵ�Ԫ����Ч��
    * setTileIfValid()�����Ž��������趨Ϊ�õ�Ԫ�����ֵ */
   protected boolean setTileIfValid(int x, int y, int value) {
      int tiles[] = getUsedTiles(x, y);
      if (value != 0) {
         for (int tile : tiles) {
            if (tile == value)
               return false;
         }
      }
      /**����0������Ч���������뵽���������У�calculateUsedTiles()��ȥ�����������е�0*/
      setTile(x, y, value);
      /**�����µ�ֵ����81������������*/
      calculateUsedTiles();
      return true;
   }

   /** ����շ���ʱ�������������δʹ������ʾkeypad��ʾ����������֣�������ʾ������ʾ */
   protected void showKeypadOrError(int x, int y) {
      int tiles[] = getUsedTiles(x, y);
      if (tiles.length == 9) {
      Toast toast = Toast.makeText(this,
               R.string.no_moves_label, Toast.LENGTH_SHORT);
         toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
      } else {
         Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
         Dialog v = new prompt(this, tiles, puzzleView);
         v.show();
      }
   }

   /**��ά���飬ǰ��λ��¼���꣬���һλ��¼�����Ӧ���ݣ��ᣬ����Ϊ0������ */
   private final int used[][][] = new int[9][9][];

   /** ��ȡ��Ӧ����� �ᣬ�ݣ������в�Ϊ0�����ֻ����б�*/
   protected int[] getUsedTiles(int x, int y) {
      return used[x][y];
   }

   /**��ÿ������ĺᣬ�ݣ������в���0�Ļ������������ά����used�С�used��81������
    * ��ÿ�������¼��һ������ĺᣬ�ݣ������в�Ϊ0������*/
   private void calculateUsedTiles() {
      for (int x = 0; x < 9; x++) {
         for (int y = 0; y < 9; y++) {
            used[x][y] = calculateUsedTiles(x, y);
            // Log.d(TAG, "used[" + x + "][" + y + "] = "
            // + toPuzzleString(used[x][y]));
         }
      }
   }

   /** ����calculateUsedTiles(int, int )�����Ǹ������������£������򣬺����Լ���Ӧ��������
    * ������0�����ִ���һ���������飬 */
   private int[] calculateUsedTiles(int x, int y) {
      int c[] = new int[9];
      // ���������ִ��뻺��������
      for (int i = 0; i < 9; i++) {
         if (i == y)
            continue;
         int t = getTile(x, i);
         if (t != 0)
            c[t - 1] = t;
      }
      // ���Ժ����Ƿ�����ͬ������
      for (int i = 0; i < 9; i++) {
         if (i == x)
            continue;
         int t = getTile(i, y);
         if (t != 0)
            c[t - 1] = t;
      }
      // same cell block
      int startx = (x / 3) * 3;
      int starty = (y / 3) * 3;
      for (int i = startx; i < startx + 3; i++) {
         for (int j = starty; j < starty + 3; j++) {
            if (i == x && j == y)
               continue;
            int t = getTile(i, j);
            if (t != 0)
               c[t - 1] = t;
         }
      }
      // ����Ϊ�������ѹ����һ�����������У��Ż����Ч��
      int nused = 0;
      for (int t : c) {
         if (t != 0)
            nused++;
      }
      int c1[] = new int[nused];
      nused = 0;
      for (int t : c) {
         if (t != 0)
            c1[nused++] = t;
      }
      return c1;
   }
   
   
}
