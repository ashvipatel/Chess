package com.example.application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class PromotionDialogFragment extends DialogFragment {

    public static final String MESSAGE_KEY = "message_key";

    PromotionDialogListener listener;

    protected String promoteTo = "q";

    public interface PromotionDialogListener {
        public void onDialogClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle bundle = getArguments();
        /*ArrayList<String> choices = new ArrayList<String>();
        choices.add("Queen");
        choices.add("Rook");
        choices.add("Bishop");
        choices.add("Knight");*/
        String[] choices = {"Queen","Rook","Bishop","Knight"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(bundle.getString(MESSAGE_KEY))
                .setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        if(position == 0){
                            promoteTo = "q";
                        }else if(position == 1){
                            promoteTo = "r";
                        }else if(position == 2){
                            promoteTo = "b";
                        }else if(position == 3){
                            promoteTo = "n";
                        }else{
                            promoteTo = "q";
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        listener.onDialogClick(PromotionDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PromotionDialogListener) context;
        } catch (ClassCastException e) {
            System.out.println("Error with draw dialog");
        }
    }
}
