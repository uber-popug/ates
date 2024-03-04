package ru.upg.ates

interface Command<D : Domain<D>, Result> {
    abstract fun execute(domain: D): Pair<Result, List<Event>>
}

interface Query<D : Domain<D>, ReadModel> {
    abstract fun execute(domain: D): ReadModel
}
