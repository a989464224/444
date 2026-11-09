package cn.edu.sicnu.cs.stu.yuxingcai.a444

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameValue: TextView
    private lateinit var genderValue: TextView
    private lateinit var phoneValue: TextView
    private lateinit var scoreValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!PlayerStore.initialized) {
            PlayerStore.name = getString(R.string.default_name)
            PlayerStore.gender = getString(R.string.gender_male)
            PlayerStore.phone = getString(R.string.default_phone)
            PlayerStore.highestScore = 0
            PlayerStore.initialized = true
        }

        nameValue = findViewById(R.id.tv_name_value)
        genderValue = findViewById(R.id.tv_gender_value)
        phoneValue = findViewById(R.id.tv_phone_value)
        scoreValue = findViewById(R.id.tv_score_value)

        findViewById<Button>(R.id.btn_edit_info).setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }
        findViewById<Button>(R.id.btn_play_game).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshInfo()
    }

    private fun refreshInfo() {
        nameValue.text = PlayerStore.name
        genderValue.text = PlayerStore.gender
        phoneValue.text = PlayerStore.phone
        scoreValue.text = PlayerStore.highestScore.toString()
    }
}
