package com.mooveit.moonitor.domain.alerts

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS

@JsonTypeInfo(use = CLASS)
sealed trait Operator {

  def eval(a: Any, b: Any) = a match {
    case aInt: Int => doEval(aInt.compareTo(b.asInstanceOf[Int]))
    case aLong: Long => doEval(aLong.compareTo(b.asInstanceOf[Long]))
    case aFloat: Float => doEval(aFloat.compareTo(b.asInstanceOf[Float]))
    case aDouble: Double => doEval(aDouble.compareTo(b.asInstanceOf[Double]))
  }

  private def doEval(value: Int) = compareImpl()(value, 0)

  protected def compareImpl(): (Int, Int) => Boolean
}

case object Gt extends Operator {

  override def toString = ">"
  
  override def compareImpl() = _ > _
}

case object Ge extends Operator {

  override def toString = ">="
  
  override def compareImpl() = _ >= _
}

case object Eq extends Operator {

  override def toString = "="
  
  override def compareImpl() = _ == _
}

case object Lt extends Operator {

  override def toString = "<"
  
  override def compareImpl() = _ < _
}

case object Le extends Operator {

  override def toString = "<="
  
  override def compareImpl() = _ <= _
}
