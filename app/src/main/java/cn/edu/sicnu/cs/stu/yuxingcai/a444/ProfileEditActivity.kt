package cn.edu.sicnu.cs.stu.yuxingcai.a444

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var genderGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        nameInput = findViewById(R.id.et_name)
        phoneInput = findViewById(R.id.et_phone)
        genderGroup = findViewById(R.id.rg_gender)
        val maleId = findViewById<RadioButton>(R.id.rb_male).id
        val femaleId = findViewById<RadioButton>(R.id.rb_female).id

        nameInput.setText(PlayerStore.name)
        phoneInput.setText(PlayerStore.phone)
        genderGroup.check(if (PlayerStore.gender == getString(R.string.gender_male)) maleId else femaleId)

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            PlayerStore.name = nameInput.text.toString().trim()
            PlayerStore.phone = phoneInput.text.toString().trim()
            PlayerStore.gender =
                if (genderGroup.checkedRadioButtonId == maleId) getString(R.string.gender_male)
                else getString(R.string.gender_female)
            finish()
        }
        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }
}
