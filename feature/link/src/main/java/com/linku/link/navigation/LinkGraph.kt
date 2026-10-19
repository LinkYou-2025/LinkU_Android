package com.linku.link.navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.linku.core.model.CategoryType
import com.linku.core.model.LinkItemInfo
import com.linku.core.navigation.LinkuRoute
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.core.util.toImageUrl
import com.linku.data.util.toCategoryColorStyleMap
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.link.component.LinkCategoryOption
import com.linku.link.screen.LinkDetailLoadErrorScreen
import com.linku.link.screen.LinkDetailLoadingScreen
import com.linku.link.screen.LinkDetailScreen
import com.linku.link.screen.SaveLinkScreen
import com.linku.link.screen.SharedLinkDetailScreen
import com.linku.link.screen.verifiedLinkDetailJobId
import com.linku.link.util.toTempFile
import com.linku.link.viewmodel.AIArticleViewModel
import com.linku.link.viewmodel.LinkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 링크 저장, 링크 상세, 공유폴더 링크 상세 화면을 내비게이션 그래프에 등록합니다.
 *
 * 링크 상태는 홈·마이페이지·큐레이션과 공유되므로 [linkViewModel]은 앱이 생성해 전달하며, 홈 데이터
 * 갱신처럼 다른 모듈의 상태가 필요한 부분은 콜백으로 이어받습니다.
 *
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param linkViewModel 링크 저장, 조회, 수정, 삭제 상태를 제공하는 [LinkViewModel]
 * @param selectedSharedLink 공유폴더 링크 상세 화면에 표시할 링크. 없으면 화면을 닫습니다.
 * @param onLinkUpdated 링크 수정에 성공했을 때 호출하는 함수
 * @param onLinkDeleted 링크 삭제에 성공했을 때 호출하는 함수
 */
fun NavGraphBuilder.linkGraph(
    navigator: NavHostController,
    linkViewModel: LinkViewModel,
    selectedSharedLink: () -> LinkItemInfo?,
    onLinkUpdated: () -> Unit,
    onLinkDeleted: () -> Unit,
) {
    composable(LinkRoute.SAVE_LINK) {
        val context = LocalContext.current
        val linkUiState by linkViewModel.uiState.collectAsStateWithLifecycle()

        fun exitSaveLinkScreen() {
            linkViewModel.resetSaveForm()
            navigator.popBackStack()
        }

        BackHandler {
            exitSaveLinkScreen()
        }

        SaveLinkScreen(
            image = linkUiState.saveImage,
            url = linkUiState.saveUrl,
            title = linkUiState.saveTitle,
            memo = linkUiState.saveMemo,
            selectedEmotionId =
                linkUiState.selectedSaveEmotionId,
            selectedSituationId =
                linkUiState.selectedSaveSituationId,
            jobId = linkUiState.jobId ?: 3L,
            onImageSelected = linkViewModel::setSaveImage,
            onImageLoadFailed = {
                Toast.makeText(
                    context,
                    "이미지 로드에 실패했습니다.",
                    Toast.LENGTH_SHORT,
                ).show()
            },
            onDeleteImage = linkViewModel::deleteSaveImage,
            onUrlChange = linkViewModel::setSaveUrl,
            onTitleChange = linkViewModel::setSaveTitle,
            onMemoChange = linkViewModel::setSaveMemo,
            onEmotionSelect =
                linkViewModel::selectSaveEmotion,
            onSituationClick =
                linkViewModel::onSaveSituationClick,
            onBack = {
                exitSaveLinkScreen()
            },
            isSaveButtonEnabled =
                linkUiState.isSaveButtonEnabled,
            onSaveButtonClick = {
                linkViewModel.onSaveButtonClick(
                    onSucceed = { saved ->
                        linkViewModel.loadLinkDetail(
                            saved.userLinkuId,
                        )
                        linkViewModel.resetSaveForm()

                        navigator.navigate(
                            LinkRoute.linkDetail(saved.userLinkuId),
                        ){
                            popUpTo(LinkuRoute.HOME)
                            launchSingleTop = true
                        }
                    },
                    onFailed = { error ->
                        Log.e(
                            "SaveLink",
                            "failed",
                            error,
                        )
                    },
                )
            },
            toastEvent = linkViewModel.toastEvent,
        )
    }


    composable(
        route = LinkRoute.LINK_DETAIL_PATTERN,
        arguments = listOf(
            navArgument(LinkRoute.ARG_USER_LINKU_ID) {
                type = NavType.LongType
            }
        )
    ) { backStackEntry ->
        val context = LocalContext.current
        val detailCoroutineScope = rememberCoroutineScope()
        val linkUiState by linkViewModel.uiState.collectAsStateWithLifecycle()

        val userLinkuId = backStackEntry.arguments
            ?.takeIf { it.containsKey(LinkRoute.ARG_USER_LINKU_ID) }
            ?.getLong(LinkRoute.ARG_USER_LINKU_ID)
            ?.takeIf { it > 0L }
            ?: return@composable

        val aiArticleViewModel: AIArticleViewModel = hiltViewModel(backStackEntry)
        val aiArticleUiState by aiArticleViewModel.uiState.collectAsStateWithLifecycle()

        var selectedDetailImageUri by rememberSaveable(userLinkuId) {
            mutableStateOf<Uri?>(null)
        }
        var detailUserJobRequestFloor by remember(backStackEntry.id) {
            mutableStateOf<Long?>(null)
        }

        val detailImagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedDetailImageUri = uri
        }

        LaunchedEffect(backStackEntry.id, userLinkuId) {
            // 같은 링크를 다시 열어도 이번 상세 진입 이후 성공한 직업 조회만 사용합니다.
            detailUserJobRequestFloor = linkViewModel.loadUserBasics()
            linkViewModel.loadLinkDetail(userLinkuId)
            linkViewModel.loadLinkEditCategories()
        }

        fun emotionNameOf(id: Long?): String {
            return when (id) {
                1L -> "즐거움"
                2L -> "평온"
                3L -> "설렘"
                4L -> "슬픔"
                5L -> "짜증"
                6L -> "분노"
                else -> "감정"
            }
        }

        val linkEditCategories = linkUiState.linkEditCategories
        val fallbackCategoryColorStyle = CategoryColorStyle.DEFAULT

        // 기존 공통 매핑을 읽기 전용으로 재사용해 서버의 네 가지 색상 단계를 보존합니다.
        val categoryColorMap = remember(linkEditCategories) {
            runCatching {
                linkEditCategories.toCategoryColorStyleMap()
            }.getOrDefault(emptyMap())
        }

        // 원본 목록 순서와 실제 ID를 유지하면서 이름과 색상 스타일을 UI 항목으로 묶습니다.
        val categoryOptions = remember(
            linkEditCategories,
            categoryColorMap,
            fallbackCategoryColorStyle,
        ) {
            linkEditCategories.map { category ->
                LinkCategoryOption(
                    id = category.categoryId,
                    name = category.categoryName,
                    colorStyle = categoryColorMap[category.categoryName]
                        ?: fallbackCategoryColorStyle,
                )
            }
        }

        // 라우트 ID와 일치하는 상세만 실제 화면에 전달해 이전 링크가 한 프레임도 노출되지 않게 합니다.
        val linkDetail = linkUiState.linkDetail?.takeIf { detail ->
            detail.userLinkuId == userLinkuId
        }
        val detailJobId = verifiedLinkDetailJobId(
            jobId = linkUiState.jobId,
            minimumUserJobRequestId = detailUserJobRequestFloor,
            currentUserJobRequestId = linkUiState.userJobRequestId,
            isUserJobReady = linkUiState.isUserJobReady,
        )
        val verifiedDetailUserJobRequestId =
            linkUiState.userJobRequestId.takeIf { detailJobId != null }

        LaunchedEffect(
            linkDetail?.keyword,
            linkDetail?.summary,
        ) {
            aiArticleViewModel.setLinkContent(
                keyword = linkDetail?.keyword,
                summary = linkDetail?.summary,
            )
        }

        when {
            linkDetail != null -> {
                // 동일 ID 콘텐츠는 서버 재검증 중에도 유지하며 스켈레톤으로 다시 덮지 않습니다.
                LinkDetailScreen(
                    userLinkuId = userLinkuId,
                    linkTitle = linkDetail.title,
                    categoryId = linkDetail.categoryId,
                    emotion = emotionNameOf(linkDetail.emotionId),
                    situationId = linkDetail.situationId,
                    jobId = detailJobId,
                    linkUrl = linkDetail.linku,
                    imageUrl = linkDetail.linkuImageUrl.toImageUrl(),
                    selectedImageUri = selectedDetailImageUri,
                    memo = linkDetail.memo.orEmpty(),
                    tags = aiArticleUiState.displayTags,
                    aiSummary = aiArticleUiState.displaySummary,
                    isAiArticleLoading = aiArticleUiState.isLoading,
                    aiArticleErrorMessage = aiArticleUiState.errorMessage,
                    onRequestAiArticle = aiArticleViewModel::getAiArticle,
                    onClearAiArticleError =
                        aiArticleViewModel::clearErrorMessage,
                    categoryOptions = categoryOptions,
                    onBack = {
                        navigator.popBackStack()
                    },
                    onPickImage = {
                        detailImagePicker.launch("image/*")
                    },
                    onDiscardSelectedImage = {
                        selectedDetailImageUri = null
                    },
                    onSubmitEdit = { title, memo, categoryId, emotionId, situationIdToUpdate, onSuccess, onFailed ->
                        val expectedUserJobRequestId =
                            verifiedDetailUserJobRequestId
                        detailCoroutineScope.launch {
                            val selectedTempImage = runCatching {
                                withContext(Dispatchers.IO) {
                                    selectedDetailImageUri?.toTempFile(context)
                                }
                            }.getOrElse { error ->
                                LinkuLog.e(
                                    "LinkDetail",
                                    "selected image conversion failed",
                                    error,
                                )
                                onFailed()
                                return@launch
                            }

                            linkViewModel.updateLink(
                                expectedUserLinkuId = userLinkuId,
                                image = selectedTempImage,
                                title = title,
                                memo = memo,
                                categoryId = categoryId,
                                emotionId = emotionId,
                                situationIdToUpdate = situationIdToUpdate,
                                expectedUserJobRequestId = expectedUserJobRequestId,
                                onSucceed = {
                                    selectedDetailImageUri = null
                                    onLinkUpdated()
                                    onSuccess()
                                },
                                onFailed = { error ->
                                    LinkuLog.e(
                                        "LinkDetail",
                                        "update failed",
                                        error,
                                    )
                                    onFailed()
                                },
                            )
                        }
                    },
                    onDeleteLink = { onSuccess, onFailed ->
                        linkViewModel.deleteCurrentLink(
                            onSucceed = {
                                onLinkDeleted()
                                onSuccess()
                            },
                            onFailed = {
                                onFailed()
                            },
                        )
                    }
                )
            }

            linkUiState.requestedLinkDetailId == userLinkuId &&
                !linkUiState.isLoadingLinkDetail &&
                linkUiState.linkDetailLoadError != null -> {
                LinkDetailLoadErrorScreen(
                    onBack = {
                        navigator.popBackStack()
                    },
                    onRetry = {
                        linkViewModel.loadLinkDetail(
                            userLinkuId = userLinkuId,
                            forceRefresh = true,
                        )
                    },
                )
            }

            else -> {
                // 첫 composition에서도 ID가 다른 이전 상세 대신 곧바로 스켈레톤을 렌더링합니다.
                LinkDetailLoadingScreen(
                    onBack = {
                        navigator.popBackStack()
                    },
                )
            }
        }
    }

    composable(route = LinkRoute.SHARED_LINK_DETAIL) {
        val sharedLink = selectedSharedLink()

        if (sharedLink == null) {
            LaunchedEffect(Unit) { navigator.popBackStack() }
        } else {
            // 서버 카테고리 목록은 소유자별로 다를 수 있어, 고정된 16종 카테고리
            // 마스터(CategoryType)로 categoryId를 이름·색상에 매핑합니다.
            val sharedLinkCategoryType = sharedLink.categoryId?.let { categoryId ->
                CategoryType.fromId(categoryId)
            }

            SharedLinkDetailScreen(
                linkTitle = sharedLink.title,
                linkUrl = sharedLink.url,
                imageUrl = sharedLink.linkuImageUrl,
                tags = sharedLink.tags,
                categoryName = sharedLinkCategoryType?.tagName ?: "카테고리",
                categoryColorStyle = sharedLinkCategoryType?.let { categoryType ->
                    CategoryColorStyle.categoryStyleList.getOrNull(categoryType.ordinal)
                } ?: CategoryColorStyle.DEFAULT,
                onBack = { navigator.popBackStack() },
            )
        }
    }
}
