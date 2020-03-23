package org.sagebionetworks.assessmentmodel.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.*
import org.sagebionetworks.assessmentmodel.CollectionResult
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.assessmentmodel.survey.AnswerType
import org.sagebionetworks.assessmentmodel.survey.BaseType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

open class ResultTest {

    @Serializable
    data class TestResultWrapper(val result: Result)

    private val jsonCoder = Serialization.JsonCoder.default

    /**
     * Collection-type results
     */

    @UnstableDefault
    @Test
    fun testParentNodeResult() {
        val result1 = ResultObject("result1")
        val result2 = ResultObject("result2")
        val result3 = BranchNodeResultObject(identifier = "result3",
                pathHistoryResults = mutableListOf(ResultObject("resultA"), ResultObject("resultB")),
                inputResults = mutableSetOf(ResultObject("asyncResultA"), ResultObject("asyncResultB")))
        val result4 = CollectionResultObject(identifier = "result4",
                inputResults = mutableSetOf(ResultObject("asyncResultA"), ResultObject("asyncResultB")))

        val original = BranchNodeResultObject("testResult", pathHistoryResults = mutableListOf(result1, result2, result3, result4))
        val inputString = """
            {
                "identifier": "testResult",
                "stepHistory": [
                    {"identifier": "result1","type": "base"},
                    {"identifier": "result2","type": "base"},
                    {
                        "identifier": "result3",
                        "type": "task",
                        "stepHistory": [{"identifier": "resultA","type": "base"},{"identifier": "resultB","type": "base"}],
                        "asyncResults": [{"identifier": "asyncResultA","type": "base"},{"identifier": "asyncResultB","type": "base"}]
                    },
                    {
                        "identifier": "result4",
                        "type": "collection",
                        "inputResults": [{"identifier": "asyncResultA","type": "base"},{"identifier": "asyncResultB","type": "base"}]
                    }
                ]
            }   
            """.trimIndent()

        val jsonString = jsonCoder.stringify(BranchNodeResultObject.serializer(), original)
        val restored = jsonCoder.parse(BranchNodeResultObject.serializer(), jsonString)
        val decoded = jsonCoder.parse(BranchNodeResultObject.serializer(), inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)

        val jsonWrapper = Json.parseJson(jsonString).jsonObject
        assertEquals("testResult", jsonWrapper.getPrimitiveOrNull("identifier")?.content)
        val pathHistory = jsonWrapper.getArrayOrNull("stepHistory")
        assertNotNull(pathHistory)
        assertEquals(4, pathHistory.count())
        val r1 = pathHistory.firstOrNull()?.jsonObject
        assertNotNull(r1)
        assertEquals("result1", r1.getPrimitiveOrNull("identifier")?.content)
        assertEquals("base", r1.getPrimitiveOrNull("type")?.content)
        val r4 = pathHistory.lastOrNull()?.jsonObject
        assertNotNull(r4)
        assertEquals("result4", r4.getPrimitiveOrNull("identifier")?.content)
        assertEquals("collection", r4.getPrimitiveOrNull("type")?.content)
    }

    @UnstableDefault
    @Test
    fun testAssessmentResult() {
        val original = AssessmentResultObject(identifier = "testResult",
                pathHistoryResults = mutableListOf(ResultObject("resultA"), ResultObject("resultB")),
                inputResults = mutableSetOf(ResultObject("asyncResultA"), ResultObject("asyncResultB")),
                runUUIDString = "4cb0580-3cdb-11ea-b77f-2e728ce88125",
                startDateString = "2020-01-21T12:00:00.000+7000",
                endDateString = "2020-01-21T12:05:00.000+7000"
            )
         val inputString = """
            {
                "identifier": "testResult",
                "taskRunUUID": "4cb0580-3cdb-11ea-b77f-2e728ce88125",
                "startDate": "2020-01-21T12:00:00.000+7000",
                "endDate": "2020-01-21T12:05:00.000+7000",
                "stepHistory": [{"identifier": "resultA","type": "base"},{"identifier": "resultB","type": "base"}],
                "asyncResults": [{"identifier": "asyncResultA","type": "base"},{"identifier": "asyncResultB","type": "base"}]
            }
            """.trimIndent()

        val jsonString = jsonCoder.stringify(AssessmentResultObject.serializer(), original)
        val restored = jsonCoder.parse(AssessmentResultObject.serializer(), jsonString)
        val decoded = jsonCoder.parse(AssessmentResultObject.serializer(), inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)

        val jsonWrapper = Json.parseJson(jsonString).jsonObject
        assertEquals("testResult", jsonWrapper.getPrimitiveOrNull("identifier")?.content)
        assertEquals("4cb0580-3cdb-11ea-b77f-2e728ce88125", jsonWrapper.getPrimitiveOrNull("taskRunUUID")?.content)
        assertEquals("2020-01-21T12:00:00.000+7000", jsonWrapper.getPrimitiveOrNull("startDate")?.content)
        assertEquals("2020-01-21T12:05:00.000+7000", jsonWrapper.getPrimitiveOrNull("endDate")?.content)
        val pathHistory = jsonWrapper.getArrayOrNull("stepHistory")
        assertNotNull(pathHistory)
        assertEquals(2, pathHistory.count())
        val r1 = pathHistory.firstOrNull()?.jsonObject
        assertNotNull(r1)
        assertEquals("resultA", r1.getPrimitiveOrNull("identifier")?.content)
        assertEquals("base", r1.getPrimitiveOrNull("type")?.content)
        val asyncResults = jsonWrapper.getArrayOrNull("asyncResults")
        assertNotNull(asyncResults)
        assertEquals(2, asyncResults.count())
        val ar1 = asyncResults.firstOrNull()?.jsonObject
        assertNotNull(ar1)
        assertEquals("asyncResultA", ar1.getPrimitiveOrNull("identifier")?.content)
        assertEquals("base", ar1.getPrimitiveOrNull("type")?.content)
    }

    /**
     * AnswerResult
     */

    @Test
    fun testAnswerType_Primitive_jsonElementFor() {
        assertEquals(JsonPrimitive(true), AnswerType.BOOLEAN.jsonElementFor(true))
        assertEquals(JsonPrimitive(3), AnswerType.INTEGER.jsonElementFor(3))
        assertEquals(JsonPrimitive(3.2), AnswerType.DECIMAL.jsonElementFor(3.2))
        assertEquals(JsonPrimitive("foo"), AnswerType.STRING.jsonElementFor("foo"))
    }

    @Test
    fun testAnswerType_PhoneNumber_jsonElementFor() {
        val answerType = AnswerType.List(BaseType.INTEGER,"-")
        val parts = listOf(206,555,1212)
        assertEquals(JsonPrimitive("206-555-1212"), answerType.jsonElementFor(parts))
    }

    @Test
    fun testAnswerType_WordList_jsonElementFor() {
        val answerType = AnswerType.List()
        val words = listOf("fox","jumped","over","moon")
        val expected = JsonArray(words.map { JsonPrimitive(it) })
        assertEquals(expected, answerType.jsonElementFor(words))
    }

    @Test
    fun testAnswerResult_Boolean() {
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.BOOLEAN, JsonPrimitive(true)))
        val inputString = """
            { "result": 
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "boolean"
                        },
                        "value" : true
                    }
            }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_Decimal() {
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.DECIMAL, JsonPrimitive(3.2)))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "decimal"
                        },
                        "value" : 3.2
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_Int() {
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.INTEGER, JsonPrimitive(3)))
        val inputString = """
            { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "integer"
                        },
                        "value" : 3
                    }
            }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_Map() {
        val originalValue = JsonObject(mapOf("a" to JsonLiteral(3.2), "b" to JsonLiteral("boo")))
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.MAP, originalValue))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "map"
                        },
                        "value" : {"a":3.2,"b":"boo"}
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_String() {
        val originalValue = JsonPrimitive("goo")
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.STRING, originalValue))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "string"
                        },
                        "value" : "goo"
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_DateYearMonth() {
        val originalValue = JsonPrimitive("2020-02")
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.DateTime("yyyy-MM"), originalValue))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "dateTime",
                            "codingFormat": "yyyy-MM"
                        },
                        "value" : "2020-02"
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_ListInt() {
        val originalValue = JsonArray(listOf(JsonPrimitive(2), JsonPrimitive(5)))
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.List(BaseType.INTEGER), originalValue))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "list",
                            "baseType": "integer"
                        },
                        "value" : [2,5]
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    @Test
    fun testAnswerResult_Measurement() {
        val originalValue = JsonPrimitive(10.2)
        val original = TestResultWrapper(AnswerResultObject("foo", AnswerType.Measurement("cm"), originalValue))
        val inputString = """
                { "result":
                    {
                        "identifier" : "foo",
                        "type" : "answer",
                        "answerType" : {
                            "type": "measurement",
                            "unit": "cm"
                        },
                        "value" : 10.2
                    }
                }
        """.trimIndent()

        val serializer = TestResultWrapper.serializer()
        val jsonString = jsonCoder.stringify(serializer, original)
        val restored = jsonCoder.parse(serializer, jsonString)
        val decoded = jsonCoder.parse(serializer, inputString)

        // Look to see that the restored, decoded, and original all are equal
        assertEquals(original, restored)
        assertEquals(original, decoded)
    }

    /**
     * Result - copyResult
     */

//    AnswerResultObject::class with AnswerResultObject.serializer()
//    AssessmentResultObject::class with AssessmentResultObject.serializer()
//    BranchNodeResultObject::class with BranchNodeResultObject.serializer()
//    CollectionResultObject::class with CollectionResultObject.serializer()
//    ResultObject::class with ResultObject.serializer()
    @Test
    fun testResultObject_copyResult() {
        val original = ResultObject("foo")
        val copy = original.copyResult()
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }

    @Test
    fun testAnswerResultObject_copyResult() {
        val original = AnswerResultObject("foo", AnswerType.DateTime("yyyy-MM"), JsonPrimitive("2020-02"))
        val copy = original.copyResult()
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }

    @Test
    fun testAssessmentResultObject_copyResult() {
        val inputResult1 = ResultObject("boo")
        val inputResults = mutableSetOf<Result>(inputResult1, ResultObject("yahoow"))
        val pathHistoryResult1 = ResultObject("step1")
        val pathHistoryResults = mutableListOf<Result>(pathHistoryResult1, ResultObject("step2"))
        val original = AssessmentResultObject(identifier = "testResult",
                pathHistoryResults = pathHistoryResults,
                inputResults = inputResults,
                runUUIDString = "4cb0580-3cdb-11ea-b77f-2e728ce88125",
                startDateString = "2020-01-21T12:00:00.000+7000",
                endDateString = "2020-01-21T12:05:00.000+7000"
        )
        val copy = original.copyResult()
        assertEquals(original, copy)
        assertNotSame(original, copy)
        val copyInputResult1 = copy.inputResults.firstOrNull()
        assertNotNull(copyInputResult1)
        assertEquals(inputResult1, copyInputResult1)
        assertNotSame(inputResult1, copyInputResult1)
        val copyPathHistoryResult1 = copy.pathHistoryResults.firstOrNull()
        assertNotNull(copyPathHistoryResult1)
        assertEquals(pathHistoryResult1, copyPathHistoryResult1)
        assertNotSame(pathHistoryResult1, copyPathHistoryResult1)
    }

    @Test
    fun testBranchNodeResultObject_copyResult() {
        val inputResult1 = ResultObject("boo")
        val inputResults = mutableSetOf<Result>(inputResult1, ResultObject("yahoow"))
        val pathHistoryResult1 = ResultObject("step1")
        val pathHistoryResults = mutableListOf<Result>(pathHistoryResult1, ResultObject("step2"))
        val original = BranchNodeResultObject("foo", pathHistoryResults, inputResults)
        val copy = original.copyResult()
        assertEquals(original, copy)
        assertNotSame(original, copy)
        val copyInputResult1 = copy.inputResults.firstOrNull()
        assertNotNull(copyInputResult1)
        assertEquals(inputResult1, copyInputResult1)
        assertNotSame(inputResult1, copyInputResult1)
        val copyPathHistoryResult1 = copy.pathHistoryResults.firstOrNull()
        assertNotNull(copyPathHistoryResult1)
        assertEquals(pathHistoryResult1, copyPathHistoryResult1)
        assertNotSame(pathHistoryResult1, copyPathHistoryResult1)
    }

    @Test
    fun testCollectionResultObject_copyResult() {
        val inputResult1 = ResultObject("boo")
        val inputResults = mutableSetOf<Result>(inputResult1, ResultObject("yahoow"))
        val original = CollectionResultObject("foo", inputResults)
        val copy = original.copyResult()
        assertEquals(original, copy)
        assertNotSame(original, copy)
        val copyInputResult1 = copy.inputResults.firstOrNull { it == inputResult1 }
        assertNotNull(copyInputResult1)
        assertNotSame(inputResult1, copyInputResult1)
    }

    @Test
    fun testNestedCollection_copyResult() {
        val inputResult1 = ResultObject("boo")
        val inputResults = mutableSetOf<Result>(inputResult1, ResultObject("yahoow"))
        val originalCollection = CollectionResultObject("foo", inputResults)
        val pathHistoryResult1 = ResultObject("step1")
        val pathHistoryResults = mutableListOf<Result>(pathHistoryResult1, ResultObject("step2"))
        val originalBranch = BranchNodeResultObject("foo", pathHistoryResults, mutableSetOf(originalCollection))
        val copy = originalBranch.copyResult()
        assertEquals(originalBranch, copy)
        assertNotSame(originalBranch, copy)
        val copyCollection = copy.inputResults.first() as CollectionResultObject
        assertEquals(originalCollection, copyCollection)
        assertNotSame(originalCollection, copyCollection)
        val copyInputResult1 = copyCollection.inputResults.firstOrNull { it == inputResult1 }
        assertNotNull(copyInputResult1)
        assertNotSame(inputResult1, copyInputResult1)
    }
}