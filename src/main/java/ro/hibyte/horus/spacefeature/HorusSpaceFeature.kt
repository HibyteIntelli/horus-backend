package ro.hibyte.horus.spacefeature;

import lombok.extern.log4j.Log4j;
import one.space.spi.spacefeatures.SpaceFeature;
import one.space.spi.spacefeatures.SpaceFeatureHandler;
import one.space.spo.app.domain.AppScope;
import one.space.spo.app.service.SpaceFeatureHelperService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Log4j
@SpaceFeature(value = "Horus")
public class HorusSpaceFeature implements SpaceFeatureHandler {

    @Inject
    private SpaceFeatureHelperService spaceFeatureHelperService;

    private final String[] WORKFLOWS = {
    };

    private final String[] ITEM_TYPES = {
    };

    private final String[] ITEM_VIEWS = {
    };

    private final String[] SCRIPTS = {
    };

    private final String[] ROLES = {
    };

    @Override
    public void afterCreation(AppScope appScope) {
        reinit(appScope);
    }

    @Override
    public boolean reinit(AppScope space) {
        spaceFeatureHelperService.setSpace(space);
        var changed = importTypes();
        changed |= importWorkflows();
        changed |= importScripts();
        return changed;
    }

    private boolean importTypes() {
        return spaceFeatureHelperService.contentItemTypes()
                .importEntityItemTypes(ITEM_TYPES)
                .spaceConfigurationChanged();
    }

    private boolean importScripts() {
        return spaceFeatureHelperService.scripts()
                .importEventScripts(SCRIPTS)
                .spaceConfigurationChanged();
    }

    private boolean importWorkflows() {
        return spaceFeatureHelperService.importWorkflows(WORKFLOWS);
    }
}
