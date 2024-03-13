package ru.upg.ates

interface Query<D : BoundedContext, ReadModel> : Command<D, ReadModel> {
    interface Silent<D : BoundedContext> : Query<D, Unit> {
        override fun execute(context: D)
    }
}
