package com.touchdown.app.smartassistant.views;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;

import com.touchdown.app.smartassistant.R;
import com.touchdown.app.smartassistant.services.Common;

/**
 * Created by Pete on 8.9.2014.
 */
public abstract class ActionFragment extends Fragment {
    public static final String LOG_TAG = ActionFragment.class.getSimpleName();

    protected OnActionFragmentInteractionListener mListener;

    protected void changeFrameColor(int color, int container){
        GradientDrawable bg = (GradientDrawable) getActivity().findViewById(container).getBackground().mutate();    //mutate so that we only affect one frame
        bg.setStroke((int) (getResources().getDimension(R.dimen.frame_stroke)), color);
    }

    protected abstract void setUpCompoundButton();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnActionFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnActionFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
