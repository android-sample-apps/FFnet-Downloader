package com.citymapper.codingchallenge.stoppoints

class DbStopPointsRepository(private val dao: StopPointDao) {

    fun addStopPoints(stopPoints: List<StopPoint>) {
        dao.addAll(stopPoints.map {
            it.transformToDatabaseEntity()
        })
    }

    fun getStopPoints(): List<StopPoint> = dao.getAll().map {
        it.transformToEntity()
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    fun updateArrivalTimes(stopPointId: String, arrivalTimes: List<Arrival>): List<StopPoint> {
        dao.get(stopPointId)?.let {
            dao.update(it.copy(
                arrivalTimes = arrivalTimes.map { it.transformToDatabaseEntity() }
            ))
        }
        return getStopPoints()
    }

    private fun StopPoint.transformToDatabaseEntity(): StopPointDb = StopPointDb(
        id = this.id,
        name = this.name,
        arrivalTimes = this.arrivalTimes.map {
            it.transformToDatabaseEntity()
        }
    )

    private fun Arrival.transformToDatabaseEntity(): ArrivalDb = ArrivalDb(
        id = this.id,
        time = this.time
    )

    private fun StopPointDb.transformToEntity(): StopPoint = StopPoint(
        id = this.id,
        name = this.name,
        arrivalTimes = this.arrivalTimes.map {
            it.transformToEntity()
        }
    )

    private fun ArrivalDb.transformToEntity(): Arrival = Arrival(
        id = this.id,
        time = this.time
    )
}
