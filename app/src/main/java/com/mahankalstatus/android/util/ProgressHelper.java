package com.mahankalstatus.android.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;


import com.mahankalstatus.android.R;

public class ProgressHelper {

    private Dialog dialog;
    private Context context;

    public ProgressHelper(Context context) {
        this.context = context;
    }

    /**
     * Show progress dialog_dis when web service call
     */
    public void show() {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progess);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

        dialog.show();
    }

    /**
     * Dismiss progress dialog_dis
     */
    public void dismiss() {
        dialog.dismiss();
        //dialog_dis = null;
    }
}
