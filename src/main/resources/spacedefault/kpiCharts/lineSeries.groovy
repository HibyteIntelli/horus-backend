import one.space.spo.app.service.ContentItemService
import one.space.spo.app.service.contentitem.EntityItemPropertyFilter
import org.omnifaces.util.Beans
import one.space.spo.app.service.SpoqlService

import java.time.LocalDateTime
import java.time.LocalDate
import java.util.stream.Collectors

def fetch(kpiChart, params){
    var data = []
    ContentItemService entityItemService = Beans.getInstance(ContentItemService.class, true)
    SpoqlService spoql = Beans.getInstance(SpoqlService.class, true)

    var chartId = 0L
    var startDate = LocalDate.of(2021,1,1).atStartOfDay()
    var endDate = LocalDate.of(2021,12,12).atStartOfDay()
    var aggregationType = "MONTHLY"
    if(params.containsKey('chartId')) {
        chartId = Long.parseLong(params.get("chartId").get(0).toString())
    }
    if(params.containsKey('startDate') && params.containsKey('endDate')) {
        startDate = LocalDate.parse(params.get("startDate").get(0).toString()).atStartOfDay()
        endDate = LocalDate.parse(params.get("endDate").get(0).toString()).atStartOfDay()
    }
    if(params.containsKey('agg')) {
        aggregationType = params.get("agg").get(0).toString()
    }

    var tmpConfig = kpiChart.getConfig()
    var chart = entityItemService.findById(chartId).orElse(null)
    if(chart != null) {
        var chartParams = chart.<Map<String, Object>>getSingleProperty("params")
        var metric = entityItemService.filter()
                .appScopeKey(chart.getAppScope())
                .typeKey("metric")
                .propertyFilter(EntityItemPropertyFilter.builder().property("name").value(chartParams.get("metric").toString()).build())
                .build().toList().stream().findFirst().orElse(null)
        if(metric != null) {
            var values = spoql.kpis("at '" + chart.getAppScope() + "' select kpi from '" + metric.getSingleProperty("kpiIdent") +
                    "' where {dimension 'targetId' eq " + chart.getSingleProperty("target").getId() + "}").asList().stream()
                    .filter(v -> {
                        var date = LocalDateTime.parse(v.get("timeStamp").toString());
                        return !date.isAfter(endDate.plusDays(1)) && !date.isBefore(startDate);
                    })
                    .collect(Collectors.toList())

            switch(aggregationType) {
                case "MONTHLY":
                    data = [["argument":"January", "sum": 0, "numberOfValues": 0],
                            ["argument":"February", "sum": 0, "numberOfValues": 0],
                            ["argument":"March", "sum": 0, "numberOfValues": 0],
                            ["argument":"April", "sum": 0, "numberOfValues": 0],
                            ["argument":"May", "sum": 0, "numberOfValues": 0],
                            ["argument":"June", "sum": 0, "numberOfValues": 0],
                            ["argument":"July", "sum": 0, "numberOfValues": 0],
                            ["argument":"August", "sum": 0, "numberOfValues": 0],
                            ["argument":"September", "sum": 0, "numberOfValues": 0],
                            ["argument":"October", "sum": 0, "numberOfValues": 0],
                            ["argument":"November", "sum": 0, "numberOfValues": 0],
                            ["argument":"December", "sum": 0, "numberOfValues": 0]]
                    values.forEach(v -> {
                        var d = LocalDateTime.parse(v.get("timeStamp").toString())
                        var index = d.getMonthValue() - 1
                        var valuesMap = data.get(index)
                        valuesMap.replace("sum", valuesMap.get("sum") + v.get("result"))
                        valuesMap.replace("numberOfValues", valuesMap.get("numberOfValues") + 1)
                        data.remove(index)
                        data.add(index, valuesMap)
                    })
                    break
                default: break
            }
            data = data.stream()
            .map(entry -> {
                var valuesMap = entry
                var sum = entry.get("sum")
                var numberOfValues = entry.get("numberOfValues")
                valuesMap.remove("sum")
                valuesMap.remove("numberOfValues")
                valuesMap.put("value", numberOfValues > 0 ? sum / numberOfValues : 0)
                return valuesMap
            })
            .collect(Collectors.toList())
        }

        tmpConfig.put("title", chartParams.get("title"))
    }

    kpiChart.setData(data)
    kpiChart.setConfig(tmpConfig)
    return kpiChart
}