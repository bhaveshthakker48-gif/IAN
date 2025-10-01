package org.bombayneurosciences.bna_2023.Model.Submitanswer

data class submit(
    val event_id : String,
   val session_id : String,
    val topic_id: String,
  val  question_id : String,
  val  question : String,
   val user_id : String,
  val  cd_start_time : String,
 val   is_correct : String,
 val   answer : String,
 val   is_dummy :String
)
