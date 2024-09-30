package com.tigwal.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseFragment
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.language.LanguageListModel
import com.tigwal.databinding.FragmentSettingBinding
import com.tigwal.ui.activity.*
import com.tigwal.ui.adapter.ChangeLanguageAdapter
import com.tigwal.ui.factory.SettingViewFactory
import com.tigwal.ui.view_model.setting.SettingViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.LanguagePreferences
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


@SuppressLint("UseRequireInsteadOfGet")
class SettingFragment : BaseFragment(), KodeinAware, View.OnClickListener {
    private val factory: SettingViewFactory by instance()
    private lateinit var dashboardViewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding
    override val kodein: Kodein by closestKodein()
    var arrayList: ArrayList<LanguageListModel>? = null
    var lun: LanguageListModel? = null
    private lateinit var languagePreferences: LanguagePreferences

    companion object {
    }

    override fun getIntentData() {

    }


    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.rlProfile.setOnClickListener(this)
        binding.rlLogout.setOnClickListener(this)
        binding.rlShareApp.setOnClickListener(this)
        binding.rlLegal.setOnClickListener(this)
        binding.rlFaq.setOnClickListener(this)
        binding.rlHelp.setOnClickListener(this)
        binding.rlChangePassword.setOnClickListener(this)
        binding.rlSupportChat.setOnClickListener(this)
        binding.rlChangeLanguage.setOnClickListener(this)
        binding.rlSignin.setOnClickListener(this)
        binding.txtPrivacyPolicy.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLD(requireActivity())
        binding.tvProfile.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtChangePassword.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtChangeLang.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtSupportChat.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtFaq.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtShareApp.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtHelp.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtLegal.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtLogout.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtPrivacyPolicy.typeface = AppUtils.getMIDIUM(requireActivity())
    }

    @SuppressLint("Range")
    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)

        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.setting))
        binding.toolBar.ivBack.visibility = View.GONE

        if (!MySharedPreferences.getMySharedPreferences()?.userId.equals("")) {
            if (MySharedPreferences.getMySharedPreferences()?.loginType.equals("2") || MySharedPreferences.getMySharedPreferences()?.loginType!!.equals(
                    "3"
                )
            ) {
                binding.rlChangePassword.visibility = View.GONE
            } else {
                binding.rlChangePassword.visibility = View.VISIBLE
            }
        } else {
            binding.rlChangePassword.visibility = View.GONE
            binding.rlSupportChat.visibility = View.GONE
            binding.rlChangeLanguage.visibility = View.GONE
            binding.rlProfile.visibility = View.GONE
            binding.rlLogout.visibility = View.GONE
            binding.rlSignin.visibility = View.VISIBLE
        }

        if (!MySharedPreferences.getMySharedPreferences()?.userId.equals("")) {
            binding.toolBar.ivBack.visibility = View.GONE
        } else {
            binding.toolBar.ivBack.visibility = View.VISIBLE
        }

        return binding.getRoot()
    }

    //click
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.iv_back -> {
                if (getFragmentManager()!!.getBackStackEntryCount() > 0) {
                    getFragmentManager()!!.popBackStack();
                }
            }

            R.id.txtPrivacyPolicy -> {
//                activeItem(rlPrivacyPolicy, txtPrivacyPolicy, ivPrivacyPolicy)
                startActivity(
                    Intent(activity, CMSScreenActivity::class.java).putExtra(
                        "Flag",
                        "privacy-policy"
                    )
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlSignin -> {
                val dialog = Dialog(activity!!)
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
                txtMessage.text = resources.getString(R.string.do_you_want_signin)
                txtYes.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    AppUtils.startFromRightToLeft(activity!!)
                }
                txtNo.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

            }

            R.id.rlShareApp -> {
//                activeItem(rlShareApp, txtShareApp, ivShareApp)
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.tigwal"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            R.id.rlLogout -> {
                if (!MySharedPreferences.getMySharedPreferences()?.userId.equals("")) {
//                    activeItem(rlLogout, txtLogout, ivLogout)
                    dialogeLogoutApp()
                } else {
                    logoutClear()
                }
            }

            R.id.rlProfile -> {
//                activeItem(rlProfile, tvProfile, ivProfile)
                startActivity(
                    Intent(activity, ProfileActivity::class.java)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlLegal -> {
//                activeItem(rlLegal, txtLegal, ivLegal)
                startActivity(
                    Intent(activity, CMSScreenActivity::class.java).putExtra(
                        "Flag",
                        "legal"
                    )
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlHelp -> {
//                activeItem(rlHelp, txtHelp, ivHelp)
                startActivity(
                    Intent(activity, CMSScreenActivity::class.java).putExtra(
                        "Flag",
                        "help"
                    )
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlFaq -> {
//                activeItem(rlFaq, txtFaq, ivFaq)
                startActivity(
                    Intent(activity, CMSScreenActivity::class.java).putExtra(
                        "Flag",
                        "faq"
                    )
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlChangePassword -> {
//                activeItem(rlChangePassword, txt_change_password, ivChangePassword)
                startActivity(
                    Intent(activity, ChangePasswordActivity::class.java)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.rlSupportChat -> {
//                activeItem(rlSupportChat, txt_support_chat, ivSupportChat)
                startActivity(
                    Intent(activity, ChatListActivity::class.java)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }
            R.id.rlChangeLanguage -> {
//                activeItem(rlChangeLanguage, txtChangeLang, ivChangeLang)
                arrayList = ArrayList()
                lun = LanguageListModel()
                lun!!.setId("ar")
                lun!!.setName(resources.getString(R.string.arabic))
                arrayList!!.add(lun!!)

                lun = LanguageListModel()
                lun!!.setId("en")
                lun!!.setName(resources.getString(R.string.english))
                arrayList!!.add(lun!!)

                val dialog = Dialog(activity!!)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.dialoge_chooselanguage_view)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog.window!!.attributes = lp
                val txtCancel: AppCompatTextView = dialog.findViewById(R.id.txtCancel)
                val txtTitle: AppCompatTextView = dialog.findViewById(R.id.txtTitle)
                val txtDone: AppCompatTextView = dialog.findViewById(R.id.txtDone)

                txtDone.typeface = AppUtils.getMIDIUM(activity!!)
                txtCancel.typeface = AppUtils.getMIDIUM(activity!!)
                txtTitle.typeface = AppUtils.getBOLD(activity!!)

                val recyclerviewLanguage: RecyclerView =
                    dialog.findViewById(R.id.recyclerviewLanguage)
                val adapter = ChangeLanguageAdapter(
                    arrayList as ArrayList<LanguageListModel>
                )
                val mLayoutManager1: RecyclerView.LayoutManager =
                    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                recyclerviewLanguage.itemAnimator = DefaultItemAnimator()
                recyclerviewLanguage.layoutManager = mLayoutManager1
                recyclerviewLanguage.adapter = adapter
                adapter.setOnItemClickLister(object :
                    ChangeLanguageAdapter.OnItemClickLister {
                    override fun itemClicked(view: View?, position: Int) {
                        MySharedPreferences.getMySharedPreferences()!!.language =
                            arrayList!!.get(position).id
                    }
                })

                txtCancel.setOnClickListener {
                    dialog.dismiss()
                    MySharedPreferences.getMySharedPreferences()!!.language = ""
                }
                txtDone.setOnClickListener {
                    dialog.dismiss()
                    if (MySharedPreferences.getMySharedPreferences()!!.language.equals("ar")) {
                        AppUtils.languageSelection(activity!!, "ar")
                    } else {
                        AppUtils.languageSelection(activity!!, "en")
                    }
                    (v.context as DashboardActivity).restartActivity()
                }
                dialog.show()

            }
        }
    }

    fun activeItem(
        relativeLayout: RelativeLayout,
        textView: AppCompatTextView,
        imageView: AppCompatImageView
    ) {
        inactiveItem()
        relativeLayout.setBackgroundDrawable(resources.getDrawable(R.drawable.border_blue_rect_bgview))
        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        imageView.visibility = View.VISIBLE
    }

    fun inactiveItem() {
        binding.rlProfile.setBackgroundResource(0)
        binding.rlChangePassword.setBackgroundResource(0)
        binding.rlChangeLanguage.setBackgroundResource(0)
        binding.rlSupportChat.setBackgroundResource(0)
        binding.rlFaq.setBackgroundResource(0)
        binding.rlShareApp.setBackgroundResource(0)
        binding.rlHelp.setBackgroundResource(0)
        binding.rlLegal.setBackgroundResource(0)
        binding.rlLogout.setBackgroundResource(0)
        binding.rlPrivacyPolicy.setBackgroundResource(0)

        binding.tvProfile.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtChangeLang.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtChangePassword.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtSupportChat.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtFaq.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtHelp.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtShareApp.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtLegal.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtLogout.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        binding.txtPrivacyPolicy.setTextColor(ContextCompat.getColor(activity!!, R.color.black))

        binding.ivProfile.visibility = View.GONE
        binding.ivChangeLang.visibility = View.GONE
        binding.ivChangePassword.visibility = View.GONE
        binding.ivSupportChat.visibility = View.GONE
        binding.ivFaq.visibility = View.GONE
        binding.ivShareApp.visibility = View.GONE
        binding.ivHelp.visibility = View.GONE
        binding.ivLegal.visibility = View.GONE
        binding.ivLogout.visibility = View.GONE
        binding.ivPrivacyPolicy.visibility = View.GONE
    }

    //set view model
    private fun setupViewModel() {
        dashboardViewModel = ViewModelProvider(this, factory).get(SettingViewModel::class.java)
        binding.viewmodel = dashboardViewModel

    }

    private fun setupObservers() {
        dashboardViewModel.logoutApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d(
                        "Response",
                        "====== logoutApiResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { logoutResponse ->
                        if (logoutResponse.code == 200) {
                            if (logoutResponse.status == true) {
                                Toast.makeText(
                                    activity,
                                    "" + logoutResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                logoutClear()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + logoutResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + logoutResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

                is Resource.Error -> {
                    hideProgressDialog()
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
                    showProgressDialog(activity!!)
                }
            }
        }


    }

    fun logoutClear() {
        var appPreferences = LanguagePreferences(activity)
        appPreferences.clearAll()
        val mySharedPreferences =
            MySharedPreferences.getMySharedPreferences()
        mySharedPreferences!!.isLogin = false
        val sharedPreferences: SharedPreferences =
            activity!!.getSharedPreferences(
                "p6_pref",
                Context.MODE_PRIVATE
            )
        sharedPreferences.edit().clear()
        sharedPreferences.edit().commit()
        sharedPreferences.edit().apply()
        appPreferences.clearAll()
        // FB
        LoginManager.getInstance().logOut()
        // Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        googleSignInClient.signOut()
        startActivity(Intent(activity, SplashActivity::class.java))
        AppUtils.startFromRightToLeft(activity!!)
        activity!!.finish()
    }

    private fun dialogeLogoutApp() {
        val dialog = Dialog(activity!!)
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
        txtMessage.text = resources.getString(R.string.are_you_sure_you_want_logout)
        txtYes.setOnClickListener {
            dialog.dismiss()
            logoutApi()
            MySharedPreferences.getMySharedPreferences()!!.clearPreferences()
        }
        txtNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    fun logoutApi() {

        val params = HashMap<String, String?>()
        params[UserParams.app_identifier] =
            MySharedPreferences.getMySharedPreferences()!!.deviceUDID
        dashboardViewModel.logoutApi(
            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
            params
        )
    }
}