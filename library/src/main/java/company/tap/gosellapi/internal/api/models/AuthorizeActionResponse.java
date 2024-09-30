package company.tap.gosellapi.internal.api.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import company.tap.gosellapi.internal.api.enums.AuthorizeActionStatus;

/**
 * The type Authorize action response.
 */
public final class AuthorizeActionResponse implements Serializable {

    @SerializedName("status")
    @Expose
    @NonNull private AuthorizeActionStatus status;

    /**
     * Gets status.
     *
     * @return the status
     */
    @NonNull
    public AuthorizeActionStatus getStatus() {
        return status;
    }
}
