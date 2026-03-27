package com.linku.core.model.auth

enum class Job(val id: Int, val displayName: String) {
    NONE(0, "미선택"),
    HIGH_SCHOOL(1, "고등학생"),
    COLLEGE(2, "대학생"),
    WORKER(3, "직장인"),
    SELF_EMPLOYED(4, "자영업자"),
    FREELANCER(5, "프리랜서"),
    JOB_SEEKER(6, "취준생");

    companion object {
        fun getAllJobs(): List<Job> = entries.filter { it != NONE }
        fun fromId(id: Int): Job = entries.find { it.id == id } ?: NONE
    }
}