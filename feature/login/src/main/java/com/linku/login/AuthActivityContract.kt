package com.linku.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * 로그인 Activity([AuthActivity])를 열고 로그인 성공 여부를 돌려받는 계약입니다.
 *
 * 결과는 로그인에 성공해 [Activity.RESULT_OK]로 종료되면 `true`, 그 외에는 `false`입니다.
 */
class AuthActivityContract : ActivityResultContract<Unit, Boolean>() {

    /**
     * 로그인 Activity를 시작하는 [Intent]를 만듭니다.
     *
     * @param context Intent를 만들 [Context]
     * @param input 사용하지 않는 입력
     * @return 로그인 Activity를 시작하는 Intent
     */
    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(context, AuthActivity::class.java)

    /**
     * 로그인 Activity가 돌려준 결과 코드를 성공 여부로 변환합니다.
     *
     * @param resultCode Activity 종료 시 설정된 결과 코드
     * @param intent 사용하지 않는 결과 데이터
     * @return 로그인에 성공했으면 `true`
     */
    override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
        resultCode == Activity.RESULT_OK
}
