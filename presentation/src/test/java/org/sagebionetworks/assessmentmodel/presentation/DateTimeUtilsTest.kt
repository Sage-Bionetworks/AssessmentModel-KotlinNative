package org.sagebionetworks.assessmentmodel.presentation

import junit.framework.TestCase

class DateTimeUtilsTest : TestCase() {

    fun testValidateDateTimePeriod() {
        // test leap years
        var d = DateTimeUtils()
        var leapYears = listOf(1952, 1956, 1960, 1964, 1968, 1972, 1976, 1980, 1984, 1988, 1992, 1996, 2000, 2004, 2008, 2012, 2016, 2020)
        for (i in leapYears) {
            assertTrue(i % 4 == 0)
        }
        for (i in leapYears) {
            for (j in 30..31) {
                assertFalse(d.validateYear(i, 2, j))
            }
        }
        for (i in leapYears) {
            for (j in 1..29) {
                assertTrue(d.validateYear(i, 2, j))
            }
        }
        // test non leap years
        var nonLeapYears = listOf(1981, 1982, 1983, 1994, 1995, 1997, 1998, 1999, 2001, 2002, 2003, 2005, 2006, 2007, 2009, 2010, 2011, 2013, 2014, 2015, 2017, 2018, 2019, 2021)
        var monthsWith31 = listOf(1, 3, 5, 7, 8, 10, 12)
        var monthsWith30 = listOf(4, 6, 9, 11)
        for (year in nonLeapYears) {
            for (month in monthsWith30) {
                for (day in 1..30) {
                    assertTrue(d.validateYear(year, month, day))
                }
            }
        }
        for (year in nonLeapYears) {
            for (month in monthsWith31) {
                for (day in 1..31) {
                    assertTrue(d.validateYear(year, month, day))
                }
            }
        }
    }

    fun testValidateMonth() {
        var d = DateTimeUtils()
        for (i in 1..12) {
            assertTrue(d.validateMonth(i, 1))
        }
        var month = 2
        for (i in 1..28) {
            assertTrue(d.validateMonth(month, i))
        }

        for (i in 1..12) {
            assertFalse(d.validateMonth(i, 0))
        }
    }

    fun testNegDaysValidMonths() {
        var d = DateTimeUtils()
        for (month in 1..12) {
            for (day in Int.MIN_VALUE..0) {
                assertFalse(d.validateMonth(month, day))
            }
        }
    }

    fun testInvalidMonths() {
        var d = DateTimeUtils()
        for (month in Int.MIN_VALUE..0) {
            for (day in 1..31) {
                assertFalse(d.validateMonth(month, day))
            }
        }
    }

    fun testValidateYear() {
        var d = DateTimeUtils()
        var day = 1;
        for (i in 1..12) {
            for (j in 2000..2021) {
                assertTrue(d.validateYear(j, i, day))
            }
        }
        // assert false for years over 2021
        for (i in 1..12) {
            for (j in 1..31) {
                for (k in 2022..2030) {
                    assertFalse(d.validateYear(k, j, i))
                }
            }
        }
        // assert fail for years under 1950
        for (i in 1..12) {
            for (j in 1..31) {
                for (k in 0..1949) {
                    assertFalse(d.validateYear(k, j, i))
                }
            }
        }
    }
    fun testValidateDay() {
        // test months with 31 days
        var d = DateTimeUtils()
        var monthsWith31 = listOf(1, 3, 5, 7, 8, 10, 12)
        var monthsWith30 = listOf(4, 6, 9, 11)
        for (i in 1..31) {
            for (j in monthsWith31) {
                assertTrue(d.validateDay(j, i))
            }
        }
        for (i in 1..30) {
            for (j in monthsWith30) {
                assertTrue(d.validateDay(j, i))
            }
        }
        // test february
        for (i in 29..31) {
            assertFalse(d.validateDay(2, i))
        }

        for (i in 32..Int.MAX_VALUE) {
            for (j in monthsWith30) {
                assertFalse(d.validateDay(j, i))
            }
        }
        for (i in 32..Int.MAX_VALUE) {
            for (j in monthsWith31) {
                assertFalse(d.validateDay(j, i))
            }
        }

        for (i in Int.MIN_VALUE..0) {
            for (j in monthsWith30) {
                assertFalse((d.validateDay(j, i)))
            }
        }
        for (i in Int.MIN_VALUE..0) {
            for (j in monthsWith31) {
                assertFalse((d.validateDay(j, i)))
            }
        }
    }
}