package io.github.yunato.myrecordtimer.model.entity

import java.util.*

data class Record(val id: String,
                  val start: Date,
                  val end: Date,
                  val title: String,
                  val memo: String,
                  val eval: Int)
