package com.jianglei.videoplay.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.jianglei.videoplay.DataInter;
import com.jianglei.videoplay.R;
import com.kk.taurus.playerbase.receiver.BaseCover;

public class CloseCover extends BaseCover {

    ImageView mCloseIcon;


    public CloseCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
    }

    public void onViewClick(View view) {
        notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_CLOSE, null);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
    }

    @Override
    public View onCreateCoverView(Context context) {
        View view = View.inflate(context, R.layout.layout_close_cover, null);
        mCloseIcon = view.findViewById(R.id.iv_close);
        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(view);
            }
        });
        return view;
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public int getCoverLevel() {
        return levelMedium(10);
    }
}
