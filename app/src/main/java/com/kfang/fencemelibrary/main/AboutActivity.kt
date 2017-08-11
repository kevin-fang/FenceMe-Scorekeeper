package com.kfang.fencemelibrary.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.kfang.fencemelibrary.BuildConfig
import com.kfang.fencemelibrary.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

/*
 * Simple about page.
 */

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.about)
        }

        val versionElement = Element()
        val versionName = BuildConfig.VERSION_NAME
        versionElement.title = "Version: " + versionName

        val page = AboutPage(this)
                .isRTL(false)
                .setDescription(resources.getString(R.string.about_text))
                .setImage(R.drawable.about_app_feature)
                .addItem(versionElement)
                .addGroup("Connect with us:")
                .addEmail("fenceme@helionlabs.com")

        if (!MainActivity.isPro(this)) {
            page.addPlayStore("com.kfang.fenceme")
        } else {
            page.addPlayStore("com.helionlabs.fencemepro")
        }

        val aboutPage = page.create()
        setContentView(aboutPage)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    fun rateApp(v: View) {
        val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)))
        }

    }

}
