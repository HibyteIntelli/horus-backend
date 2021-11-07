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
                    data = [["argument":"January", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"February", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"March", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"April", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"May", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"June", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"July", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"August", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"September", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"October", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"November", "min": Double.MAX_VALUE, "max": -10000],
                            ["argument":"December", "min": Double.MAX_VALUE, "max": -10000]]
                    values.forEach(v -> {
                        var d = LocalDateTime.parse(v.get("timeStamp").toString())
                        var index = d.getMonthValue() - 1
                        var valuesMap = data.get(index)
                        var minValue = valuesMap.get("min")
                        var maxValue = valuesMap.get("max")
                        var result = v.get("result")
                        var changed = false
                        if(result < minValue) {
                            valuesMap.replace("min", result)
                            changed = true
                        }
                        if(result > maxValue) {
                            valuesMap.replace("max", result)
                            changed = true
                        }
                        if(changed) {
                            data.remove(index)
                            data.add(index, valuesMap)
                        }
                    })
                    break
                default: break
            }
        }

        data = data.stream().map(entry -> {
            var valuesMap = entry
            var min = entry.get("min")
            if(Double.MAX_VALUE.equals(min)) {
                valuesMap.replace("min", 0.0)
                valuesMap.replace("max", 0.0)
            }
            return valuesMap
        }).collect(Collectors.toList());

        tmpConfig.put("title", chartParams.get("title"))
    }

    kpiChart.setData(data)
    kpiChart.setConfig(tmpConfig)
    return kpiChart
}