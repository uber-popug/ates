package ru.upg.ates.common.cqrs

import ru.upg.ates.common.ddd.Domain
import ru.upg.ates.common.events.Event

interface IAggregate {
    companion object : IAggregate
}

abstract class Command<D : Domain<D>, A : IAggregate, Result> {
    abstract val aggregate: A

    abstract fun execute(domain: D): Pair<Result, List<Event>>
}