package ru.upg.cqrs

interface ReadModel

abstract class Query<D : Domain<D>, T : ReadModel> {
    abstract fun execute(domain: D): T
}