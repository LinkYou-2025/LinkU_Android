package com.linku.core.error

class SameNameException(message: String = "이미 존재하는 파일명입니다.") : Exception(message)