package com.tigwal

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.tigwal.data.api.RetrofitClient
import com.tigwal.data.repository.AppRepository
import com.tigwal.ui.factory.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

open class Application : KodeinAware, MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        application = this

    }

    companion object {
        @JvmStatic
        var application: Application? = null
            private set
    }

    //kodein injection provide by kotlin
    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))
        bind() from singleton { RetrofitClient() }
//        bind() from singleton { RetrofitClientTap() }


        bind() from provider {
            LoginViewFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            VerifyOtpViewFactory(
                instance(), appRepository = AppRepository()
            )
        }
        bind() from provider {
            DashboardViewFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ForgotPasswordFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ResetPasswordFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            CMSScreenFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ProfileViewFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            SettingViewFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            CartViewFactory(
                instance(), appRepository = AppRepository()
            )
        }
        bind() from provider {
            OnGoingFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            NotificationViewFactory(
                instance(), appRepository = AppRepository()
            )
        }


        bind() from provider {
            MySaveCardFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ChangePasswordFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            AddNewCardFactory(
                instance(), appRepository = AppRepository()
            )
        }
        bind() from provider {
            OrderReceiveFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            SupportChatFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            CancelOrderFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            OrderSummaryFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ViewAllCategoryFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            SubCategoryFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ProductListFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ProductDetailFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            PaymentOptionFactory(
                instance(), appRepository = AppRepository()
            )
        }


        bind() from provider {
            SplashFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            SearchProductListFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            ChatListFactory(
                instance(), appRepository = AppRepository()
            )
        }

        bind() from provider {
            LanguageFactory(
                instance(), appRepository = AppRepository()
            )
        }
    }
}