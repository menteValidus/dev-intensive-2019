package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    private val invalidNameString = "Имя должно начинаться с заглавной буквы"
    private val invalidProfessionString = "Профессия должна начинаться со строчной буквы"
    private val invalidMaterialString = "Материал не должен содержать цифр"
    private val invalidBDayString = "Год моего рождения должен содержать только цифры"
    private val invalidSerialString = "Серийный номер содержит только цифры, и их 7"

    private val wrongAnswerMessage = "Это неправильный ответ"

    fun askQuestion(): String =  when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer:String): Pair<String, Triple<Int, Int, Int>> {

        val invalidAnswer = validateAnswer(answer, question)
        val answer = answer.toLowerCase()

        return if (invalidAnswer != null) {
            "$invalidAnswer\n${question.question}" to status.color
        } else if (question.answers.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {

            if (question != Question.IDLE) {
                if (status == Status.CRITICAL) {
                    status = Status.NORMAL
                    question = Question.NAME
                    "$wrongAnswerMessage. Давай все по новой\n${question.question}" to status.color
                } else {
                    status = status.nextStatus()
                    "$wrongAnswerMessage\n${question.question}" to status.color
                }
            } else {
                "Отлично - ты справился\n${question.question}" to status.color
            }

        }

    }

    private fun validateAnswer(answer: String, question: Question): String? {

        if (answer.isEmpty()) return null

        when (question) {
            Question.NAME -> {
                if (!answer[0].isUpperCase())
                    return invalidNameString
            }
            Question.PROFESSION -> {
                if (answer[0].isUpperCase())
                    return invalidProfessionString
            }
            Question.MATERIAL -> {
                answer.forEach { w -> if (w.isDigit()) return invalidMaterialString }
            }
            Question.BDAY -> {
                answer.forEach { w -> if (!w.isDigit()) return invalidBDayString }
            }
            Question.SERIAL -> {
                if (answer.length == 7) {
                    answer.forEach { w -> if (!w.isDigit()) return invalidSerialString }
                } else {
                    return invalidSerialString
                }
            }
        }

        return null
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)) ,
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0)) ;

        fun  nextStatus(): Status {

            return if (this.ordinal < values().lastIndex)  {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }

        }
    }

    enum class Question(val question: String, val answers:List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun nextQuestion(): Question =  PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")){
            override fun nextQuestion(): Question =  MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")){
            override fun nextQuestion(): Question =  BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")){
            override fun nextQuestion(): Question =  SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")){
            override fun nextQuestion(): Question =  IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()){
            override fun nextQuestion(): Question =  IDLE
        };

        abstract fun nextQuestion(): Question
    }
}