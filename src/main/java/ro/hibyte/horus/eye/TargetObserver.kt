package ro.hibyte.horus.eye

import one.space.common.event.ContentItemCreatedEvent
import one.space.common.event.ContentItemDeletedEvent
import one.space.common.event.ContentItemEvent
import one.space.spo.app.domain.ContentItem
import one.space.spo.app.service.ContentItemService
import ro.hibyte.horus.utils.supportedKpis
import java.util.*
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.event.ObservesAsync
import javax.inject.Inject

@Dependent
class TargetObserver {

    private lateinit var targetIds: MutableSet<Long>

    @Inject
    private lateinit var itemService: ContentItemService

    @Inject
    private lateinit var dataLoader: DataLoader

    @PostConstruct
    fun init() {
        this.targetIds = Collections.synchronizedSet(
                itemService.filter()
                .appScopeKey("point")
                .typeKey("target")
                .build()
                .toIdList()
                .toMutableSet()
        )
        loadData()
    }

    fun getTargetIds() = Collections.unmodifiableSet(targetIds)

    fun observeTargets(event: ContentItemEvent) {
        if (event.typeKey == "target") {
            when (event) {
                is ContentItemDeletedEvent -> targetIds.remove(event.itemId)
                is ContentItemCreatedEvent -> targetIds.add(event.itemId)
            }
        }
    }

    fun loadData() {
        this.targetIds.forEach(this::loadData)
    }

    fun loadData(id: Long) {
        itemService.findById(id)
                .ifPresentOrElse(
                        { item ->  },
                        { targetIds.remove(id) }
                )
    }

    fun loadData(item: ContentItem) =
        supportedKpis.forEach { dataLoader.loadData(item, it) }

}