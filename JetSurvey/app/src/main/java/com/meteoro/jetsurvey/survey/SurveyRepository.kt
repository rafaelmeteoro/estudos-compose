package com.meteoro.jetsurvey.survey

import android.os.Build
import com.meteoro.jetsurvey.R

// Static data of questions
private val jetpackQuestions = mutableListOf(
    Question(
        id = 1,
        questionText = R.string.in_my_free_time,
        answer = PossibleAnswer.MultipleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games,
                R.string.dance,
                R.string.watch_movies
            )
        ),
        description = R.string.select_all
    ),
    Question(
        id = 2,
        questionText = R.string.pick_superhero,
        answer = PossibleAnswer.SingleChoice(
            optionsStringRes = listOf(
                R.string.spiderman,
                R.string.ironman,
                R.string.unikitty,
                R.string.captain_planet
            )
        ),
        description = R.string.select_one
    ),
    Question(
        id = 7,
        questionText = R.string.favourite_movie,
        answer = PossibleAnswer.SingleChoice(
            listOf(
                R.string.star_trek,
                R.string.social_network,
                R.string.back_to_future,
                R.string.outbreak
            )
        ),
        description = R.string.select_one
    ),
    Question(
        id = 3,
        questionText = R.string.takeaway,
        answer = PossibleAnswer.Action(label = R.string.pick_date, actionType = SurveyActionType.PICK_DATE),
        description = R.string.select_date
    ),
    Question(
        id = 4,
        questionText = R.string.selfies,
        answer = PossibleAnswer.Slider(
            range = 1f..10f,
            steps = 3,
            startText = R.string.selfie_min,
            endText = R.string.selfie_max
        )
    )
).apply {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        // Add the camera feature only for devices 29+
        add(
            Question(
                id = 975,
                questionText = R.string.selfie_skills,
                answer = PossibleAnswer.Action(label = R.string.add_photo, actionType = SurveyActionType.TAKE_PHOTO)
            )
        )
    }
}.toList()

private val jetpackSurvey = Survey(
    title = R.string.which_jetpack_library,
    questions = jetpackQuestions
)

object SurveyRepository {

    suspend fun getSurvey() = jetpackSurvey

    @Suppress("UNUSED_PARAMETER")
    fun getSurveyResult(answers: List<Answer<*>>): SurveyResult {
        return SurveyResult(
            library = "Compose",
            result = R.string.survey_result,
            description = R.string.survey_result_description
        )
    }
}