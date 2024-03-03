package ru.upg.common.ddd

import ru.upg.common.events.EventsBroker


interface Domain<D : Domain<D>> {
    val broker: EventsBroker
}
