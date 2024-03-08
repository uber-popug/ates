package ru.upg.ates

interface Command<D : BoundedContext, Result> {
    fun execute(context: D): Result

    interface Silent<D : BoundedContext> : Command<D, Unit> {
        override fun execute(context: D)
    }
}

