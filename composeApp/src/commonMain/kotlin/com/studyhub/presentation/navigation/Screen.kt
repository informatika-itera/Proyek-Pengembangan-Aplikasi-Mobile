package com.studyhub.presentation.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main?openPomodoro={openPomodoro}") {
        fun createRoute(openPomodoro: Boolean = false) = "main?openPomodoro=$openPomodoro"
    }
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object AddTask : Screen("add_task?date={date}") {
        fun createRoute(date: String? = null) =
            if (date != null) "add_task?date=$date" else "add_task"
    }
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }
    object SmartPriority : Screen("smart_priority")
    object NotifHistory : Screen("notif_history")
    object Progress : Screen("progress")
    object Report : Screen("report")
    object Pomodoro : Screen("pomodoro?taskId={taskId}") {
        fun createRoute(taskId: String? = null) =
            if (taskId != null) "pomodoro?taskId=$taskId"
            else "pomodoro"
    }
}
