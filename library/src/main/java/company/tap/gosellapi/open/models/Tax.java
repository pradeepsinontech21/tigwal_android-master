package company.tap.gosellapi.open.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import company.tap.gosellapi.internal.api.models.AmountModificator;

/**
 * The type Tax.
 */
public final class Tax {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    @Nullable private String description;

    @SerializedName("amount")
    @Expose
    private AmountModificator amount;

    /**
     * Instantiates a new Tax.
     *
     * @param name        the name
     * @param description the description
     * @param amount      the amount
     */
    public Tax(String name, @Nullable String description, AmountModificator amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    @Nullable public String getDescription() {
        return description;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public AmountModificator getAmount() {
        return amount;
    }
}
