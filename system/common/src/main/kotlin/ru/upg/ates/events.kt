package ru.upg.ates

import com.fasterxml.jackson.annotation.JsonTypeInfo


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
interface Event

/**
 * Marker interface to note Business Events
 */
interface BusinessEvent : Event

/**
 * Marker interface to note CUD (Create, Update, Delete) events
 */
interface CUDEvent : Event