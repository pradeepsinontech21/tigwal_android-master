package com.tigwal.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tigwal.data.api.UserParams
import com.tigwal.app.rest.RestConstant
import com.tigwal.R
import com.tigwal.base.BaseFragment
import com.tigwal.data.api.Resource
import com.tigwal.data.model.listCart.CartListItem
import com.tigwal.databinding.FragmentCartBinding
import com.tigwal.ui.activity.DashboardActivity
import com.tigwal.ui.activity.ForgotPasswordActivity
import com.tigwal.ui.activity.PaymentOptionActivity
import com.tigwal.ui.adapter.CartListAdapter
import com.tigwal.ui.factory.CartViewFactory
import com.tigwal.ui.view_model.cart.CartViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
//import company.tap.gosellapi.open.controllers.SDKSession
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


@SuppressLint("UseRequireInsteadOfGet")
class CartFragment : BaseFragment(), KodeinAware, View.OnClickListener {
    private val factory: CartViewFactory by instance()
    private lateinit var cartViewModel: CartViewModel
    private lateinit var binding: FragmentCartBinding
    override val kodein: Kodein by closestKodein()
    var arrayListProduct = ArrayList<CartListItem>()
    lateinit var adapter: CartListAdapter
    var isLoading: Boolean = false

    companion object {
    }

    @SuppressLint("Range")
    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        setupViewModel()
        setupObservers()


        if (Connectivity.isConnected(activity)) {
            isLoading = true
            cartViewModel.cartApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.getRoot()
    }

    override fun getIntentData() {
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.my_cart))
        binding.toolBar.ivBack.visibility=View.GONE
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)

        binding.btnCheckOrder.setOnClickListener(this)
        binding.btnContinueShopping.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(requireActivity())
        binding.btnCheckOrder.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.btnContinueShopping.typeface = AppUtils.getMIDIUM(requireActivity())
    }

    //click
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.iv_back -> {
                if (getFragmentManager()!!.getBackStackEntryCount() > 0) {
                    getFragmentManager()!!.popBackStack();
                }
            }

            R.id.btn_check_order -> {
//                if (!this::sdkSession.isInitialized) sdkSession = SDKSession()

                startActivity(
                    Intent(
                        activity!!,
                        PaymentOptionActivity::class.java
                    ).putExtra("CART_DATA", arrayListProduct)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.btnContinueShopping -> {
                startActivity(Intent(activity, DashboardActivity::class.java))
                AppUtils.startFromRightToLeft(activity!!)
            }
        }
    }

    //set view model
    private fun setupViewModel() {
        cartViewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)
        binding.viewmodel = cartViewModel
    }

    //set observer for currency
    private fun setupObservers() {
        cartViewModel.deleteCartApiCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== deleteCartApiCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { cartlistResponse ->
                            if (cartlistResponse.code == 200) {
                                if (cartlistResponse.status == true) {
                                    if (Connectivity.isConnected(activity)) {
                                        isLoading = false
                                        cartViewModel.cartApi(
                                            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
                                        )
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "" + resources.getString(R.string.noInternetConnection),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + cartlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + cartlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(
                            activity,
                            "" + response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {

                    }
                }
            })


        cartViewModel.updatedCartApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== updatedCartApiResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { cartlistResponse ->
                            if (cartlistResponse.code == 200) {
                                if (cartlistResponse.status == true) {
                                    if (Connectivity.isConnected(activity)) {
                                        isLoading = false
                                        cartViewModel.cartApi(
                                            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
                                        )
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "" + resources.getString(R.string.noInternetConnection),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + cartlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + cartlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(
                            activity,
                            "" + response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                    }
                }
            })

        cartViewModel.cartApiCallResponse.observe(
            this,
            { response ->
                when (response) {


                    is Resource.Success -> {

                        Log.d(
                            "Response",
                            "====== CART RESPONSE ====> " + Gson().toJson(response)
                        )
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                        response.data?.let { cartlistResponse ->
                            if (cartlistResponse.code == 200) {
                                if (cartlistResponse.status == true) {
                                    cartList(cartlistResponse.data!!.cartdata,cartlistResponse.data.admin_commision!!)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + cartlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + cartlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                        Toast.makeText(
                            activity,
                            "" + response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Response",
                            "====== CART RESPONSE ====> " + Gson().toJson(response)
                        )


                    }
                    is Resource.Loading -> {
                        if (isLoading) {
//                            (activity as DashboardActivity?)!!.rlProgressView.visibility =
//                                View.VISIBLE
                        }
                    }
                }
            })
    }

    private fun cartList(data: List<CartListItem?>?,admin_commision:String) {
        if (data != null && data.size > 0) {
            binding.btnCheckOrder.visibility = View.VISIBLE
            if (RestConstant.ContinueShopping.equals("1")) {
                RestConstant.ContinueShopping = "0"
                binding.btnContinueShopping.visibility = View.VISIBLE
//                binding.btnContinueShopping.visibility = View.GONE
            }
            binding.rvCart.visibility = View.VISIBLE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.GONE
            arrayListProduct = data as ArrayList<CartListItem>
            adapter = CartListAdapter(
                arrayListProduct,admin_commision
            )
            val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            binding.rvCart.itemAnimator = DefaultItemAnimator()
            binding.rvCart.layoutManager = mLayoutManager1
            binding.rvCart.adapter = adapter
            adapter.setOnItemClickLister(object :
                CartListAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int) {
                }

                override fun itemDelete(position: Int, data: CartListItem) {
                    dialogeCartRemove(position, data)
                }

                override fun itemCartUpdate(position: Int, data: CartListItem) {
                    if (Connectivity.isConnected(activity)) {
                        val params = HashMap<String, Any?>()
                        params.put(UserParams.qty, "" + data.qty)
                        params.put(UserParams._method, "PUT")
                        val body: RequestBody = RequestBody.create(
                            "application/json; charset=utf-8".toMediaTypeOrNull(),
                            JSONObject(params).toString()
                        )
                        cartViewModel.updateCartApi(
                            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                            data.id.toString(), body
                        )
                    } else {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.noInternetConnection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun itemAddNote(position: Int, data: CartListItem) {
                    dialogeAddNote(position, data)
                }
            })
        } else {
            binding.btnCheckOrder.visibility = View.GONE
            binding.btnContinueShopping.visibility = View.GONE
            binding.rvCart.visibility = View.GONE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
            binding.layoutNotFound.txtDataNotFoundTitle.setText(resources.getString(R.string.cart_empty))
        }
    }

    private fun dialogeAddNote(position: Int, data: CartListItem) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialoge_add_note)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        val txtSubmit: AppCompatTextView = dialog.findViewById(R.id.txtSubmit)
        val txtNo: AppCompatTextView = dialog.findViewById(R.id.txtCancel)
        val etAddMessage: AppCompatEditText = dialog.findViewById(R.id.etAddMessage)
        if (data.notes != null && !data.notes.equals("")) {
            etAddMessage.setText(data.notes)
        }
        txtSubmit.setOnClickListener {

            dialog.dismiss()
            if (Connectivity.isConnected(activity)) {
                val params = HashMap<String, Any?>()
                params.put(UserParams.notes, "" + etAddMessage.text.toString())
                params.put(UserParams._method, "PUT")
                val body: RequestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    JSONObject(params).toString()
                )
                cartViewModel.updateCartApi(
                    RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                    data.id.toString(), body
                )
            } else {
                Toast.makeText(
                    activity,
                    "" + resources.getString(R.string.noInternetConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        txtNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

//    private lateinit var sdkSession: SDKSession

    private fun dialogeCartRemove(position: Int, data: CartListItem) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialoge_custom_two_button)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        val txtNo: AppCompatTextView = dialog.findViewById(R.id.txtNo)
        val txtYes: AppCompatTextView = dialog.findViewById(R.id.txtYes)
        val txtMessage: AppCompatTextView = dialog.findViewById(R.id.txtMessage)
        txtMessage.text = resources.getString(R.string.are_you_sure_you_want_to_delete_cart)
        txtYes.setOnClickListener {
            dialog.dismiss()
            if (Connectivity.isConnected(activity)) {
                val params = HashMap<String, Any?>()
                params.put(UserParams._method, "DELETE")
                val body: RequestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    JSONObject(params).toString()
                )
                cartViewModel.deleteCartApi(
                    RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                    data.id.toString(), body
                )
            } else {
                Toast.makeText(
                    activity,
                    "" + resources.getString(R.string.noInternetConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        txtNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}