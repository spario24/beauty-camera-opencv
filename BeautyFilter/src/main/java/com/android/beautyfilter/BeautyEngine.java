package com.android.beautyfilter;

import com.android.beautyfilter.filter.helper.BeautyFilterType;
import com.android.beautyfilter.helper.SavePictureTask;
import com.android.beautyfilter.util.BeautyParams;
import com.android.beautyfilter.widget.BeautyCameraView;
import com.android.beautyfilter.widget.base.BeautyBaseView;

import java.io.File;

/**
 * @author xufulong
 * @date 2022/6/24 8:47 上午
 * @desc
 */
public class BeautyEngine {

    public BeautyEngine(BeautyBaseView baseView) {
        BeautyParams.context = baseView.getContext();
        BeautyParams.beautyBaseView = baseView;
    }

    public void setFilter(BeautyFilterType type) {
        BeautyParams.beautyBaseView.setFilter(type);
    }

    public void startRecord() {
        if (BeautyParams.beautyBaseView instanceof BeautyCameraView) {
            ((BeautyCameraView) BeautyParams.beautyBaseView).changeRecordingState(true);
        }
    }

    public void stopRecord() {
        if (BeautyParams.beautyBaseView instanceof BeautyCameraView) {
            ((BeautyCameraView) BeautyParams.beautyBaseView).changeRecordingState(false);
        }
    }

    public void setBeautyLevel(int level) {
        if (BeautyParams.beautyBaseView instanceof BeautyCameraView && BeautyParams.beautyLevel != level) {
            BeautyParams.beautyLevel = level;
            ((BeautyCameraView) BeautyParams.beautyBaseView).onBeautyLevelChanged();
        }
    }

    public void savePicture(File file, SavePictureTask.OnPictureSavedListener listener) {
        SavePictureTask task = new SavePictureTask(file, listener);
        BeautyParams.beautyBaseView.savePicture(task);
    }

}
