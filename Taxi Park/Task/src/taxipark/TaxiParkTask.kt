package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
    this.allDrivers - this.trips.map(Trip::driver).distinct()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
    this.allPassengers
      .filter { p -> this.trips.count{ p in it.passengers } >= minTrips }
      .toSet()
//    this.trips
//        .flatMap (Trip::passengers)
//        .groupBy { passenger -> passenger }
//        .filterValues { group -> group.size >= minTrips }
//        .keys
/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
//    this.allPassengers
//      .filter { p -> this.trips.count{ p in it.passengers && it.driver == driver } > 1 }
//      .toSet()
    this.trips
        .filter { it.driver == driver }
        .flatMap (Trip::passengers)
        .groupBy { passenger -> passenger }
        .filterValues { group -> group.size > 1 }
        .keys


/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
//    val (tripsWithDiscount, tripsWithoutDiscount) = this.trips.partition { t -> t.discount?.let { it > 0.0 } ?: false }
//
//    return this.allPassengers
//            .filter { passenger ->
//                tripsWithDiscount.count { t -> passenger in t.passengers } >
//                tripsWithoutDiscount.count { t -> passenger in t.passengers } }
//            .toSet()

    return this.allPassengers
            .associate { passenger -> passenger to this.trips.filter { t -> passenger in t.passengers } }
            .filterValues { trips ->
                val (tripsWithDiscount, tripsWithoutDiscount) = trips.partition { t -> t.discount?.let { it > 0.0 } ?: false }
                tripsWithDiscount.size > tripsWithoutDiscount.size
            }.keys
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? =
        this.trips
                .groupBy {
                    val start = it.duration / 10 * 10
                    val end = start + 9
                    start..end
                }
                .entries.maxBy { (_, trips) -> trips.size }
                ?.key
/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val totalIncome = this.trips.sumByDouble(Trip::cost)
    if (totalIncome == 0.0) return false

    val twentyPercentOfDrivers = this.allDrivers.size/5

    val incomeOfTwentyPercentOfDrivers = this.trips
            .groupBy(Trip::driver)
            .map { (_,tripsByDriver) -> tripsByDriver.sumByDouble(Trip::cost) }
            .sortedDescending()
            .take(twentyPercentOfDrivers)
            .sum()

    return incomeOfTwentyPercentOfDrivers >= totalIncome * 0.8
}