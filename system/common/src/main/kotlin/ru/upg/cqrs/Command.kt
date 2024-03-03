package ru.upg.cqrs

interface IAggregate

abstract class Command<D : Domain<D>, A : IAggregate> {
    abstract val aggregate: A

    abstract fun execute(domain: D): List<Event>
}