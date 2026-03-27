package com.linku.data.api



// 모든 api를 ServerApi를 하나로 합치면 한 번의 조립으로 자동 상속이 가능함.
interface ServerApi :
    AuthApi,
    UserApi,
    LinkuApi,
    CurationApi,
    AIArticleApi,
    FolderApi,
    CategoryApi


