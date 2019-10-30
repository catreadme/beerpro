package ch.beerpro.domain.utils;

import java.util.HashMap;
import java.util.List;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeItem;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;

public class MyBeersContainer {
    private List<Wish> wishlist;
    private List<Rating> ratings;
    private List<FridgeItem> fridge;
    private HashMap<String, Beer> beers;

    public MyBeersContainer (HashMap<String, Beer> beers, List<Wish> wishlist, List<Rating> ratings, List<FridgeItem> fridge) {
        this.beers = beers;
        this.wishlist = wishlist;
        this.ratings = ratings;
        this.fridge = fridge;
    }

    public List<Wish> getWishlist() {
        return wishlist;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<FridgeItem> getFridge() {
        return fridge;
    }

    public HashMap<String, Beer> getBeers() {
        return beers;
    }
}
