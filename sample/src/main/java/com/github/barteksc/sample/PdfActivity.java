package com.github.barteksc.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.barteksc.sample.adapter.FlipAdapter;

import se.emilsjolander.flipview.FlipView;
import se.emilsjolander.flipview.FlipView.OnFlipListener;
import se.emilsjolander.flipview.FlipView.OnOverFlipListener;
import se.emilsjolander.flipview.OverFlipMode;


public class PdfActivity extends Activity implements FlipAdapter.Callback, OnFlipListener, OnOverFlipListener {
    private MyApplication singleton;
	private FlipView mFlipView;
	private FlipAdapter mAdapter;
    private int pdfindex;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdf_main);
        this.singleton = MyApplication.getInstance();

        Intent intent=getIntent();
        pdfindex=intent.getExtras().getInt("pdfindex");

		mFlipView = (FlipView) findViewById(R.id.flip_view);
		mAdapter = new FlipAdapter(this,pdfindex);
		int pageCount = singleton.urls.get(pdfindex).getPages();
		int i = singleton.urls.get(pdfindex).getPages()%2;
		Log.d("111", "2333333 pageCount = " + pageCount + ", iii v= "+(pageCount/2 + i));
        mAdapter.addItems(pageCount/2 + i);
		mAdapter.setCallback(this);
		mFlipView.setAdapter(mAdapter);
		mFlipView.setOnFlipListener(this);
		mFlipView.peakNext(false);
		mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
		mFlipView.setEmptyView(findViewById(R.id.empty_view));
		mFlipView.setOnOverFlipListener(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.prepend:
			mAdapter.addItemsBefore(5);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageRequested(int page) {
		mFlipView.smoothFlipTo(page);
	}

	@Override
	public void onFlippedToPage(FlipView v, int position, long id) {
		Log.i("pageflip", "Page: "+position);
//		if(position > mFlipView.getPageCount()-3 && mFlipView.getPageCount()<30){
//			mAdapter.addItems(5);
//		}
	}

	@Override
	public void onOverFlip(FlipView v, OverFlipMode mode,
			boolean overFlippingPrevious, float overFlipDistance,
			float flipDistancePerPage) {
		Log.i("overflip", "overFlipDistance = "+overFlipDistance);
	}

}
