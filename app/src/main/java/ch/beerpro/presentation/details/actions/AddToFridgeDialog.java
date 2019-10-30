package ch.beerpro.presentation.details.actions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import ch.beerpro.R;
import ch.beerpro.data.repositories.FridgeRepository;

public class AddToFridgeDialog extends DialogFragment {
    String userId, beerId;
    int amountInFridge;
    FridgeRepository fridgeRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        userId = arguments.getString("userId");
        beerId = arguments.getString("beerId");
        amountInFridge = arguments.getInt("amountInFridge");
        fridgeRepository = new FridgeRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_fridge_dialog, container, false);

        NumberPicker numberPicker = view.findViewById(R.id.addToFridgeNumber);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(amountInFridge);

        Button submitButton = view.findViewById(R.id.addToFridgeSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = numberPicker.getValue();
                fridgeRepository.addItemToFridge(userId, beerId, amount);
                dismiss();
            }
        });

        return view;
    }
}
