package com.example.schooldashboardstaff.utils

object Constants {
    const val USERS_COLLECTION = "users"
    const val USERNAMES_COLLECTION = "usernames"

    const val USERS_FIELD_USERNAME = "username"
    const val USERS_FIELD_EMAIL = "email"
    const val USERS_FIELD_ROLE = "role"
    const val USERS_FIELD_UID = "uid"
    const val USERS_FIELD_FIRST_NAME = "firstname"
    const val USERS_FIELD_LAST_NAME = "lastname"
    const val USERS_FIELD_SCHOOL_ID = "schoolId"

    const val ROLE_SUPER_ADMIN = "SUPER_ADMIN"
    const val ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN"
    const val ROLE_TEACHER = "TEACHER"
    const val ROLE_STUDENT = "STUDENT"

    const val USER_OBJECT_INTENT_KEY = "user"

    const val SCHOOL_OBJECT_INTENT_KEY = "school"

    const val SCHOOLS_COLLECTION = "schools"

    const val SCHOOL_ID_KEY = "SCHOOL_ID"
    const val SCHOOL_FIELD_NAME_KEY = "SCHOOL_NAME"
    const val SCHOOL_FIELD_LOCATION_KEY = "SCHOOL_LOCATION"
    const val SCHOOL_FIELD_GRADE_START_KEY = "GRADE_START"
    const val SCHOOL_FIELD_GRADE_END_KEY = "GRADE_END"

    const val MODE_KEY = "MODE"
    const val MODE_ASSIGN = "assign_admin"
    const val MODE_EDIT = "edit_school"
    const val MODE_ADD = "add_school"

    const val SCHOOL_CLASSES_COLLECTION = "school_classes"

    const val SCHOOL_CLASSES_FIELD_GRADE = "grade"
    const val GRADE_KEY = "grade"

    const val PERIODS_LEFT_KEY = "periodsLeft"
    const val SCHOOL_CLASSES_FIELD_SECTION = "section"

    const val INTENT_SCHOOL_ID = "school_id"
    const val INTENT_CLASS_OBJECT = "school_class_object"
    const val GRADE_LOWER_LIMIT = 1
    const val GRADE_UPPER_LIMIT = 12

    const val SUBJECTS_COLLECTION = "subjects"
    const val MAX_PERIODS_KEY = "max_periods"

    const val CLASS_ID_KEY = "class_id"

    const val SEARCH_TYPE_KEY = "search_type"

    const val SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY = "SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY"
    const val SEARCH_SUBJECTS = "SEARCH_SUBJECTS"

    const val SUBJECT_OBJECT_KEY = "subject_object_key"
    const val CLASS_OBJECT_KEY = "class_object_key"

    const val SEARCH_SUBJECTS_FOR_TEACHER = "SEARCH_SUBJECTS_FOR_TEACHER"
    const val RESULT_SELECTED_SUBJECT_IDS = "result_selected_subject_ids"

    const val RESULT_SUBJECT_TO_CLASS_MAP = "result_subject_to_class_map"
    const val RESULT_TOTAL_ASSIGNED_PERIODS = "result_total_assigned_periods"




    const val SEARCH_TEACHERS = "SEARCH_TEACHERS"
    const val SEARCH_CLASS_TEACHER = "SEARCH_CLASS_TEACHER"
    const val SEARCH_CLASSES = "SEARCH_CLASSES"
    const val SEARCH_STUDENTS = "SEARCH_STUDENTS"




}
