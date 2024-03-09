package ru.upg.ates.auth

import ru.upg.ates.BoundedContext
import ru.upg.ates.Event
import ru.upg.ates.Topic
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.UserCreated
import kotlin.reflect.KClass

class AuthContext(
    override val serviceName: String,
    override val broker: EventsBroker
) : BoundedContext
