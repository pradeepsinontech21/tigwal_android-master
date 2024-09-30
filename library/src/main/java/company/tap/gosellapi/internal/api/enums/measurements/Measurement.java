package company.tap.gosellapi.internal.api.enums.measurements;

import androidx.annotation.RestrictTo;

import com.google.gson.annotations.SerializedName;

/**
 * The enum Measurement.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum Measurement {

    /**
     * Area measurement.
     */
    @SerializedName("area")             AREA,
    /**
     * Duration measurement.
     */
    @SerializedName("duration")         DURATION,
    /**
     * Electric charge measurement.
     */
    @SerializedName("electric_charge")  ELECTRIC_CHARGE,
    /**
     * Electric current measurement.
     */
    @SerializedName("electric_current") ELECTRIC_CURRENT,
    /**
     * Energy measurement.
     */
    @SerializedName("energy")           ENERGY,
    /**
     * Length measurement.
     */
    @SerializedName("length")           LENGTH,
    /**
     * Mass measurement.
     */
    @SerializedName("mass")             MASS,
    /**
     * Power measurement.
     */
    @SerializedName("power")            POWER,
    /**
     * Volume measurement.
     */
    @SerializedName("volume")           VOLUME,
    /**
     * Units measurement.
     */
    @SerializedName("units")            UNITS
}
