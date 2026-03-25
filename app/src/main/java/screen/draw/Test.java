package screen.draw;

import java.util.ArrayList;

import screen.draw.CirclesDrawingView.CircleArea;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

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

		if(yon == false) {
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
		ArrayList<CircleArea> ca = asdf.getList();
		centerx.clear();
		centery.clear();
		radius.clear();
		for(CircleArea c : ca) {
			centerx.add(c.centerX);
			centery.add(c.centerY);
			radius.add(c.radius);
		}

		Intent i = new Intent(Test.this, FingerPaintActivity.class);
		i.putIntegerArrayListExtra("x", centerx);
		i.putIntegerArrayListExtra("y", centery);
		i.putIntegerArrayListExtra("radius", radius);
		setResult(Activity.RESULT_OK, i);

		Test.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
}
