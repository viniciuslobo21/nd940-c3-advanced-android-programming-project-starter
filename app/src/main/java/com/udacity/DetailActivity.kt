package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

private val STATUS = "download_status"
private val NAME = "download_name"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        getIntentExtras()
        initButtonListener()
    }

    private fun getIntentExtras() {
        if (intent.hasExtra(STATUS))
            if (intent.getBooleanExtra(STATUS, false)) {
                status.text = getString(R.string.txt_success)
                status.setTextColor(getColor(R.color.darkGreen))
            } else {
                status.text = getString(R.string.txt_error)
                status.setTextColor(getColor(R.color.red))
            }
        if (intent.hasExtra(NAME)) {
            name.text = intent.getStringExtra(NAME)
        }
    }

    private fun initButtonListener() {
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
