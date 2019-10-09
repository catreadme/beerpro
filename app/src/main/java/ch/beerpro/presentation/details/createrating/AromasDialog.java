package ch.beerpro.presentation.details.createrating;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import ch.beerpro.R;

public class AromasDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList selectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Aromas")
                .setMultiChoiceItems(R.array.aromas, null,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                selectedItems.add(which);
                            } else if (selectedItems.contains(which)) {
                                // Else, if the item is already in the array, remove it
                                selectedItems.remove(Integer.valueOf(which));
                            }
                        })
                .setPositiveButton("Fertig", (dialog, id) -> {
                    // User clicked OK, so save the selectedItems results somewhere
                    // or return them to the component that opened the dialog
                })
                .setNegativeButton("Abbrechen", (dialog, id) -> {

                });

        return builder.create();
    }

}
