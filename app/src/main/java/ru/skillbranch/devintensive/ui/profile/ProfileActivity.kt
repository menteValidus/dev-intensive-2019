package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.utils.Utils.convertSpToPixel
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    lateinit var viewFields: Map<String, TextView>

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
        const val INVALID_REPOSITORY_MESSAGE = "Невалидный адрес репозитория"
    }

    var isEditMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO set custom Theme this before super and setContentView

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity","onCreate")

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(IS_EDIT_MODE, isEditMode)

    }

    private  fun  initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity","updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k,v) in viewFields) {
                v.text = it[k].toString()
            }
        }

        val bgColor = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, bgColor, true)

        iv_avatar.setImageBitmap(Utils.textAsBitmap(
                iv_avatar.layoutParams.width,
                iv_avatar.layoutParams.height,
                Utils.toInitials(et_first_name.text.toString(), et_last_name.text.toString())!!,
                convertSpToPixel(48f, this),
                Color.WHITE,
                bgColor.data))
        iv_avatar.setupBitmap()
    }

    private fun initViews(savedInstanceState: Bundle?) {

        viewFields = mapOf(
            "nickname" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)


        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (isEditMode) {

                    if (Utils.checkGithubUrl(s.toString())) {
                        wr_repository.error = null
                        wr_repository.isErrorEnabled = false
                    } else {
                        wr_repository.error = INVALID_REPOSITORY_MESSAGE
                        wr_repository.isErrorEnabled = true
                    }

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        btn_edit.setOnClickListener {

            if (isEditMode) saveProfileInfo()

            isEditMode = !isEditMode
                showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }

        for ((_,v) in info) {
            v as EditText
            v.isFocusable = isEdit;
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            val filter : ColorFilter? = if (isEdit){
                PorterDuffColorFilter(
                    typedValue.data,
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }

    }

    private fun  saveProfileInfo() {

        if (wr_repository.error != null) {
            et_repository.setText("")
        }

        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
            tv_nick_name.text = this.nickname
        }
    }
}

