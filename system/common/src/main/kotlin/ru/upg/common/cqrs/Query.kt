package ru.upg.common.cqrs

import ru.upg.common.ddd.Domain

interface ReadModel

abstract class Query<D : Domain<D>, T : ReadModel> {
    abstract fun execute(domain: D): T
}