package ch.beerpro.domain.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.Locale;

@IgnoreExtraProperties
public class FridgeBeer implements Entity {
    public static final String COLLECTION = "fridgeBeers";
    public static final String FIELD_ID = "id";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_BEER_ID = "beerId";
    public static final String FIELD_AMOUNT = "amount";
    public static final String FIELD_ADDED_AT = "addedAt";

    @Exclude
    private String id;
    private String userId;
    private String beerId;
    private int amount;
    private Date addedAt;

    public FridgeBeer(String userId, String beerId, int amount, Date addedAt) {
        this.userId = userId;
        this.beerId = beerId;
        this.amount = amount;
        this.addedAt = addedAt;
    }

    public FridgeBeer() {
    }

    public static String generateId(String userId, String beerId) {
        return String.format("%s_%s", userId, beerId);
    }

    public String getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getBeerId() {
        return this.beerId;
    }

    public int getAmount() {
        return this.amount;
    }

    public Date getAddedAt() { return this.addedAt; }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBeerId(String beerId) {
        this.beerId = beerId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setAddedAt(Date addedAt) { this.addedAt = addedAt; }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FridgeBeer)) {
            return false;
        }

        final FridgeBeer other = (FridgeBeer) o;

        if (!other.canEqual((Object) this)) {
            return false;
        }

        if (this.getId() == null ? other.getId() != null : !this.getId().equals(other.getId())) {
            return false;
        }

        if (this.getUserId() == null ? other.getUserId() != null : !this.getUserId().equals(other.getUserId())) {
            return false;
        }

        if (this.getBeerId() == null ? other.getBeerId() != null : !this.getBeerId().equals(other.getBeerId())) {
            return false;
        }

        if (this.getAmount() != other.getAmount()) {
            return false;
        }

        return this.getAddedAt() == null ? other.getAddedAt() == null : this.getAddedAt().equals(other.getAddedAt());
    }

    private boolean canEqual(final Object other) {
        return other instanceof FridgeBeer;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        result = result * PRIME + (this.getId() == null ? 43 : this.getId().hashCode());
        result = result * PRIME + (this.getUserId() == null ? 43 : this.getUserId().hashCode());
        result = result * PRIME + (this.getBeerId() == null ? 43 : this.getBeerId().hashCode());
        result = result * PRIME + (this.getAmount() == 0 ? 43 : this.getAmount());
        result = result * PRIME + (this.getAddedAt() == null ? 43 : this.getAddedAt().hashCode());

        return result;
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.getDefault(),
                "FridgeBeer(id=%s, userId=%s, beerId=%s, amount=%d, addedAt=%s",
                this.getId(),
                this.getUserId(),
                this.getBeerId(),
                this.getAmount(),
                this.getAddedAt()
        );
    }
}
