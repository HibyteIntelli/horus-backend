package ro.hibyte.horus.spacefeature

import one.space.spi.spacefeatures.SpaceFeature
import one.space.spi.spacefeatures.SpaceFeatureHandler
import one.space.spo.app.domain.AppScope
import one.space.spo.app.service.SpaceFeatureHelperService
import javax.enterprise.context.Dependent
import javax.inject.Inject

@Dependent
@SpaceFeature(value = "Horus")
class HorusSpaceFeature : SpaceFeatureHandler {

    @Inject
    private lateinit var spaceFeatureHelperService: SpaceFeatureHelperService

    private val WORKFLOWS = arrayOf<String>()

    private val ITEM_TYPES = arrayOf<String>()

    private val SCRIPTS = arrayOf<String>()

    override fun afterCreation(appScope: AppScope) {
        reinit(appScope)
    }

    override fun reinit(space: AppScope): Boolean {
        spaceFeatureHelperService.setSpace(space)
        var changed = importTypes()
        changed = changed or importWorkflows()
        changed = changed or importScripts()
        return changed
    }

    private fun importTypes() = spaceFeatureHelperService.contentItemTypes()
                                                         .importEntityItemTypes(*ITEM_TYPES)
                                                         .spaceConfigurationChanged()

    private fun importScripts() = spaceFeatureHelperService.scripts()
                                                           .importEventScripts(*SCRIPTS)
                                                           .spaceConfigurationChanged()

    private fun importWorkflows() = spaceFeatureHelperService.importWorkflows(*WORKFLOWS)

}