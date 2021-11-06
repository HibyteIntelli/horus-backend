import one.space.spo.app.service.ContentItemService
import one.space.spo.app.service.contentitem.EntityItemPropertyFilter
import org.omnifaces.util.Beans
import one.space.spo.app.domain.ContentItem

import java.time.LocalDate
import java.util.stream.Collectors

def fetch(kpiChart, params){
    var space = "heydo";
    var lang = "de";
    var df = [];
    var tableValues = [];
    var total = 0;
    var kpiService = inject("kpiService");
    ContentItemService entityItemService = Beans.getInstance(ContentItemService.class, true);

    var tmpConfigs = kpiChart.getConfig();
    if(tmpConfigs.containsKey("_language")) {
        lang = tmpConfigs.get("_language").toString();
    }
    if(tmpConfigs.containsKey("_space")) {
        space = tmpConfigs.get("_space").toString();
    }

    LocalDate startDate = LocalDate.now().minusDays(31);
    LocalDate endDate = LocalDate.now();
    if(params.containsKey('startDate') && params.containsKey('endDate')) {
        startDate = LocalDate.parse(params.get("startDate").get(0).toString());
        endDate = LocalDate.parse(params.get("endDate").get(0).toString());
    }

    var orgStructureId = 0;
    if(params.containsKey('organisationalStructure')) {
        orgStructureId = Long.parseLong(params.get("organisationalStructure").get(0).toString());
    }

    var allCategories = [:];
    var dashletId = null;
    ContentItem dynamicKpiConfiguration = null;
    if(params.containsKey("dashletId")) {
        dashletId = Long.parseLong(params.get("dashletId").get(0).toString());
        var dashlet = entityItemService.findById(dashletId).get();
        if(dashlet != null) {
            if(orgStructureId == 0 && dashlet.getSingleProperty("organisationalStructure") != null) {
                orgStructureId = dashlet.getSingleProperty("organisationalStructure").getId();
            }

            dynamicKpiConfiguration = dashlet.getSingleProperty("dynamicKpiConfiguration");
            if(dynamicKpiConfiguration != null) {
                var parent = null;
                if(params.containsKey("parentClassification")) {
                    parent = entityItemService.findById(Long.parseLong(params.get("parentClassification").get(0).toString())).get();
                }

                var keys = dynamicKpiConfiguration.getListProperty("dynamicKpiCategories").stream()
                                                .filter(c -> (parent == null && c.getParent() == null) ||
                                                                      (parent != null && c.getParent() != null && c.getParent().getId() == parent.getId()))
                                                .collect(Collectors.toList());

                keys.forEach(k -> {
                    var values = [];
                    values.add(k);
                    values.addAll(dynamicKpiConfiguration.getListProperty("dynamicKpiCategories").stream()
                                                        .filter(c -> isCategoryChildOf(c,k.getId()))
                                                        .collect(Collectors.toList()));
                    allCategories.put(k, values);
                })
            }
        }
    }

    List groupIds = [];
    if(params.containsKey('groupId')) {
        params.get("groupId").stream().forEach(groupId -> {
            groupIds.add(Long.parseLong(groupId.toString()));
        })
    }

    var organisationalStructure = entityItemService.findById(orgStructureId).orElse(null);
    List<ContentItem> children = organisationalStructure.getChildrenWrappers();
    List<ContentItem> grandchildren = new ArrayList<>();
    List<ContentItem> grandGrandChildren = new ArrayList<>();
    List<Long> orgStructures = new ArrayList<>();
    List<Map<String,Object>> relevantOSAndDashlets = [];
    orgStructures.add(orgStructureId);
    if(!children.isEmpty()) {
        children.forEach(child -> {
            grandchildren.addAll(child.getChildrenWrappers());
            orgStructures.add(child.getId());
        });
        if(!grandchildren.isEmpty()) {
            grandchildren.forEach(child -> {
                grandGrandChildren.addAll(child.getChildrenWrappers());
                orgStructures.add(child.getId());
            });
            if(!grandGrandChildren.isEmpty()) {
                grandGrandChildren.forEach(child -> orgStructures.add(child.getId()));
            }
        }
    }

    List dashlets = entityItemService.filter()
            .appScopeKey(organisationalStructure.getAppScope())
            .typeKey("dashlet")
            .propertyFilter(EntityItemPropertyFilter.builder()
                    .filterType(EntityItemPropertyFilter.Type.EQUALS)
                    .property("dynamicKpiConfiguration")
                    .value(String.valueOf(dynamicKpiConfiguration.getId()))
                    .build())
            .build().toList();
    dashlets.stream()
            .map(d -> (ContentItem)d)
            .filter(d -> d.getSingleProperty("organisationalStructure") != null && orgStructures.contains(d.getSingleProperty("organisationalStructure").getId()))
            .forEach(d -> {
                relevantOSAndDashlets.add(Map.of("dashletId", d.getId(), "organisationalStructureId", d.getSingleProperty("organisationalStructure").getId()));
            });

    for(LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
        relevantOSAndDashlets.forEach(item -> {
            groupIds.forEach(groupId -> {
                allCategories.forEach((par,cats) -> {
                    var categoryName = par.getSingleProperty("title");
                    cats.forEach(c -> {
                        var value = kpiService.getLatestValueForDimensions(space, "groupAndCategoryActualValueDynamicKpi", ["date": date.toString(), "dashletId": item.get("dashletId"), "groupId": groupId, "organisationalStructureId": item.get("organisationalStructureId"), "categoryId": c.getId()]);
                        if(value != null) {
                            var result = value.get("result");
                            if(result != null) {
                                tableValues.add([
                                        "date": date.toString(),
                                        "groupId": groupId,
                                        "categoryId": c.getId(),
                                        "value": result,
                                        "organisationalStructureId": item.get("organisationalStructureId"),
                                        "dashletId": item.get("dashletId")
                                ]);
                                total += result;

                                var oExisting = df.stream()
                                        .filter(e -> e.containsKey("argument")? e.get("argument").equals(categoryName) : false)
                                        .peek(e -> {
                                            if(e.containsKey("value")) {
                                                e.replace("value", e.get("value") + result);
                                            }
                                            if(!e.containsKey("onClick") && c.getId() != par.getId()) {
                                                e.put("onClick", [
                                                        "kpiIdent": "commonDynamicKpiPareto",
                                                        "params": [
                                                                "organisationalStructure": orgStructureId,
                                                                "startDate" : startDate.toString(),
                                                                "endDate" : endDate.toString(),
                                                                "parentClassification" : par.getId(),
                                                                "dashletId":dashletId,
                                                                "groupId":groupIds
                                                        ]
                                                ]);
                                            }
                                        })
                                        .findAny();
                                if(!oExisting.isPresent()) {
                                    var toAdd = [
                                            "argument": categoryName,
                                            "value": result,
                                            "isPareto": true
                                    ];
                                    if(c.getId() != par.getId()) {
                                        toAdd.put("onClick", [
                                                "kpiIdent": "commonDynamicKpiPareto",
                                                "params": [
                                                        "organisationalStructure": orgStructureId,
                                                        "startDate" : startDate.toString(),
                                                        "endDate" : endDate.toString(),
                                                        "parentClassification" : par.getId(),
                                                        "dashletId":dashletId,
                                                        "groupId":groupIds
                                                ]
                                        ]);
                                    }
                                    df.add(toAdd);
                                }
                            }
                        }
                    })
                })
            })
        })
    }

    total = total == 0 ? 1 : total;
    Double cummulative = 0;
    kpiChart.setData(df.stream()
            .sorted((e1,e2) -> {
                def v1 = e1.containsKey("value") ? e1.get("value") : 0;
                def v2 = e2.containsKey("value") ? e2.get("value") : 0;
                return v2.compareTo(v1);
            })
            .peek(e -> {
                cummulative += e.containsKey("value") ? e.get("value") : 0;
                e.put("splineValue", (cummulative * 100 / total))
            })
            .collect(Collectors.toList()));

    tmpConfigs = kpiChart.getConfig();
    List panes = tmpConfigs.get("panes");
    Map pane = panes.get(0);
    List valueAxisList = pane.get("valueAxisList");
    Map valueAxis = valueAxisList.get(0);
    valueAxis.put("title", (dynamicKpiConfiguration != null ? dynamicKpiConfiguration.getSingleProperty("paretoValueAxisTitle") : ""));
    valueAxisList.remove(0);
    valueAxisList.add(valueAxis);
    pane.put("valueAxisList", valueAxisList);
    panes.remove(0);
    panes.add(pane);
    tmpConfigs.put("panes", panes);

    tmpConfigs.put("actualValues", tableValues);

    kpiChart.setConfig(tmpConfigs);

    return kpiChart;
}

def isCategoryChildOf(category, parentId) {
    if(category.getParent() != null) {
        if(category.getParent().getId() == parentId) {
            return true;
        }
        return isCategoryChildOf(category.getParent(), parentId);
    }
    return false;
}