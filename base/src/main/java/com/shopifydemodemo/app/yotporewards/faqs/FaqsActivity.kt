package com.shopifydemodemo.app.yotporewards.earnrewards

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityFaqsBinding

class FaqsActivity : NewBaseActivity() {
    private var binding: ActivityFaqsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_faqs, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doFaqsInjection(this)
        showBackButton()
        showTittle(getString(R.string.frequently_asked))
        binding?.webview?.loadDataWithBaseURL(null, "<div class=\"swell-page-content secondary-color\">\n" +
                "        <h2 class=\"swell-question\">How do I participate?</h2>\n" +
                "<p class=\"swell-answer\">Joining is easy! Just click the Create An Account button to get started. Once you're registered with our store, you'll have the opportunity to take part in all of the exciting ways we currently offer to earn points!</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">How can I earn points?</h2>\n" +
                "<p class=\"swell-answer\">You can earn points by participating in any of our innovative promotions! Simply click on the 'Earn Points' tab to view and take part in our current opportunities. In addition, make sure to check back often, as we're adding great new ways for you to earn points all the time!</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">What can I redeem my points for?</h2>\n" +
                "<p class=\"swell-answer\">Glad you asked! We want to make it easy and fun to redeem your hard-earned points. Just visit the 'Get Rewards' tab to view all of our exciting reward options.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">How do I redeem my points?</h2>\n" +
                "<p class=\"swell-answer\">Exchanging your points for great rewards couldn't be easier! Simply visit the 'Get Rewards' tab to view all of our great reward options and click the 'Redeem' button to redeem your reward.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">How do I check my points balance?</h2>\n" +
                "<p class=\"swell-answer\">Your up-to-date points balance is always displayed in the top of this popup.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">Does it cost anything to begin earning points?</h2>\n" +
                "<p class=\"swell-answer\">Absolutely not! Sign up is 100% free, and it will never cost you anything to earn points. Make sure to visit the 'Earn Points' tab to get started.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">Do I have to enroll or register in individual promotions?</h2>\n" +
                "<p class=\"swell-answer\">Once you register for an account, you're all set – we don't require you to register for individual promotions in order to be eligible. Just fulfill the requirements of a promotion, and we'll post the points to your account immediately!</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">How long will it take for points to post to my account?</h2>\n" +
                "<p class=\"swell-answer\">You should receive points in your account instantly once you complete a promotion!</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">Do my points expire?</h2>\n" +
                "<p class=\"swell-answer\">Nope! Your points will never expire.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">What happens to my points if I make a return?</h2>\n" +
                "<p class=\"swell-answer\">When you return an item, you lose the associated credit you originally earned by buying the item in the first place. <br> Sound kind of confusing? Let's take an example: let's say you had previously spent \$50 towards a 'spend \$100, earn 500 points' promotion, and you decide to buy a \$20 item, which bumps you up to \$70. If you decide to return that item, your progress would also go back down to \$50 – it's just like you hadn't bought the item in the first place.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">How do I contact support if I have questions about my points?</h2>\n" +
                "<p class=\"swell-answer\">Our team is ready and waiting to answer your questions about our rewards program! Just send us an email and we'll be in touch.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\"> I'm very close to earning a reward. Can I buy extra points to get there?</h2>\n" +
                "<p class=\"swell-answer\">We currently require you to have enough points to redeem any of the awards you see listed on the 'Get Rewards' tab.</p>\n" +
                "\n" +
                "<h2 class=\"swell-question\">What if I don't want to receive promotional emails?</h2>\n" +
                "<p class=\"swell-answer\">From time to time, you'll receive program-related emails from us. If you'd prefer to not receive those types of emails anymore, just click the 'Unsubscribe' button when you receive your next email.</p>\n" +
                "\n" +
                "      </div>", "text/html", "utf-8", null)
    }
}