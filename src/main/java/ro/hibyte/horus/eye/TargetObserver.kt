package ro.hibyte.horus.eye

import one.space.common.event.ContentItemCreatedEvent
import one.space.common.event.ContentItemDeletedEvent
import one.space.common.event.ContentItemEvent
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.ObservesAsync

@ApplicationScoped
class TargetObserver {

    private val targetIds: MutableSet<Number> = Collections.synchronizedSet(HashSet())

    fun getTargetIds() = Collections.unmodifiableSet(targetIds)

    fun observeTargets(@ObservesAsync event: ContentItemEvent) {
        if (event.typeKey == "target") {
            when (event) {
                is ContentItemDeletedEvent -> targetIds.remove(event.itemId)
                is ContentItemCreatedEvent -> targetIds.add(event.itemId)
            }
        }
    }

}