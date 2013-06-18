
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
	
	/**gameView���췽�� */
	public gameView(Context context){
		
		/*public View (Context context): Simple constructor to use when creating a view from code.
		Parameters:The Context the view is running in, through which it can access 
		the current theme, resources, etc.
		 */
		super(context);
		this.shuduGame = (game)context; /*Activity�� ��Service�� ��Application�౾���϶���Context���࣬������ʵ����game����ʾ*/
		setFocusable(true);/**�����ý���,���������ѡ��*/
		setFocusableInTouchMode(true);/**���������ƻ�ȡ����ʱ����������ָѡ��*/
	}
	
	protected Parcelable onSaveInstanceState() { 
		/**�ں������汣��һЩView���õ����ݵ�һ��Parcelable���󲢷��ء�
		��Activity��onSaveInstanceState(Bundle outState)�е���View��onSaveInstanceState ()������Parcelable����
����		������Bundle��putParcelable����������Bundle  savedInstanceState�С�*/

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
	   /**������Ļ��ÿ����Ԫ��Ĵ�С*/
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged:width " + width + ", height" + height);

		/**��view�ĳߴ�ı�ʱ������*/
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	/**OnDraw()����ÿ�����ڷ����ػ�ʱ�ͻ�ִ��*/
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
		startX,startY : �Ӹ����꿪ʼ������
		stopX,stopY : ����������������
		ע������ͼ�����Ͻ�Ϊ����ԭ��*/
		
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
		
		/*Paint.ANTI_ALIAS_FLAG�� �������*/
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		
		/*�������﷨��public void setStyle ( Paint.Style style)

			���У�����styleΪ���ʵķ��ΪPaint.Style���ͣ�����ȡֵ���¡�

			Style.FILL��ʵ�ġ�

			Style.FILL_AND_STROKE��ͬʱʵ�ĺͿ��ģ��ò�����ĳЩ���ϻ��������Ԥ�ڵ���ʾЧ����
		*/
		foreground.setStyle(Style.FILL);
		/*public void setTextSize (float textSize)
		Set the paint's text size. This value must be > 0

		Parameters
		textSize	set the paint's text size.*/
		foreground.setTextSize(height*0.75f);
		
		/*�÷����������û�������ı������ӣ�Ĭ��Ϊ1��������1��ʱ���ʾ�������죬��С��1��ʱ���ʾ����ѹ����.*/
		foreground.setTextScaleX(width/height);
		
		/*����������뷽ʽ*/
		foreground.setTextAlign(Paint.Align.CENTER);
		
		//�ڷ����м仭������
		FontMetrics fm = foreground.getFontMetrics();
		
		float x = width / 2;
		//centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		
		for(int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
			   /*��ÿ��С�����л������֣���ո�*/	
				canvas.drawText(shuduGame.getTileString(i, j), i*width + x, j * height + y, foreground);
			}		
		}
		
			/*public final Context getContext ()
			Returns the context the view is running in, through which it can access the current theme, resources, etc.

			Returns
			The view's Context.*/
		/*������hint��true�������иô��룬�ô�����ʾ��ʾ����ɫ*/
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
	/**����select()�������ε�����invalidate()��������һ�ε���֪ͨAndoridѡ����θ��ǵ�������Ҫ�ػ档
	 * �ڶ��ε���֪ͨAndroid�µ�ѡ������Ҳ��Ҫ�ػ档ʵ�������ﲢû�н��л�ͼ������*/
	private void select(int x, int y){
		/**invalidate��Rect rect��ˢ��ָ����������*/
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
	
	/**ָ������������ĺ������꣬��ߵ�X���꣬�ұߵ�X���꣬�ϱߵ�Y���꣬�±ߵ�Y����*/
	private void getRect(int x, int y, Rect rect){
		rect.set((int)(x*width), (int)(y*height), (int)(x*width + width), (int)(y*height+height));
	}
	
/**setSelectedTile�ı䵱ǰ��Ԫ���е����֣��������������Ч����ζ��ֻ����ڵ����ϲ��Եģ�û�취�ζ����� - -����*/
	public void setSelectedTile(int tile){
		if (shuduGame.setTileIfValid(selX, selY, tile)){
			/*ˢ������view*/
			invalidate();
		}else{
			Log.d(TAG, "setSelectedTile: invalid:" + tile);
			startAnimation(AnimationUtils.loadAnimation(shuduGame, R.anim.shake));
		}
	}

}
