package ru.upg.ates

import ru.upg.ates.events.Event

interface Command<D : Domain, Result> {
    abstract fun execute(domain: D): Pair<Result, List<Event<*>>>
}

interface Query<D : Domain, ReadModel> {
    abstract fun execute(domain: D): ReadModel
}
