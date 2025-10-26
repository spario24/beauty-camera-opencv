package com.android.camera.beauty.edit.filter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.beautyfilter.display.BeautyImageDisplay;
import com.android.beautyfilter.filter.helper.BeautyFilterType;
import com.android.camera2.R;
import com.android.camera.beauty.edit.FilterLayoutUtil;
import com.android.camera.beauty.edit.ImageEditFragment;

@SuppressLint("ValidFragment")
public class ImageEditFilterView extends ImageEditFragment {
	
	private FilterLayoutUtil mFilterLayoutUtil;
	
	public ImageEditFilterView(Context context, BeautyImageDisplay beautyDisplay) {
		super(context, beautyDisplay);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_image_edit_filter, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mFilterLayoutUtil = new FilterLayoutUtil(getActivity(), mBeautyDisplay);
		mFilterLayoutUtil.init(getView());
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if(!hidden)
			mFilterLayoutUtil.init(getView());
	}

	@Override
	protected boolean isChanged() {
		return mFilterLayoutUtil.getFilterType() != BeautyFilterType.NONE;
	}

}
