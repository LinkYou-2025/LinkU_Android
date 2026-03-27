package com.linku.design.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * 시각적 효과(Ripple)가 없는 클릭 이벤트를 적용하는 [Modifier]의 확장 함수.
 *
 * @param enabled 클릭 가능 여부를 설정합니다. false일 경우 클릭 이벤트가 발생하지 않습니다.
 * @param onClickLabel 접근성 서비스를 위한 클릭 작업에 대한 설명 레이블입니다.
 * @param role 요소의 역할(예: Button, RadioButton 등)을 정의하여 접근성 서비스에 정보를 제공합니다.
 * @param onClick 요소가 클릭되었을 때 실행될 콜백 함수입니다.
 *
 * @return 시각적 효과가 없는 클릭 가능한 [Modifier].
 */
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        /*
         * interactionSource와 indication의 값을 고정하여 리플 효과를 제거합니다.
         * remember { MutableInteractionSource() }를 통해 상호작용 상태를 관리하는 객체를 생성하고,
         * indication에 null을 전달하여 클릭 시 시각적 효과가 발생하지 않도록 설정합니다.
         */
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}
