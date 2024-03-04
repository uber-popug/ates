package ru.upg.common.cqrs

import ru.upg.common.ddd.Domain
import ru.upg.common.events.Event

interface IAggregate

abstract class Command<D : Domain<D>, A : IAggregate, Result> {
    abstract val aggregate: A

    abstract fun execute(domain: D): Pair<Result, List<Event>>
}