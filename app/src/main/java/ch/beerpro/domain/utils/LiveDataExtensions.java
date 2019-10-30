package ch.beerpro.domain.utils;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeItem;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;

public class LiveDataExtensions {

    public static <A, B> LiveData<Pair<A, B>> zip(LiveData<A> as, LiveData<B> bs) {
        return new MediatorLiveData<Pair<A, B>>() {

            A lastA = null;
            B lastB = null;

            {
                {
                    addSource(as, (A a) -> {
                        lastA = a;
                        update();
                    });
                    addSource(bs, (B b) -> {
                        lastB = b;
                        update();
                    });
                }
            }

            private void update() {
                this.setValue(new Pair<>(lastA, lastB));
            }
        };
    }

    public static <A, B> LiveData<Pair<A, B>> combineLatest(LiveData<A> as, LiveData<B> bs) {
        return new MediatorLiveData<Pair<A, B>>() {

            A lastA = null;
            B lastB = null;

            {
                {
                    addSource(as, (A a) -> {
                        lastA = a;
                        update();
                    });
                    addSource(bs, (B b) -> {
                        lastB = b;
                        update();
                    });
                }
            }

            private void update() {
                if (lastA != null && lastB != null) {
                    this.setValue(new Pair<>(lastA, lastB));
                }
            }
        };
    }

    public static <A, B, C> LiveData<Triple<A, B, C>> combineLatest(LiveData<A> as, LiveData<B> bs, LiveData<C> cs) {
        return new MediatorLiveData<Triple<A, B, C>>() {

            A lastA = null;
            B lastB = null;
            C lastC = null;

            {
                {
                    addSource(as, (A a) -> {
                        lastA = a;
                        update();
                    });
                    addSource(bs, (B b) -> {
                        lastB = b;
                        update();
                    });
                    addSource(cs, (C c) -> {
                        lastC = c;
                        update();
                    });
                }
            }

            private void update() {
                if (lastA != null && lastB != null && lastC != null) {
                    this.setValue(Triple.of(lastA, lastB, lastC));
                }
            }
        };
    }

    public static LiveData<MyBeersContainer> combineLatest(LiveData<List<Wish>> myWishList, LiveData<List<Rating>> myRatings, LiveData<List<FridgeItem>> myFridge, LiveData<HashMap<String, Beer>> myBeers) {
        return new MediatorLiveData<MyBeersContainer>() {
            List<Wish> lastWishlist = null;
            List<Rating> lastRatings = null;
            List<FridgeItem> lastFridge = null;
            HashMap<String, Beer> lastBeers = null;

            {
                {
                    addSource(myBeers, (HashMap<String, Beer> a) -> {
                        lastBeers = a;
                        update();
                    });

                    addSource(myWishList, (List<Wish> b) -> {
                        lastWishlist = b;
                        update();
                    });

                    addSource(myRatings, (List<Rating> c) -> {
                        lastRatings = c;
                        update();
                    });

                    addSource(myFridge, (List<FridgeItem> d) -> {
                        lastFridge = d;
                        update();
                    });
                }
            }

            private void update() {
                if (lastBeers != null && lastWishlist != null && lastRatings != null && lastFridge != null) {
                    this.setValue(new MyBeersContainer(lastBeers, lastWishlist, lastRatings, lastFridge));
                }
            }
        };
    }
}
