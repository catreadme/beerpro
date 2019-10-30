package ch.beerpro.domain.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Manufacturer implements Entity {

    public static final String COLLECTION = "manufacturers";
    public static final String FIELD_ID = "id";
    public static final String FIELD_RESOURCE_NAME = "resourceName";

    private String id;
    private String resourceName;

    public Manufacturer(String id, String resourceName) {
        this.id = id;
        this.resourceName = resourceName;
    }

    public Manufacturer() {
    }

    public String getId() {
        return this.id;
    }

    public String getResourceName() { return this.resourceName; }


    public void setId(String id) {
        this.id = id;
    }

    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Wish)) return false;
        final Manufacturer other = (Manufacturer) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$resourceName = this.getResourceName();
        final Object other$resourceName = other.getResourceName();
        return this$resourceName == null ? other$resourceName == null : this$resourceName.equals(other$resourceName);
    }

    private boolean canEqual(final Object other) {
        return other instanceof Manufacturer;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $resourceName = this.getResourceName();
        result = result * PRIME + ($resourceName == null ? 43 : $resourceName.hashCode());
        return result;
    }

    @NonNull
    public String toString() {
        return "Manufacturer(id=" + this.getId() + ", resourceName=" + this.getResourceName() + ")";
    }
}
