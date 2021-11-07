import one.space.spo.app.service.ContentItemService
import one.space.spo.app.service.contentitem.EntityItemPropertyFilter
import org.omnifaces.util.Beans
import one.space.spo.app.service.SpoqlService

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

def fetch(kpiChart, params){
    var data = [];
    ContentItemService entityItemService = Beans.getInstance(ContentItemService.class, true);
    SpoqlService spoql = Beans.getInstance(SpoqlService.class, true);

    var chartId = 0L;
    if(params.containsKey('chartId')) {
        chartId = Long.parseLong(params.get("chartId").get(0).toString());
    }

    var tmpConfig = kpiChart.getConfig();
    var chart = entityItemService.findById(chartId).orElse(null);
    if(chart != null) {
        var chartParams = chart.<Map<String, Object>>getSingleProperty("params");
        var metric = entityItemService.filter()
        .appScopeKey(chart.getAppScope())
        .typeKey("metric")
        .propertyFilter(EntityItemPropertyFilter.builder().property("name").value(chartParams.get("metric").toString()).build())
        .build().toList().stream().findFirst().orElse(null);
        if(metric != null) {
            var values = spoql.kpis("at '" + chart.getAppScope() + "' select kpi from '" + metric.getSingleProperty("kpiIdent") +
                    "' where {dimension 'targetId' eq " + chart.getSingleProperty("target").getId() + "} orderby {dimension 'timeStamp' descending}").asList();
            var value = values != null && values.size() > 0 ? values.get(0) : null;
            if(value != null) {
                var prettyDate = LocalDateTime.parse(value.get("timeStamp").toString()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                data.add([
                        "argument": prettyDate,
                        "value": value.get("result")
                ]);
            }
        }

        tmpConfig.put("title", chartParams.get("title"));
    }

    kpiChart.setData(data);
    kpiChart.setConfig(tmpConfig);
    return kpiChart;
}