package com.vvasilyev.tutu.ui;

import android.content.Context;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.vvasilyev.tutu.R;


/**
 *  Toast manager. Adds abstraction layer
 */
public class ToastManager {

    private SuperToast toast;

    public void makeToast(String text, Context context) {
        if (toast != null) {
            toast.dismiss();
        }

        toast = SuperActivityToast.create(context, new Style(), Style.TYPE_STANDARD)
                .setText(text)
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.DARK_GREY))
                .setAnimations(Style.ANIMATIONS_POP);

        toast.show();
    }

    private void makeToast(int resId, Context context) {
        makeToast(context.getResources().getString(resId), context);
    }

    public void makeSelectDepartingToast(Context context) {
        makeToast(R.string.select_departing, context);
    }

    public void makeSelectDestinationToast(Context context) {
        makeToast(R.string.select_destination, context);
    }

    public void makeSelectDifferentToast(Context context) {
        makeToast(R.string.departing_and_destination_should_differ, context);
    }
}
