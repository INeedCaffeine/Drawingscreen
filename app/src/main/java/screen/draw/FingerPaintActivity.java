package screen.draw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;



import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

public class FingerPaintActivity extends Activity implements
		ColorPickerDialog.OnColorChangedListener {

	public MyView mv;
	AlertDialog dialog;
	boolean saved = false;

	public static final int fieldPicture = R.drawable.rebuilt2026;
	
	
	boolean device = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				try {
			if(ViewConfiguration.get(this).hasPermanentMenuKey()) {
				getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
				getActionBar().hide();
				device = false;
			} else {
				device = true;
			}
		} catch(Error e) {
			
		}
		
		
		mv = new MyView(this);
		mv.setDrawingCacheEnabled(true);
		mv.setBackgroundResource(fieldPicture);
		setContentView(mv);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);		
		
		
		
		

	}
	AlertDialog asdf;
	 @Override
	 public void onBackPressed() {
					if(saved) {
					this.finish();
				} else if(!saved) {
					 asdf = new AlertDialog.Builder(this)
				    .setTitle("Quit Without Saving")
				    .setMessage("Are you sure you want to exit without saving your work?")
				    .setPositiveButton("No", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // continue with delete
				        	asdf.dismiss();
				        	
				        }
				     })
				    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // do nothing
				        	FingerPaintActivity.this.finish();
				        }
				     }).show();
				}
		}


	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	public void colorChanged(int color) {
		mPaint.setColor(color);
	}

	public class MyView extends View {

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		Context context;

		public MyView(Context c) {
			super(c);
			context = c;
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);

		}

		public void clear(int w, int h) {

			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(Color.BLUE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(20);
			invalidate();

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			
			canvas.drawPath(mPath, mPaint);
			
			
		}
		
		public boolean pressed = false;
		
		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			// showDialog();
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;

		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
			if(Q) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mPaint.setAlpha(0x80);
				mPaint.setStrokeWidth(40);
			} else {
				mPaint.setXfermode(null);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:

				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int CLEAR_MENU_ID = Menu.FIRST + 4;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 5;
	private static final int Save = Menu.FIRST + 6;
	private static final int Add = Menu.FIRST + 7;
	private static final int ABOUT = Menu.FIRST + 8;
	private static final int NEXT = Menu.FIRST + 9;
    private static final int SCREEN = Menu.FIRST + 10;
    AlertDialog asdf1;
	boolean Q = false;
	
	private void deactivateEraser(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);
		mPaint.setStrokeWidth(20);
		if (item != null) item.setChecked(false);
		Q = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z').setCheckable(true);
		menu.add(0, CLEAR_MENU_ID, 0, "Clear").setShortcut('5', 'z');
		menu.add(0, Save, 0, "Save").setShortcut('5', 'z');
		menu.add(0, NEXT, 0, "Add Robots").setShortcut('3', 'c');
		menu.add(0, ABOUT, 0, "About").setShortcut('5', 'z');
		/*if(device) {
			menu.add(0,R.menu.finger_paint,0,"Red/White/Blue").setShortcut('5', 'z');
		}*/
		return true;
		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	
	ArrayList<Integer> centerx = new ArrayList<Integer>();
	ArrayList<Integer> centery = new ArrayList<Integer>();
	ArrayList<Integer> radius = new ArrayList<Integer>();
	
	boolean work = true;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			mPaint.setStrokeWidth(20);
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
				item.setChecked(true);
			} else {
				mPaint.setMaskFilter(null);
				item.setChecked(false);
			}
			mPaint.setStrokeWidth(20);
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
				item.setChecked(true);
			} else {
				mPaint.setMaskFilter(null);
				item.setChecked(false);
			}
			mPaint.setStrokeWidth(20);
			return true;
		case ERASE_MENU_ID:
			if (!Q) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mPaint.setAlpha(0x80);
				mPaint.setStrokeWidth(40);
				item.setChecked(true);
				Q = true;
			} else {
				deactivateEraser(item);
			}
			return true;
		case CLEAR_MENU_ID:
			mv.clear(mv.getWidth(), mv.getHeight());
			
			centerx.clear();
			centery.clear();
			radius.clear();
			
			return true;
		case Add:
			mv.invalidate();
			return true;
		case ABOUT:
			Intent i = new Intent(FingerPaintActivity.this, About.class);
			startActivity(i);
		return true;
		case NEXT:
			Paint clearPaint = new Paint();
			clearPaint.setStyle(Paint.Style.FILL);
			clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			int w1 = 0;
			while(w1<=centerx.size()-1) {
				mv.mCanvas.drawCircle(centerx.get(w1), centery.get(w1), 30, clearPaint);
				w1++;
			}
			
			mv.invalidate();
		
			
			
			
			if(work) {
			
				Intent j = new Intent(FingerPaintActivity.this,Test.class);  
				j.putExtra("YES", work);
					work = false;
				startActivityForResult(j, 1);
				
			} else if(!work) {
				Intent j = new Intent(FingerPaintActivity.this,Test.class);    
				j.putIntegerArrayListExtra("x", centerx);
				j.putIntegerArrayListExtra("y", centery);
				j.putIntegerArrayListExtra("radius", radius);
				j.putExtra("YES", work);
				
				centerx.clear();
				centery.clear();
				radius.clear();
				startActivityForResult(j, 1);
				
			}
		return true;
		case R.id.bluecol:
			deactivateEraser(item);
			mPaint.setColor(Color.BLUE);
		return true;
		case R.id.redcol:
			deactivateEraser(item);
			mPaint.setColor(Color.RED);
		return true;
		case R.id.whitecol:
			deactivateEraser(item);
			mPaint.setColor(Color.WHITE);
		return true;
		case R.id.erase:
			if(!Q) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				mPaint.setAlpha(0x80);
				mPaint.setStrokeWidth(40);
				item.setChecked(true);
				Q = true;
			} else {
				deactivateEraser(item);
			}
		return true;
		case Save:
			saved = true;
			AlertDialog.Builder editalert = new AlertDialog.Builder(
					FingerPaintActivity.this);
			editalert.setTitle("Please Enter the name with which you want to Save");
			final EditText input = new EditText(FingerPaintActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			input.setLayoutParams(lp);
			editalert.setView(input);
			editalert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							
							String name = input.getText().toString();
							
							
							 mv.setDrawingCacheEnabled(true);
						    Bitmap bitmap = mv.getDrawingCache();
						    ContentValues values = new ContentValues();
						    values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
						    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
						    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
						    Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
						    try
						    {
						        OutputStream ostream = getContentResolver().openOutputStream(imageUri);
						        bitmap.compress(CompressFormat.PNG, 100, ostream);
						        ostream.close();
						    }
						    catch (Exception e)
						    {
						        e.printStackTrace();
						    }
							
													
						
						
						}
					});

			editalert.show();
			return true;
           case SCREEN:

               asdf1 = new AlertDialog.Builder(this)
               .setTitle("What screen do you want?")
               .setMessage("Choose a screen")
               .setPositiveButton("Red Side", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       mv = new MyView(FingerPaintActivity.this);
                       mv.setDrawingCacheEnabled(true);
                       mv.setBackgroundResource(fieldPicture);
                       // / set the back
                       // ground if you
                       // wish to
                       setContentView(mv);
                       mPaint = new Paint();
                       mPaint.setAntiAlias(true);
                       mPaint.setDither(true);
                       mPaint.setColor(Color.BLUE);
                       mPaint.setStyle(Paint.Style.STROKE);
                       mPaint.setStrokeJoin(Paint.Join.ROUND);
                       mPaint.setStrokeCap(Paint.Cap.ROUND);
                       mPaint.setStrokeWidth(20);
                       mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
                       mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
                   }
               })
               .setNeutralButton("Full Field", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       mv = new MyView(FingerPaintActivity.this);
                       mv.setDrawingCacheEnabled(true);
                       mv.setBackgroundResource(fieldPicture);
                       // set the back
                       // ground if you
                       // wish to
                       setContentView(mv);
                       mPaint = new Paint();
                       mPaint.setAntiAlias(true);
                       mPaint.setDither(true);
                       mPaint.setColor(Color.BLUE);
                       mPaint.setStyle(Paint.Style.STROKE);
                       mPaint.setStrokeJoin(Paint.Join.ROUND);
                       mPaint.setStrokeCap(Paint.Cap.ROUND);
                       mPaint.setStrokeWidth(20);
                       mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
                       mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
                   }
                })
               .setNegativeButton("Blue Side", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       mv = new MyView(FingerPaintActivity.this);
                       mv.setDrawingCacheEnabled(true);
                       mv.setBackgroundResource(R.drawable.recyclerushfieldhalf2point0);
                       // set the back
                       // ground if you
                       // wish to
                       setContentView(mv);
                       mPaint = new Paint();
                       mPaint.setAntiAlias(true);
                       mPaint.setDither(true);
                       mPaint.setColor(Color.BLUE);
                       mPaint.setStyle(Paint.Style.STROKE);
                       mPaint.setStrokeJoin(Paint.Join.ROUND);
                       mPaint.setStrokeCap(Paint.Cap.ROUND);
                       mPaint.setStrokeWidth(20);
                       mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
                       mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
                   }

               }).show();

           return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void captureScreen() {
		
		// image naming and path  to include sd card  appending name you choose for file
		
		
		// create bitmap screen capture
		Bitmap bitmap;
		View v1 = mv.getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		
		String mPath = Environment.getExternalStorageDirectory().toString() + "/" + bitmap;   
		Uri uri = Uri.fromFile(new File(mPath));

		OutputStream fout = null;
		File imageFile = new File(mPath);

		try {
		    fout = new FileOutputStream(imageFile);
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
		    fout.flush();
		    fout.close();

		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  
	  int w = 0;
	  
	  switch(requestCode) { 
	    case (1) : { 
	    	  	centerx = data.getIntegerArrayListExtra("x");
				centery = data.getIntegerArrayListExtra("y");
				radius = data.getIntegerArrayListExtra("radius");
				
				
				w = 0;
				Paint robotPaint = new Paint();
				robotPaint.setAntiAlias(true);
				robotPaint.setStyle(Paint.Style.FILL);
				while(w<=centerx.size()-1) {
					if(w>=3) {
						robotPaint.setColor(Color.RED);
					} else {
						robotPaint.setColor(Color.BLUE);
					}
					mv.mCanvas.drawCircle(centerx.get(w), centery.get(w), 30, robotPaint);
					w++;
				}
				
				mv.invalidate();
	      break; 
	    } 
	    }
	}
	
}