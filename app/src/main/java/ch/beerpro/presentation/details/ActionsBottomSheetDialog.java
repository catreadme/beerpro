package ch.beerpro.presentation.details;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ch.beerpro.R;
import ch.beerpro.presentation.details.actions.AddToFridgeDialog;
import ch.beerpro.presentation.profile.myfridge.FridgeActivity;


public class ActionsBottomSheetDialog extends BottomSheetDialogFragment {
    Bundle arguments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_bottom_sheet_dialog, container, false);

        Button addToFridgeButton = view.findViewById(R.id.addToFridge);
        addToFridgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToFridgeDialog addToFridgeDialog = new AddToFridgeDialog();
                addToFridgeDialog.setArguments(arguments);
                addToFridgeDialog.show(getFragmentManager(), "ActionsBottomSheetDialog");
                dismiss();
            }
        });

        return view;
    }


}
