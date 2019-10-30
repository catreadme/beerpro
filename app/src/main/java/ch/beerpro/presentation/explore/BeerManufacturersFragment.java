package ch.beerpro.presentation.explore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.R;
import ch.beerpro.domain.models.Manufacturer;
import ch.beerpro.presentation.MainViewModel;
import ch.beerpro.presentation.utils.GridSpacingItemDecoration;


/**
 * This class is really similar to {@link BeerCategoriesFragment}, see the documentation there.
 */
public class BeerManufacturersFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private OnItemSelectedListener mListener;

    public BeerManufacturersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            mListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expore_beer_manufacturers, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false, 0));

        BeerManufacturersRecyclerViewAdapter adapter = new BeerManufacturersRecyclerViewAdapter(mListener);

        MainViewModel model = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        model.getBeerManufacturers().observe(this, (List<Manufacturer> manufacturers) -> {
           adapter.submitList(new ArrayList<String>() {{
               for (Manufacturer manufacturer : manufacturers) {
                   add(manufacturer.getResourceName());
               }
           }});
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnItemSelectedListener {
        void onBeerManufacturerSelected(String name);
    }
}
