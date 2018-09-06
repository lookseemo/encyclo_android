package com.deedsit.android.bookworm.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.deedsit.android.bookworm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jack on 12/11/2017.
 */

public class ErrorDialog extends BaseDialog {

    @BindView(R.id.err_dialog_title)
    TextView dialogTitle;
    @BindView(R.id.err_dialog_message)
    TextView dialogMessage;
    @BindView(R.id.confirm_TV)
    TextView confirmTV;

    String tilte;
    String message;
    AlertDialog dialog;
    public static ErrorDialog newInstance(String title,String message) {
        ErrorDialog errorDialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString("MESSAGE", message);
        args.putString("TITLE", title);
        errorDialog.setArguments(args);
        return errorDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.error_dialog_layout,null);
        tilte = getArguments().getString("TITLE");
        message = getArguments().getString("MESSAGE");
        ButterKnife.bind(this,view);
        builder.setView(view);
        dialogTitle.setText(tilte);
        dialogMessage.setText(message);
        dialog = builder.create();
        return dialog;
    }

    @OnClick(R.id.confirm_TV)
    void onTVClick(){
        dialog.dismiss();
    }
}
