package com.grofers.skthinks.my2048.View;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grofers.skthinks.my2048.Interfaces.ITwoButtonDialogListener;
import com.grofers.skthinks.my2048.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Skthinks on 24/10/16.
 */

public class DialogFrag extends android.support.v4.app.DialogFragment {

    private Unbinder unbinder;
    private ITwoButtonDialogListener iTwoButtonDialogListener;

    public static DialogFrag newInstance(
            int dialogId,
            Parcelable parcelable,
            String title,
            String content,
            String buttonYes,
            String buttonNo) {

        DialogFrag dialogFrag = new DialogFrag();
        Bundle bundle = new Bundle();
        return dialogFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        try {
            iTwoButtonDialogListener = (ITwoButtonDialogListener) context;
        } catch (ClassCastException c) {
            Log.e(getClass().getSimpleName(), c.getMessage());
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick({R.id.button_dialogtwobutton_no, R.id.button_dialogtwobutton_yes})
    public void onClick(View view) {

        if (iTwoButtonDialogListener == null) {
            iTwoButtonDialogListener = (ITwoButtonDialogListener) getTargetFragment();
        }

        switch (view.getId()) {
            case R.id.button_dialogtwobutton_no:
                dismiss();
                iTwoButtonDialogListener.onClickNo();
                break;
            case R.id.button_dialogtwobutton_yes:
                dismiss();
                Bundle bundle = new Bundle();
                iTwoButtonDialogListener.onClickYes(bundle);
                break;
        }
    }

    public void setBackground(){
        LayerDrawable drawable = (LayerDrawable)getActivity().getResources().getDrawable(R.drawable.pointy_border);
        //drawable.setLayerInset(1,);
        drawable.mutate();
    }
}
