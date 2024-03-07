package ru.upg.ates.events


interface Event

/**
 * Marker interface to note Business Events
 */
interface BusinessEvent : Event

/**
 * Marker interface to note CUD (Create, Update, Delete) events
 */
interface CUDEvent : Event
