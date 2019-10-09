package ch.beerpro.presentation.details.createrating;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ch.beerpro.R;

public class BitternessDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bitterkeit")
                .setItems(R.array.bitterness, (dialog, which) -> {

                });

        return builder.create();
    }
}
