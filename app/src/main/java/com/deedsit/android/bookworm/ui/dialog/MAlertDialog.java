package com.deedsit.android.bookworm.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.component.JTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MAlertDialog extends BaseDialog implements View.OnClickListener {
    public  static final String TAG = "ALERT_DIALOG";
    @BindView(R.id.alert_dialog_title)
    TextView dialogTitle;
    @BindView(R.id.alert_dialog_message)
    TextView dialogMessage;

    private String title;
    private String message;
    private int layout;
    private ArrayList<JTextView> buttons;
    private AlertDialog dialog;

    public static MAlertDialog newInstance(String title, String message, int layout,
                                           ArrayList<JTextView> buttons){
        MAlertDialog alertDialog = new MAlertDialog();
        Bundle args = new Bundle();
        args.putString("MESSAGE", message);
        args.putString("TITLE", title);
        args.putInt("LAYOUT", layout);
        args.putSerializable("BUTTON_SET", buttons);
        alertDialog.setArguments(args);
        return alertDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        //data
        title = getArguments().getString("TITLE");
        message = getArguments().getString("MESSAGE");
        layout = getArguments().getInt("LAYOUT");
        buttons = (ArrayList<JTextView>) getArguments().getSerializable("BUTTON_SET");

        View view = layoutInflater.inflate(this.layout,null);
        builder.setView(view);

        ButterKnife.bind(this,view);

        dialogTitle.setText(title);
        dialogMessage.setText(message);

        dialog = builder.create();
        configureButtonSet(view);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        for(JTextView button : buttons){
            if(button.getId() == view.getId())
                button.getListener().onTextViewClicked(button);
        }
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    private void configureButtonSet(View view){
        for(int i = 0; i < buttons.size();i++){
            AppCompatTextView textView = view.findViewById(buttons.get(i).getId());
            textView.setText(buttons.get(i).getText());
            if(buttons.get(i).getVisibility() == View.GONE){
                textView.setVisibility(View.GONE);
            }else {
                textView.setVisibility(View.VISIBLE);
                textView.setOnClickListener(this);
            }
        }
    }
}
