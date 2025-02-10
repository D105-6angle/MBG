package com.ssafy.mbg.ui.modal

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.ModalProfileBinding
import com.ssafy.mbg.databinding.ModalConfirmWithdrawBinding

class ProfileModal(
    context: Context,
    private val email: String,
    private val name: String,
    private val currentNickname: String,
    private val onConfirm: (String) -> Unit,
    private val onLogout: () -> Unit,
    private val onWithdraw: () -> Unit
) : Dialog(context) {

    private var _binding: ModalProfileBinding? = null
    private val binding get() = _binding!!

    private var withdrawConfirmDialog: Dialog? = null
    private var _withdrawBinding: ModalConfirmWithdrawBinding? = null
    private val withdrawBinding get() = _withdrawBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = ModalProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initializeViews()

        window?.let { window ->
            val params = window.attributes
            params.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
            window.attributes = params
        }
    }



    private fun initializeViews() {
        with(binding) {
            // 초기값 설정
            emailTextView.text = email
            nameTextView.text = name
            nicknameEditText.hint = "닉네임 : $currentNickname"

            // 닉네임 입력 필드 설정
            nicknameEditText.apply {
                setText(currentNickname)  // 현재 닉네임을 기본값으로 설정
                setSelection(currentNickname.length)  // 커서를 끝으로 이동
            }

            // 클릭 리스너 설정
            changeNicknameButton.setOnClickListener {
                nicknameEditText.requestFocus()
            }

            confirmButton.setOnClickListener {
                val newNickname = nicknameEditText.text.toString().trim()
                when {
                    newNickname.isBlank() -> {
                        nicknameEditText.error = "닉네임을 입력해주세요"
                    }
                    newNickname == currentNickname -> {
                        nicknameEditText.error = "현재 닉네임과 동일합니다"
                    }
                    newNickname.length < 2 -> {
                        nicknameEditText.error = "닉네임은 2자 이상이어야 합니다"
                    }

                    newNickname.length > 13 -> {
                        nicknameEditText.error = "닉네임은 13자 이하여야 합니다"
                    }
                    else -> {
                        onConfirm(newNickname)
                        dismiss()
                    }
                }
            }

            logoutButton.setOnClickListener {
                onLogout()
                dismiss()
            }

            withdrawButton.setOnClickListener {
                showWithdrawConfirmDialog()
            }
        }
    }

    private fun showWithdrawConfirmDialog() {
        withdrawConfirmDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            _withdrawBinding = ModalConfirmWithdrawBinding.inflate(layoutInflater)
            setContentView(withdrawBinding.root)

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            withdrawBinding.confirmWithdrawButton.setOnClickListener {
                onWithdraw()
                dismiss()
                this@ProfileModal.dismiss()
            }

            window?.let { window ->
                val params = window.attributes
                params.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
                window.attributes = params
            }

            show()
        }
    }

    override fun dismiss() {
        withdrawConfirmDialog?.dismiss()
        super.dismiss()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
        _withdrawBinding = null
    }
}