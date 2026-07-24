package com.linku.design.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic

/**
 * 확인 버튼 하나만 제공하는 모달 다이얼로그 창입니다. 확인 버튼이 가로로 꽉 차게 표시됩니다.
 *
 * @param visible 모달 창의 표시 여부. true일 때 화면에 나타납니다.
 * @param onOkay '확인' 버튼을 클릭했을 때 실행될 콜백 함수.
 * @param onDismiss 모달을 닫아야 할 때(외부 영역 클릭 시) 실행될 콜백 함수.
 * @param positiveText 확인 버튼에 표시될 텍스트.
 * @param title 모달 상단에 표시될 강조된 제목 텍스트.
 * @param isLogoDimmed 상단 로고를 연한 이미지로 표시할지 여부. 기본값은 false이며, 이 경우 진한 로고 이미지가 표시됩니다.
 * @param textBody 모달 중앙에 배치될 사용자 정의 Composable 본문 내용.
 *
 * @see Dialog
 */
@Composable
fun ModalWindow(
    visible: Boolean,
    onOkay: () -> Unit = {},
    onDismiss: () -> Unit,
    positiveText: String,
    title: String,
    isLogoDimmed: Boolean = false,
    textBody: @Composable () -> Unit
) {
    ModalWindowScaffold(
        visible = visible,
        onDismiss = onDismiss,
        title = title,
        isLogoDimmed = isLogoDimmed,
        textBody = textBody
    ) {
        // 버튼이 확인 하나뿐이므로 전체 너비로 확장
        ModalPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = positiveText,
            onClick = {
                onOkay()
                onDismiss()
            }
        )
    }
}

/**
 * 확인/취소 버튼 두 개를 제공하는 모달 다이얼로그 창입니다.
 *
 * @param visible 모달 창의 표시 여부. true일 때 화면에 나타납니다.
 * @param onOkay '확인' 버튼을 클릭했을 때 실행될 콜백 함수.
 * @param onDismiss 모달을 닫아야 할 때(외부 영역 클릭 시, 또는 두 버튼 클릭 이후) 실행될 콜백 함수. 순수 UI 처리(모달 숨김)만 담당해야 하며, 두 버튼 클릭 시 공통으로 호출되므로 여기에 버튼별 부수 효과를 넣으면 안 됨.
 * @param onNegativeClick 취소 버튼을 클릭했을 때 실행될 콜백 함수. 기본값은 null이며, 이 경우 취소 버튼은 [onDismiss]만 호출하는 기존 동작을 유지함. 취소 버튼도 확인 버튼과 별개의 의미 있는 동작(예: 서버 호출)을 수행해야 하는 경우에만 지정할 것.
 * @param positiveText 확인 버튼에 표시될 텍스트.
 * @param negativeText 취소 버튼에 표시될 텍스트.
 * @param title 모달 상단에 표시될 강조된 제목 텍스트.
 * @param isLogoDimmed 상단 로고를 연한 이미지로 표시할지 여부. 기본값은 false이며, 이 경우 진한 로고 이미지가 표시됩니다.
 * @param textBody 모달 중앙에 배치될 사용자 정의 Composable 본문 내용.
 *
 * @see Dialog
 */
@Composable
fun ModalWindow(
    visible: Boolean,
    onOkay: () -> Unit = {},
    onDismiss: () -> Unit,
    onNegativeClick: (() -> Unit)? = null,
    positiveText: String,
    negativeText: String,
    title: String,
    isLogoDimmed: Boolean = false,
    textBody: @Composable () -> Unit
) {
    ModalWindowScaffold(
        visible = visible,
        onDismiss = onDismiss,
        title = title,
        isLogoDimmed = isLogoDimmed,
        textBody = textBody
    ) {
        // 취소/확인 버튼 두 개를 절반씩 나란히 배치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModalSecondaryButton(
                modifier = Modifier.weight(1f),
                text = negativeText,
                onClick = {
                    onNegativeClick?.invoke()
                    onDismiss()
                }
            )
            ModalPrimaryButton(
                modifier = Modifier.weight(1f),
                text = positiveText,
                onClick = {
                    onOkay()
                    onDismiss()
                }
            )
        }
    }
}

/**
 * [ModalWindow] 두 오버로드가 공유하는 뼈대(다이얼로그, 카드, 로고, 제목, 본문)입니다.
 * 버튼 영역([buttons])은 각 오버로드가 직접 구성하므로 이 함수는 분기 없이 공통 레이아웃만 담당합니다.
 * 각 요소 사이 간격은 고정값이 아닌 콘텐츠 크기에 맞춰 결정되므로, 폰트 크기가 커져도
 * 텍스트가 잘리지 않고 모달 전체 크기가 함께 늘어납니다.
 */
@Composable
private fun ModalWindowScaffold(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    isLogoDimmed: Boolean,
    textBody: @Composable () -> Unit,
    buttons: @Composable () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Basic.white
            ){
                Column(
                    modifier = Modifier
                        .widthIn(min = 372.dp) //TODO : 유지민 - max 지정해주기.
                        .padding(top = 25.dp, bottom = 28.dp, start = 28.dp, end = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 상단 로고 (isLogoDimmed = true일 때 연한 이미지, 기본값(false)일 때 진한 이미지)
                    Image(
                        painter = painterResource(
                            id = if (isLogoDimmed) R.drawable.ic_transparent_logo else R.drawable.ic_logo_color
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .height(32.dp) //TODO : 유지민 - 일요일에 사이즈 체크하기
                    )

                    Spacer(modifier = Modifier.height(20.dp)) //TODO : 맘대로 수정하기

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = LocalFontTheme.current.font,
                        fontWeight = FontWeight(500),
                        color = Basic.black,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    textBody()

                    Spacer(modifier = Modifier.height(28.dp))

                    // 버튼 높이를 고정하지 않고 텍스트 위아래 패딩으로 보장하여 폰트 확대 시에도 잘리지 않도록 함
                    buttons()
                }
            }

        }
    }
}

/**
 * 그라데이션이 채워진 확인(주요 동작) 버튼입니다.
 */
@Composable
private fun ModalPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Basic.maincolor,
                shape = RoundedCornerShape(size = 14.dp)
            )
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(500),
            color = Basic.white,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 테두리만 있는 취소(보조 동작) 버튼입니다. 텍스트에 메인 컬러 그라데이션이 적용됩니다.
 */
@Composable
private fun ModalSecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Basic.maincolor,
                shape = RoundedCornerShape(size = 14.dp)
            )
            .background(
                color = Basic.white,
                shape = RoundedCornerShape(size = 14.dp)
            )
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            // 텍스트(그라데이션 및 스타일 지정)
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        // 폰트 크기 (16sp)
                        fontSize = 16.sp,

                        // 사용할 폰트 (paperlogy 폰트)
                        fontFamily = LocalFontTheme.current.font,

                        // 폰트 굵기 (500)
                        fontWeight = FontWeight(500),

                        // 텍스트 그라데이션 색상(링큐 메인 색상)
                        brush = Basic.maincolor,
                    )
                ) {
                    // 실제 표시할 텍스트
                    append(text)
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "취소/확인 버튼 2개")
@Composable
private fun ModalWindowTwoButtonsPreview() {
    ModalWindow(
        visible = true,
        onOkay = {},
        onDismiss = {},
        positiveText = "확인",
        negativeText = "취소",
        title = "해당 폴더를 비공개 하시겠습니까?"
    ){
        Text(
            text = "해당 폴더는 타인과 공유중인 폴더입니다.\n비공개 폴더로 전환하시겠습니까?",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, name = "삭제하기 버튼 1개(전체 너비 확장)")
@Composable
private fun ModalWindowSingleButtonPreview() {
    ModalWindow(
        visible = true,
        onOkay = {},
        onDismiss = {},
        positiveText = "삭제하기",
        title = "해당 링크를 삭제하시겠습니까?"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, name = "연한 로고 (isLogoDimmed = true)")
@Composable
private fun ModalWindowDimmedLogoPreview() {
    ModalWindow(
        visible = true,
        onOkay = {},
        onDismiss = {},
        positiveText = "확인",
        title = "알림",
        isLogoDimmed = true
    ) {
        Text(
            text = "연한 로고 이미지가 적용된 예시입니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    showBackground = true,
    name = "글자 크기 확대 (fontScale = 2.0)",
    fontScale = 2.0f
)
@Composable
private fun ModalWindowLargeFontScalePreview() {
    ModalWindow(
        visible = true,
        onOkay = {},
        onDismiss = {},
        positiveText = "삭제하기",
        negativeText = "취소하기",
        title = "해당 링크를 삭제하시겠습니까?"
    ) {
        Text(
            text = "삭제 시 해당 링크가 영구적으로 제거되며\n복구가 불가능합니다.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}
