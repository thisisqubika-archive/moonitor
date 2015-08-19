package com.mooveit.moonitor.domain.alerts

sealed trait Action

case object Mail extends Action
