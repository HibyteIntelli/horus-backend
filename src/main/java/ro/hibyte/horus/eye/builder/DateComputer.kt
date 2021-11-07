package ro.hibyte.horus.eye.builder

import one.space.spo.app.domain.ContentItem
import one.space.spo.app.service.SpoqlService
import ro.hibyte.horus.utils.supportedKpis
import java.time.Instant
import java.util.*
import kotlin.collections.HashSet

object DateComputer {

    fun computeTimePerDay(): Set<String> {
        val time = HashSet<String>()
        for (i in 0..23) {
            time.add(
                    when {
                        i < 10 -> "0$i"
                        i >= 10 -> i.toString()
                        else -> throw IllegalStateException("This is actually exhaustive")
                    } + ":00"
            )
        }
        return time
    }

    fun computeDaysUntilNow(date: Date): Set<String> {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.time = date
        val days = HashSet<String>()
        val nowYear = now.get(Calendar.YEAR)
        val nowMonth = now.get(Calendar.MONTH)
        val nowDays = now.get(Calendar.DAY_OF_MONTH)
        while (then.get(Calendar.YEAR) <= nowYear || then.get(Calendar.MONTH) <= nowMonth || then.get(Calendar.DAY_OF_MONTH) <= nowDays || days.size >= 31) {
            days.add(then.get(Calendar.DAY_OF_MONTH).toString())
            then.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    fun computeMonthsUntilNow(date: Date): Set<String> {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.time = date
        val months = HashSet<String>()
        val nowYear = now.get(Calendar.YEAR)
        val nowMonth = now.get(Calendar.MONTH)
        while (then.get(Calendar.YEAR) <= nowYear || then.get(Calendar.MONTH) <= nowMonth || months.size >= 12) {
            months.add(then.get(Calendar.MONTH).toString())
            then.add(Calendar.MONTH, 1)
        }
        return months
    }

    fun computeYearsUntilNow(date: Date): Set<String> {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.time = date
        val years = HashSet<String>()
        val nowYear = now.get(Calendar.YEAR)
        while (then.get(Calendar.YEAR) <= nowYear) {
            years.add(then.get(Calendar.YEAR).toString())
            then.add(Calendar.YEAR, 1)
        }
        return years
    }

    fun computeStartDate(target: ContentItem, spoqlService: SpoqlService): Date =
            supportedKpis.map { spoqlService.kpis("at '${target.appScope}' select 'kpi' from '${it}' where {dimension 'result' ge 0} or {dimension 'result' lt 0} orderby timestamp limit 1").asList() }
                    .map { it[0]["timestamp"].toString() }
                    .map { Date.from(Instant.parse(it)) }
                    .maxOrNull() ?: Date(target.created.toInstant().minusMillis(604800000L).toEpochMilli())
}