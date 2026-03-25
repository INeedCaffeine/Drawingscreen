package screen.draw;

import java.util.ArrayList;
import java.util.HashSet;

import screen.draw.CirclesDrawingView.CircleArea;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;

public class Test extends Activity {
	
	
	CirclesDrawingView asdf;
	ArrayList<Integer> centerx;
	ArrayList<Integer> centery;
	ArrayList<Integer> radius;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		boolean yon = getIntent().getBooleanExtra("YES", false);
		
		
		
		asdf = (CirclesDrawingView) findViewById(R.id.circlesDrawingView1);
		
		
		
		if(yon==false) {
			
			centerx = getIntent().getIntegerArrayListExtra("x");
			centery = getIntent().getIntegerArrayListExtra("y");
			radius = getIntent().getIntegerArrayListExtra("radius");
			asdf.setEvery(centerx, centery, radius);
			
		} else {
		
		
			centerx = new ArrayList<Integer>();
			centery = new ArrayList<Integer>();
			radius = new ArrayList<Integer>();
			
		
		}
		
		
		
		
		
	
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ArrayList<CircleArea> ca = asdf.getList1();
		centerx.clear();
		centery.clear();
		radius.clear();
		for(CircleArea c : ca) {
			centerx.add(c.centerX);
			centery.add(c.centerY);
			radius.add(c.radius);
		}
		
		
		/*getIntent().putIntegerArrayListExtra("x", centerx);
		getIntent().putIntegerArrayListExtra("y", centery);
		getIntent().putIntegerArrayListExtra("radius", radius);
		*/
		Intent i = new Intent(Test.this, FingerPaintActivity.class);
		//Intent i = new Intent();
		i.putIntegerArrayListExtra("x", centerx);
		i.putIntegerArrayListExtra("y", centery);
		i.putIntegerArrayListExtra("radius", radius);
		setResult(Activity.RESULT_OK, i);
		//startActivityFromChild(getParent(), i, RESULT_OK);
		
		Test.this.finish();
		
		/*Intent i = new Intent(Test.this, FingerPaintActivity.class);
		i.putIntegerArrayListExtra("x", centerx);
		i.putIntegerArrayListExtra("y", centery);
		i.putIntegerArrayListExtra("radius", radius);
		// Activity finished ok, return the data
		startActivity(i);
		//setResult(RESULT_OK, i);
		finish();*/
		
		/*Intent i = new Intent(Test.this, FingerPaintActivity.class);
		
		startActivity(i);
		finish();*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
