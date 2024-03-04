package ru.upg.ates.common.ddd

import ru.upg.ates.common.events.EventsBroker


interface Domain<D : Domain<D>> {
    val broker: EventsBroker
}
