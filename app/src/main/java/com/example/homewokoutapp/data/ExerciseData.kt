package com.example.homewokoutapp.data

import com.example.homewokoutapp.R


enum class MuscleGroup(val displayName: String) {
    CHEST("Mellkas"),
    BACK("Hát"),
    SHOULDERS("Váll"),
    ARMS("Kar"),
    LEGS("Láb"),
    CORE("Has")
}

data class Exercise(
    val name: String,
    val repsGoal: Int,
    val durationSeconds: Int = 30,  // Edzés idő gyakorlatonként
    val muscleGroup: MuscleGroup? = null,
    val videoResId: Int? = null
)

// Izomcsoportok gyakorlatokkal (valódi dumbbell gyakorlatok alapján)
val muscleGroupsExercises = mapOf(
    MuscleGroup.CHEST to listOf(
        Exercise("Kézi súlyzós fekvenyomás", 15, videoResId = R.raw.bench_press_with_dumbbells),
        Exercise("Kézi súlyzós repülés", 12, videoResId = R.raw.hand_weight_flight),
        Exercise("Fekvőtámasz", 20, videoResId = R.raw.push_ups),
        Exercise("Kézi súlyzós ferde padon nyomás", 10, videoResId = R.raw.press_on_an_incline_bench_with_dumbbells)

    ),
    MuscleGroup.BACK to listOf(
        Exercise("Kézi súlyzóval előrehajolva, forditott evezés", 12, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzós előrehajlitott evezés padon", 10, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzós előrehajlitott evezés", 12, videoResId = R.raw.reverse_rowing)
    ),
    MuscleGroup.SHOULDERS to listOf(
        Exercise("Ülve végzett vállnyomás", 10, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzós elötartás", 15, videoResId = R.raw.reverse_rowing),
        Exercise("Álló nyomás fej fölé", 12, videoResId = R.raw.reverse_rowing)
    ),
    MuscleGroup.ARMS to listOf(
        Exercise("Kézi súlyzós bicepsz gördülés", 12, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi kalapács karhajlitás ferde padon", 12, videoResId = R.raw.reverse_rowing),
        Exercise("Hasra fekve végzett ferdepados bicepszgörbités", 10, videoResId = R.raw.reverse_rowing)
    ),
    MuscleGroup.LEGS to listOf(
        Exercise("Kézi súlyzós ülő vádliemelés", 15, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzós pohár guggolás", 10, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzós felhúzás", 20, videoResId = R.raw.reverse_rowing)
    ),
    MuscleGroup.CORE to listOf(
        Exercise("Padló hasprés", 20, videoResId = R.raw.reverse_rowing),
        Exercise("Kézi súlyzóval orosz csavar ", 15, videoResId = R.raw.reverse_rowing),
        Exercise("Deszka", 30, videoResId = R.raw.reverse_rowing)
    )
)

typealias SetData = Pair<Int, Double>  // reps, weight (kg)