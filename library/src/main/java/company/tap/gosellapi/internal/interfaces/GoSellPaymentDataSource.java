package company.tap.gosellapi.internal.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import company.tap.gosellapi.internal.api.enums.TransactionMode;
import company.tap.gosellapi.internal.api.models.AuthorizeAction;
import company.tap.gosellapi.internal.api.models.Receipt;
import company.tap.gosellapi.internal.api.models.Reference;
import company.tap.gosellapi.internal.api.models.Shipping;
import company.tap.gosellapi.internal.api.models.Tax;
import company.tap.gosellapi.open.models.Customer;
import company.tap.gosellapi.open.models.PaymentItem;

/**
 * The interface Go sell payment data source.
 */
public interface GoSellPaymentDataSource {

    /**
     * Transaction currency. @return the currency
     */
    @NonNull    String                  getCurrency();

    /**
     * Customer. @return the customer
     */
    @NonNull
    Customer getCustomer();

    /**
     * Amount. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the amount
     */
    @Nullable    BigDecimal              getAmount();

    /**
     * List of items to pay for. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the items
     */
    @Nullable    ArrayList<PaymentItem>  getItems();

    /**
     * Transaction mode. If you return null in this method, it will be treated as PURCHASE. @return the transaction mode
     */
    @Nullable    TransactionMode         getTransactionMode();

    /**
     * List of taxes. Optional. Note: specifying taxes will affect total payment amount. @return the taxes
     */
    @Nullable    ArrayList<Tax>          getTaxes();

    /**
     * Shipping list. Optional. Note: specifying shipping will affect total payment amount. @return the shipping
     */
    @Nullable    ArrayList<Shipping>     getShipping();

    /**
     * Tap will post to this URL after transaction finishes. Optional. @return the post url
     */
    @Nullable    String                  getPostURL();

    /**
     * Description of the payment. @return the payment description
     */
    @Nullable    String                  getPaymentDescription();

    /**
     * If you would like to pass additional information with the payment, pass it here. @return the payment metadata
     */
    @Nullable    HashMap<String, String> getPaymentMetadata();

    /**
     * Payment reference. Implement this property to keep a reference to the transaction on your backend. @return the payment reference
     */
    @Nullable    Reference               getPaymentReference();

    /**
     * Payment statement descriptor. @return the payment statement descriptor
     */
    @Nullable    String                  getPaymentStatementDescriptor();

    /**
     * Defines if 3D secure check is required. @return the requires 3 d secure
     */
    @Nullable    boolean                 getRequires3DSecure();

    /**
     * Receipt dispatch settings. @return the receipt settings
     */
    @Nullable    Receipt                 getReceiptSettings();

    /**
     * Action to perform after authorization succeeds. Used only if transaction mode is AUTHORIZE_CAPTURE. @return the authorize action
     */
    @Nullable    AuthorizeAction         getAuthorizeAction();
}
