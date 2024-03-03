package ru.upg.ates

import com.fasterxml.jackson.annotation.JsonTypeInfo
import ru.upg.cqrs.Event

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
interface AtesEvent : Event

/**
 * Marker interface to note Business Events
 */
interface BusinessEvent

/**
 * Marker interface to note CUD (Create, Update, Delete) events
 */
interface CUDEvent