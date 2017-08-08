package com.junjingit.propery.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.junjingit.propery.R;

/**
 * Created by jxy on 2017/8/8.
 */

public class TipDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Button cancle, sure;
    public ItipDialogListener itipDialogListener;

    public TipDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_follow);
        initViews();
    }

    private void initViews() {
        sure = findViewById(R.id.dialog_sure);
        cancle = findViewById(R.id.dialog_cancle);
        sure.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_sure:
                cancelTo();
                break;
            case R.id.dialog_cancle:
                dismiss();
                break;
        }
    }

    /**
     * 取消关注
     */
    public void cancelTo() {
        itipDialogListener.onCancel();
    }

    public interface ItipDialogListener {
        void onCancel();
    }

    public void settipDialogListener(ItipDialogListener itipDialogListener) {
        this.itipDialogListener = itipDialogListener;
    }
}
