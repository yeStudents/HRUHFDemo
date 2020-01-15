package com.jf.uhf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.reader.helper.InventoryBuffer;
import com.reader.helper.InventoryBuffer.InventoryTagMap;
import com.reader.helper.ReaderHelper;
import com.jf.uhf.R;
import com.jf.uhf.tagpage.RealListAdapter;

public class TagRealList extends LinearLayout {
	private Context mContext;
	private TableRow mTagRealRow;
	private ImageView mTagRealImage;
	private TextView mListTextInfo;
	
	private TextView mTagsCountText, mTagsTotalText;
	private TextView  mTagsTimeText;

	
	private ReaderHelper mReaderHelper;
	
	private List<InventoryTagMap> data;
	private RealListAdapter mRealListAdapter;
	private ListView mTagRealList;
	
	private View mTagsRealListScrollView;
	
	private static InventoryBuffer m_curInventoryBuffer;
	
	private OnItemSelectedListener mOnItemSelectedListener;
	public interface OnItemSelectedListener {
		public void onItemSelected(View arg1, int arg2,
				long arg3);
	}
	
	public TagRealList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContext(context);
	}
	
	public TagRealList(Context context) {  
        super(context);
        initContext(context);
    }

	private void initContext(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.tag_real_list, this);
		
		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = new ArrayList<InventoryTagMap>();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		
		mTagsRealListScrollView = findViewById(R.id.tags_real_list_scroll_view);
		mTagsRealListScrollView.setVisibility(View.GONE);
		
		mTagRealRow = (TableRow) findViewById(R.id.table_row_tag_real);
		mTagRealImage = (ImageView) findViewById(R.id.image_prompt);
		mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
		mListTextInfo = (TextView) findViewById(R.id.list_text_info);
		mListTextInfo.setText(getResources().getString(R.string.open_tag_list));

		mTagsCountText = (TextView) findViewById(R.id.tags_count_text);
		mTagsTotalText = (TextView) findViewById(R.id.tags_total_text);
		mTagsTimeText = (TextView) findViewById(R.id.tags_time_text);


		
		mTagRealRow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mTagsRealListScrollView.getVisibility() != View.VISIBLE) {
					mTagsRealListScrollView.setVisibility(View.VISIBLE);
					mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.down));
					mListTextInfo.setText(getResources().getString(R.string.close_tag_list));
				} else {
					mTagsRealListScrollView.setVisibility(View.GONE);
					mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
					mListTextInfo.setText(getResources().getString(R.string.open_tag_list));
				}
			}
		});
		
		mTagRealList = (ListView) findViewById(R.id.tag_real_list_view);
		mRealListAdapter = new RealListAdapter(mContext, data);
		mTagRealList.setAdapter(mRealListAdapter);
		
		mTagRealList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (mOnItemSelectedListener != null)
					mOnItemSelectedListener.onItemSelected(arg1, arg2, arg3);
			}
			
		});
	}
	
	public void setOnItemSelectedListener(
			OnItemSelectedListener onItemSelectedListener) {
		mOnItemSelectedListener = onItemSelectedListener;
	}
	
	public final void clearText() {
		mTagsCountText.setText("0");
		mTagsTotalText.setText("0");
		mTagsTimeText.setText("0");
	}
	
	public final void refreshText() {
		mTagsCountText.setText(String.valueOf(m_curInventoryBuffer.lsTagList.size()));
		mTagsTotalText.setText(String.valueOf(mReaderHelper.getInventoryTotal()));
		mTagsTimeText.setText(String.valueOf(m_curInventoryBuffer.dtEndInventory.getTime() - m_curInventoryBuffer.dtStartInventory.getTime()));
	}

	public final void refreshList() {
		data.clear();
		data.addAll(m_curInventoryBuffer.lsTagList);
		mRealListAdapter.notifyDataSetChanged();
	}	
}
