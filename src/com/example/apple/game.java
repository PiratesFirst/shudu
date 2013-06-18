
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
作用：获取存放在intent对象中的键名为key的int类型的数据。若获取不到，则赋一个默认值intDefaultValue。*/
      int diff = getIntent().getIntExtra(KEY_DIFFICULTY,
            DIFFICULTY_EASY);
      puzzle = getPuzzle(diff);
      calculateUsedTiles();

      puzzleView = new gameView(this);
      setContentView(puzzleView);
      puzzleView.requestFocus();

      
      // ...
      // 该代码将DIFFICULTY_CONTINUE的值压入KEY_DIFFICULTY，重新启动游戏后系统会选择继续游戏
      getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
   }
	public boolean onCreateOptionsMenu(Menu menu){
		Log.d(TAG, "menu start");
		/*getMenuInflater()方法返回一个MenuInflater类的实例，用于从xml文件中读取菜单定义，
		 * 并将其转换为实际的视图*/
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

      // getPreferences(MODE_PRIVATE):以PRIVATE方式打开SharedPreferences对象，
      //edit(): 获得SharedPreferences 的Editor对象
      //putString: 修改对象里面的值
      //commit():更新保存修改后的值
      getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE,
            toPuzzleString(puzzle)).commit();
   }
   
   
   
   /** 根据intent传过来的信息选择难度列表选项，或者继续游戏选项 */
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
      /**将字符串puz转变成数组*/
      return fromPuzzleString(puz);
   }
   

   /** 将数组转换为字符串 */
   static private String toPuzzleString(int[] puz) {
      StringBuilder buf = new StringBuilder();
      for (int element : puz) {
         buf.append(element);
      }
      return buf.toString();
   }

   /** 将字符串转换为数组*/
   static protected int[] fromPuzzleString(String string) {
      int[] puz = new int[string.length()];
      for (int i = 0; i < puz.length; i++) {
         puz[i] = string.charAt(i) - '0';
      }
      return puz;
   }

   /**返回给定坐标（x,y）对应数组puzzle中的值 */
   private int getTile(int x, int y) {
      return puzzle[y * 9 + x];
   }

   /**将给定坐标（x,y）对应puzzle数组中的值更新为value */
   private void setTile(int x, int y, int value) {
      puzzle[y * 9 + x] = value;
   }

   /** 将getTile返回的值转换为字符串返回，如果获得的数字是0，则返回""*/
   protected String getTileString(int x, int y) {
      int v = getTile(x, y);
      if (v == 0)
         return "";
      else
    	  /**String.valueOf(int i) : 将 int 变量 i 转换成字符串 ，该方法是重载函数，支持不同的类型转换成字符串*/
         return String.valueOf(v);
   }

   /** 给定一组x和y坐标以及一个单元格的新值，只有该数字对该坐标位置的单元格有效，
    * setTileIfValid()方法才将该数字设定为该单元格的新值 */
   protected boolean setTileIfValid(int x, int y, int value) {
      int tiles[] = getUsedTiles(x, y);
      if (value != 0) {
         for (int tile : tiles) {
            if (tile == value)
               return false;
         }
      }
      /**包括0在内有效的数字输入到缓存数组中，calculateUsedTiles()会去掉缓存数组中的0*/
      setTile(x, y, value);
      /**将更新的值存入81个缓存数组中*/
      calculateUsedTiles();
      return true;
   }

   /** 点击空方块时，如果还有数字未使用则显示keypad提示可输入的数字，否则显示错误提示 */
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

   /**三维数组，前两位记录坐标，最后一位记录坐标对应的纵，横，宫不为0的数字 */
   private final int used[][][] = new int[9][9][];

   /** 提取对应坐标的 横，纵，宫所有不为0的数字缓存列表*/
   protected int[] getUsedTiles(int x, int y) {
      return used[x][y];
   }

   /**将每个坐标的横，纵，宫所有不是0的缓存数组存入三维数组used中。used有81个数组
    * ，每个数组记录了一个坐标的横，纵，宫所有不为0的数字*/
   private void calculateUsedTiles() {
      for (int x = 0; x < 9; x++) {
         for (int y = 0; y < 9; y++) {
            used[x][y] = calculateUsedTiles(x, y);
            // Log.d(TAG, "used[" + x + "][" + y + "] = "
            // + toPuzzleString(used[x][y]));
         }
      }
   }

   /** 　　calculateUsedTiles(int, int )方法是给定坐标的情况下，将纵向，横向，以及对应宫的所有
    * 不等于0的数字存入一个缓存数组， */
   private int[] calculateUsedTiles(int x, int y) {
      int c[] = new int[9];
      // 将纵向数字存入缓存数组中
      for (int i = 0; i < 9; i++) {
         if (i == y)
            continue;
         int t = getTile(x, i);
         if (t != 0)
            c[t - 1] = t;
      }
      // 测试横向是否有相同的数字
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
      // 将不为零的数字压入另一个缓存数组中，优化检测效率
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
