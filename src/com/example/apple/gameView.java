
package com.example.apple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;

import android.os.Bundle;
import android.os.Parcelable;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class gameView extends View{
	private static final String TAG = "shudu";
	
	
    private static final String SELX = "selX"; 
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	private static final int ID = 42; 
	
	private float width;
	private float height;
	private int selX;
	private int selY;
	
	   /*Rect holds four integer coordinates for a rectangle.
	   The rectangle is represented by the coordinates of its 
	   4 edges (left, top, right bottom). These fields can be 
	   accessed directly. Use width() and height() to retrieve the
	   rectangle's width and height. Note: most methods do not check 
	   to see that the coordinates are sorted correctly (i.e. left <= right and top <= bottom).*/
	private final Rect selRect = new Rect();
	private final game shuduGame;
	
	/**gameView构造方法 */
	public gameView(Context context){
		
		/*public View (Context context): Simple constructor to use when creating a view from code.
		Parameters:The Context the view is running in, through which it can access 
		the current theme, resources, etc.
		 */
		super(context);
		this.shuduGame = (game)context; /*Activity类 、Service类 、Application类本质上都是Context子类，表明该实例在game中显示*/
		setFocusable(true);/**允许获得焦点,可以用鼠标选择*/
		setFocusableInTouchMode(true);/**允许触摸控制获取焦点时，可以用手指选择*/
	}
	
	protected Parcelable onSaveInstanceState() { 
		/**在函数里面保存一些View有用的数据到一个Parcelable对象并返回。
		在Activity的onSaveInstanceState(Bundle outState)中调用View的onSaveInstanceState ()，返回Parcelable对象，
　　		接着用Bundle的putParcelable方法保存在Bundle  savedInstanceState中。*/

		Parcelable p = super.onSaveInstanceState();
		
		Log.d(TAG, "onSaveInstanceState");
		Bundle bundle = new Bundle();
		bundle.putInt(SELX, selX);
		bundle.putInt(SELY, selY);
		
		/**public void putParcelable (String key, Parcelable value)
		Inserts a Parcelable value into the mapping of this Bundle, replacing any existing value for the given key.
 		Either key or value may be null.

		Parameters :key	a String, or null
		value:	a Parcelable object, or null

		 */
		bundle.putParcelable(VIEW_STATE, p);
		return bundle;
	}
	 
	protected void onRestoreInstanceState(Parcelable state) { 
		 Log.d(TAG, "onRestoreInstanceState");
		 Bundle bundle = (Bundle) state;
		 /**/
		 select(bundle.getInt(SELX), bundle.getInt(SELY));
		 super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
		 return;
	}
	   /**计算屏幕上每个单元格的大小*/
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged:width " + width + ", height" + height);

		/**当view的尺寸改变时被调用*/
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	/**OnDraw()函数每当窗口发生重绘时就会执行*/
	protected void onDraw(Canvas canvas){
		/*public Paint ()
		Create a new paint with default settings.*/
		Paint background = new Paint();
		
	
		background.setColor(getResources().getColor(R.color.puzzle_background));
		
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		/*public void drawLine (float startX, float startY, float stopX, float stopY, Paint paint)
		startX,startY : 从该坐标开始画线条
		stopX,stopY : 将线条画至该坐标
		注：以视图的左上角为坐标原点*/
		
		//draw the minor grid lines
		for (int i = 0; i < 9; i++){
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			canvas.drawLine(i*width, 0, i*width, getHeight(),light);
			canvas.drawLine(i*width+1, 0, i*width+1, getHeight(), hilite);
		}
		
		//draw the major grid lines
		for (int i=0; i < 9; i++){
			if (i%3 != 0)
				continue;
			canvas.drawLine(0, i*height, getWidth(), i*height, dark);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			canvas.drawLine(i*width, 0, i*width, getHeight(), dark);
			canvas.drawLine(i*width+1, 0, i*width+1, getHeight(), hilite);
		}
		
		/*Paint.ANTI_ALIAS_FLAG： 消除锯齿*/
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		
		/*【基本语法】public void setStyle ( Paint.Style style)

			其中，参数style为画笔的风格，为Paint.Style类型，可以取值如下。

			Style.FILL：实心。

			Style.FILL_AND_STROKE：同时实心和空心，该参数在某些场合会带来不可预期的显示效果。
		*/
		foreground.setStyle(Style.FILL);
		/*public void setTextSize (float textSize)
		Set the paint's text size. This value must be > 0

		Parameters
		textSize	set the paint's text size.*/
		foreground.setTextSize(height*0.75f);
		
		/*该方法用于设置画笔字体的比例因子，默认为1，当大于1的时候表示横向拉伸，当小于1的时候表示横向压缩。.*/
		foreground.setTextScaleX(width/height);
		
		/*设置字体对齐方式*/
		foreground.setTextAlign(Paint.Align.CENTER);
		
		//在方格中间画出数字
		FontMetrics fm = foreground.getFontMetrics();
		
		float x = width / 2;
		//centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		
		for(int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
			   /*在每个小方格中绘制数字，或空格*/	
				canvas.drawText(shuduGame.getTileString(i, j), i*width + x, j * height + y, foreground);
			}		
		}
		
			/*public final Context getContext ()
			Returns the context the view is running in, through which it can access the current theme, resources, etc.

			Returns
			The view's Context.*/
		/*如果获得hint是true，则运行该代码，该代码显示提示背景色*/
		if (settings.getHints(getContext())){
			Paint hint = new Paint();
			int c[] = {getResources().getColor(R.color.puzzle_hint_0), 
					getResources().getColor(R.color.puzzle_hint_1),
					getResources().getColor(R.color.puzzle_hint_2), };
			Rect r = new Rect();
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					int movesleft = 9 - shuduGame.getUsedTiles(i,j).length;
					if (movesleft < c.length){
						getRect(i, j, r);
						hint.setColor(c[movesleft]);
						canvas.drawRect(r, hint);
					}
				}
			}
		}
	Log.d(TAG, "selRect=" + selRect);
	Paint selected = new Paint();
	selected.setColor(getResources().getColor(R.color.puzzle_selected));
	/*public void drawRect (Rect r, Paint paint)
	Draw the specified Rect using the specified Paint. The rectangle will be filled or framed based on the Style in the paint.
	Parameters
	r	The rectangle to be drawn.
	paint	The paint used to draw the rectangle
	 */

	canvas.drawRect(selRect, selected);
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		select((int)(event.getX()/width), (int)(event.getY()/height));
		shuduGame.showKeypadOrError(selX, selY);
		Log.d(TAG, "onTouchEvent: x" + selX + ", y" + selY);
		return true;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event" + event);
	      switch (keyCode) {
	      case KeyEvent.KEYCODE_DPAD_UP:
	         select(selX, selY - 1);
	         break;
	      case KeyEvent.KEYCODE_DPAD_DOWN:
	         select(selX, selY + 1);
	         break;
	      case KeyEvent.KEYCODE_DPAD_LEFT:
	         select(selX - 1, selY);
	         break;
	      case KeyEvent.KEYCODE_DPAD_RIGHT:
	         select(selX + 1, selY);
	         break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE: setSelectedTile(0); break;
		case KeyEvent.KEYCODE_1: setSelectedTile(1); break;
		case KeyEvent.KEYCODE_2: setSelectedTile(2); break;
		case KeyEvent.KEYCODE_3: setSelectedTile(3); break;
		case KeyEvent.KEYCODE_4: setSelectedTile(4); break;
		case KeyEvent.KEYCODE_5: setSelectedTile(5); break;
		case KeyEvent.KEYCODE_6: setSelectedTile(6); break;
		case KeyEvent.KEYCODE_7: setSelectedTile(7); break;
		case KeyEvent.KEYCODE_8: setSelectedTile(8); break;
		case KeyEvent.KEYCODE_9: setSelectedTile(9); break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			shuduGame.showKeypadOrError(selX, selY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	/**　　select()方法两次调用了invalidate()方法，第一次调用通知Andorid选择矩形覆盖的区域需要重绘。
	 * 第二次调用通知Android新的选择区域也需要重绘。实际上这里并没有进行绘图操作。*/
	private void select(int x, int y){
		/**invalidate（Rect rect）刷新指定矩形区域*/
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}
	
	/*public void set (int left, int top, int right, int bottom)
Set the rectangle's coordinates to the specified values. Note: no range checking is performed, 
so it is up to the caller to ensure that left <= right and top <= bottom.

Parameters
left	The X coordinate of the left side of the rectangle
top	The Y coordinate of the top of the rectangle
right	The X coordinate of the right side of the rectangle
bottom	The Y coordinate of the bottom of the rectangle*/
	
	/**指定矩形四条标的横纵坐标，左边的X坐标，右边的X坐标，上边的Y坐标，下边的Y坐标*/
	private void getRect(int x, int y, Rect rect){
		rect.set((int)(x*width), (int)(y*height), (int)(x*width + width), (int)(y*height+height));
	}
	
/**setSelectedTile改变当前单元格中的数字，如果给的数字无效，则晃动手机（在电脑上测试的，没办法晃动电脑 - -！）*/
	public void setSelectedTile(int tile){
		if (shuduGame.setTileIfValid(selX, selY, tile)){
			/*刷新整个view*/
			invalidate();
		}else{
			Log.d(TAG, "setSelectedTile: invalid:" + tile);
			startAnimation(AnimationUtils.loadAnimation(shuduGame, R.anim.shake));
		}
	}

}
