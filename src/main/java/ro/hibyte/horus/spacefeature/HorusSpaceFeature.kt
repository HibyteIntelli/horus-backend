package ro.hibyte.horus.spacefeature

import com.fasterxml.jackson.databind.ObjectMapper
import one.space.spi.spacefeatures.SpaceFeature
import one.space.spi.spacefeatures.SpaceFeatureHandler
import one.space.spo.app.domain.AppScope
import one.space.spo.app.service.SpaceFeatureHelperService
import java.io.IOException
import javax.enterprise.context.Dependent
import javax.inject.Inject

@Dependent
@SpaceFeature(value = "Horus")
class HorusSpaceFeature : SpaceFeatureHandler {

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
        return changed
    }

    private fun importKpiCharts(): Boolean {
        spaceFeatureHelperService.importKpiCharts(
            "circularGauge", "barGauge", "lineSeries", "rangeSeries"
        );
        return true;
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