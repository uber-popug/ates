package ru.upg.ates.common.cqrs

import ru.upg.ates.common.ddd.Domain

interface ReadModel

abstract class Query<D : Domain<D>, T : ReadModel> {
    abstract fun execute(domain: D): T
}