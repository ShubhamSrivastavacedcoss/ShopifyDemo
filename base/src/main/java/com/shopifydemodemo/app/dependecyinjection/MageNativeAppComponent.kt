package com.shopifydemodemo.app.dependecyinjection

import com.shopifydemodemo.app.addresssection.activities.AddressList
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.activities.Splash
import com.shopifydemodemo.app.basesection.fragments.LeftMenu
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.checkoutsection.activities.CheckoutWeblink
import com.shopifydemodemo.app.checkoutsection.activities.OrderSuccessActivity
import com.shopifydemodemo.app.collectionsection.activities.CollectionList
import com.shopifydemodemo.app.collectionsection.activities.CollectionListMenu
import com.shopifydemodemo.app.homesection.activities.HomePage
import com.shopifydemodemo.app.homesection.viewmodels.HomePageViewModel
import com.shopifydemodemo.app.jobservicessection.JobScheduler
import com.shopifydemodemo.app.productsection.activities.JudgeMeCreateReview
import com.shopifydemodemo.app.loginsection.activity.LoginActivity
import com.shopifydemodemo.app.loginsection.activity.RegistrationActivity
import com.shopifydemodemo.app.ordersection.activities.OrderDetails
import com.shopifydemodemo.app.ordersection.activities.OrderList
import com.shopifydemodemo.app.productsection.activities.*
import com.shopifydemodemo.app.quickadd_section.activities.QuickAddActivity
import com.shopifydemodemo.app.searchsection.activities.AutoSearch
import com.shopifydemodemo.app.userprofilesection.activities.UserProfile
import com.shopifydemodemo.app.utils.Urls
import com.shopifydemodemo.app.wishlistsection.activities.WishList
import com.shopifydemodemo.app.yotporewards.earnrewards.EarnRewardsActivity
import com.shopifydemodemo.app.yotporewards.earnrewards.FaqsActivity
import com.shopifydemodemo.app.yotporewards.getrewards.GetRewardsActivity
import com.shopifydemodemo.app.yotporewards.myrewards.MyRewardsActivity
import com.shopifydemodemo.app.yotporewards.referfriend.ReferFriendActivity
import com.shopifydemodemo.app.yotporewards.rewarddashboard.RewardDashboard
import com.shopifydemodemo.app.yotporewards.withoutlogin.RewardsPointActivity

import javax.inject.Singleton

import dagger.Component

@Component(modules = [UtilsModule::class])
@Singleton
interface MageNativeAppComponent {

    fun doSplashInjection(splash: Splash)
    fun doFilterInjection(product: FilterActivity)
    fun doProductListInjection(product: ProductList)
    fun doCollectionInjection(collectionList: CollectionList)
    fun doCollectionInjection(collectionList: CollectionListMenu)
    fun doProductViewInjection(product: ProductView)
    fun doJudgeMeReviewInjection(judgeMeCreateReview: JudgeMeCreateReview)
    fun doYotpoReviewInjection(WriteAReview: WriteAReview)
    fun doReviewListInjection(reviewListActivity: AllReviewListActivity)
    fun doAllJudgeMeReviewListInjection(judgeMeReviews: AllJudgeMeReviews)
    fun doAllAliReviewListInjection(aliReviews: AllAliReviewsListActivity)
    fun doZoomActivityInjection(base: ZoomActivity)
    fun doBaseActivityInjection(base: NewBaseActivity)
    fun doWishListActivityInjection(wish: WishList)
    fun doCartListActivityInjection(cart: CartList)
    fun doCheckoutWeblinkActivityInjection(cart: CheckoutWeblink)
    fun doAutoSearchActivityInjection(cart: AutoSearch)
    fun doLoginActivtyInjection(loginActivity: LoginActivity)
    fun doRegistrationActivityInjection(registrationActivity: RegistrationActivity)
    fun doLeftMeuInjection(left: LeftMenu)
    fun doUserProfileInjection(profile: UserProfile)
    fun doOrderListInjection(profile: OrderList)
    fun doOrderDetailsInjection(orderDetails: OrderDetails)
    fun doAddressListInjection(addressList: AddressList)
    fun doHomePageInjection(home: HomePage)
    fun doHomePageModelInjection(home: HomePageViewModel)
    fun orderSuccessInjection(orderSuccessActivity: OrderSuccessActivity)
    fun quickAddInjection(quickAddActivity: QuickAddActivity)
    fun doServiceInjection(job: JobScheduler)
    fun doURlInjection(urls: Urls)
    fun doRewarsPointsInjection(rewardsPointActivity: RewardsPointActivity)
    fun doRewarsDashbordInjection(rewardsDashboard: RewardDashboard)
    fun doGetRewadsInjection(getRewardsActivity: GetRewardsActivity)
    fun doEarnRewadsInjection(earnRewardsActivity: EarnRewardsActivity)
    fun doReferFriendInjection(referFriendActivity: ReferFriendActivity)
    fun doMyRewardInjection(myRewardsActivity: MyRewardsActivity)
    fun doFaqsInjection(faqsActivity: FaqsActivity)
}
