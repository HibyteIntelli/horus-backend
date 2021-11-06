package ro.hibyte.horus.spacefeature

import com.fasterxml.jackson.databind.ObjectMapper
import one.space.spi.spacefeatures.SpaceFeature
import one.space.spi.spacefeatures.SpaceFeatureHandler
import one.space.spo.app.domain.AppScope
import one.space.spo.app.domain.ContentItem
import one.space.spo.app.service.ContentItemService
import one.space.spo.app.service.SpaceFeatureHelperService
import java.io.IOException
import javax.enterprise.context.Dependent
import javax.inject.Inject

@Dependent
@SpaceFeature(value = "Horus")
class HorusSpaceFeature : SpaceFeatureHandler {

    @Inject
    @Transient
    private lateinit var itemService: ContentItemService

    @Inject
    private lateinit var spaceFeatureHelperService: SpaceFeatureHelperService

    private val WORKFLOWS = arrayOf<String>()

    private val ITEM_TYPES = arrayOf(
        "layout",
        "chartType",
        "locationPoint",
        "location",
        "metric",
        "target",
        "alert",
        "team",
        "chart",
        "dashboard",
        "dashboardCollection"
    )

    private val VIEWS = arrayOf(
        "itemview_layout",
        "itemview_chartType",
        "itemview_location",
        "itemview_metric",
        "itemview_target",
        "itemview_alert",
        "itemview_team",
        "itemview_chart",
        "itemview_dashboard",
        "itemview_dashboardCollection"
    )

    private val SCRIPTS = arrayOf<String>()

    private val ids: Map<Long, Long> = HashMap()

    override fun afterCreation(appScope: AppScope) {
        reinit(appScope)
    }

    override fun reinit(space: AppScope): Boolean {
        spaceFeatureHelperService.setSpace(space)
        var changed = importTypes()
        changed = changed or importWorkflows()
        changed = changed or importScripts()
        changed = changed or importViews()
        changed = changed or importKpis()
        changed = changed or importKpiCharts()
        changed = changed or importDefaultItems(space.scopeKey)
        return changed
    }

    private fun importDefaultItems(scopeKey: String?): Boolean {
        if (scopeKey != null) {
            doItemImport(scopeKey, "chartType", "name")
            doItemImport(scopeKey, "metric", "name")
            return true
        }
        return false
    }

    private fun itemsExist(itemKey: String?, space: String?): Boolean {
        val items: List<ContentItem?> = itemService.filter()
            .typeKey(itemKey)
            .appScopeKey(space)
            .build().toList()
        return !items.isEmpty()
    }

    private fun doItemImport(spaceKey: String, typeKey: String, key: String) {
        if (!itemsExist(typeKey, spaceKey)) {
            spaceFeatureHelperService.importItems(typeKey, key, ids)
        }
    }

    private fun importKpiCharts(): Boolean {
        spaceFeatureHelperService.importKpiCharts(
            "circularGauge", "barGauge", "lineSeries", "rangeSeries"
        )
        return true
    }

    private fun importKpis(): Boolean {
        try {
            val mapper = ObjectMapper()
            val kpis = this.javaClass.getResourceAsStream("/spacedefault/kpis/kpiNames.json")
            if (kpis != null) {
                val kpiNames = mapper.readValue(
                    kpis,
                    Array<String>::class.java
                )
                return spaceFeatureHelperService.kpis().importKpis(*kpiNames).spaceConfigurationChanged()
            }
        } catch (e: IOException) {
            throw IllegalStateException("Could not read default kpi jsons", e)
        }
        return false
    }

    private fun importViews() = spaceFeatureHelperService.itemViews()
                                                         .importViews(*VIEWS)
                                                         .spaceConfigurationChanged()

    private fun importTypes() = spaceFeatureHelperService.contentItemTypes()
                                                         .importEntityItemTypes(*ITEM_TYPES)
                                                         .spaceConfigurationChanged()

    private fun importScripts() = spaceFeatureHelperService.scripts()
                                                           .importEventScripts(*SCRIPTS)
                                                           .spaceConfigurationChanged()

    private fun importWorkflows() = spaceFeatureHelperService.importWorkflows(*WORKFLOWS)
}