package org.sagebionetworks.assessmentmodel.northwestern

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.resourcemanagement.FileLoader
import org.sagebionetworks.assessmentmodel.resourcemanagement.ResourceInfo
import org.sagebionetworks.assessmentmodel.resourcemanagement.copyResourceInfo
import org.sagebionetworks.assessmentmodel.serialization.*
import org.sagebionetworks.assessmentmodel.survey.*

//In order to extend abstract classes such as StepObject
// the serializable classes need to be in the same project module.
// Due to this bug: https://github.com/Kotlin/kotlinx.serialization/issues/576


object MtbSerialization {
    object SerializersModule {
        val default =
            Serialization.SerializersModule.default +
                    flankerSerializersModule

    }
    object JsonCoder {
        val default = Json(
            context = SerializersModule.default,
            configuration = JsonConfiguration.Stable)
    }
}

val flankerSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        MtbAssessmentObject::class with MtbAssessmentObject.serializer()
        FlankerInstructionStep::class with FlankerInstructionStep.serializer()
        FlankerForm::class with FlankerForm.serializer()
        //FlankerParentForm::class with FlankerParentForm.serializer()
        FlankerInstructionForm::class with FlankerInstructionForm.serializer()
        IntIntChoiceInputField::class with IntIntChoiceInputField.serializer()
    }
    polymorphic(Assessment::class) {
        MtbAssessmentObject::class with MtbAssessmentObject.serializer()
    }
}

val flankerStringIntSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        StringIntChoiceInputField::class with StringIntChoiceInputField.serializer()
    }
}
val flankerIntIntSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        IntIntChoiceInputField::class with IntIntChoiceInputField.serializer()
    }
}
val flankerDoubleIntSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        DoubleIntChoiceInputField::class with DoubleIntChoiceInputField.serializer()
    }
}

@Serializable
@SerialName("Flanker")
data class MtbAssessmentObject(
    override val identifier: String,
    @SerialName("steps")
    override val children: List<Node>,
    override val versionString: String? = null,
    override val resultIdentifier: String? = null,
    override var estimatedMinutes: Int = 0,
    val taskOrientation: String = "portrait",
    val defaultSteps: MtbDefaultSteps? = null,
    val stepRules: List<MtbStepRule> = listOf()
) : NodeContainerObject(), Assessment {
    override fun createResult(): AssessmentResult = super<Assessment>.createResult()
    override fun unpack(fileLoader: FileLoader, resourceInfo: ResourceInfo, jsonCoder: Json): MtbAssessmentObject {
        imageInfo?.copyResourceInfo(resourceInfo)
        val copyChildren = children.map { it.unpack(fileLoader, resourceInfo, jsonCoder) }
        val copy = copy(children = copyChildren)
        copy.copyFrom(this)
        return copy
    }
}

@Serializable
data class MtbDefaultSteps(
    val numberOfSteps: Int,
    val startingStepGroup: String
)

//@Serializable
//@SerialName("flankerParentForm")
//data class FlankerParentForm(
//    override val identifier: String,
//    val isPractice: Boolean = false,
//    @SerialName("steps")
//    val children: List<Node> = listOf()
//) : MtbStep()

@Serializable
@SerialName("flankerForm")
data class FlankerForm(
    override val identifier: String,
    val isPractice: Boolean = false,
    @SerialName("inputFields")
    override val children: List<Node> = listOf(),
    //Could remove this and use above "flankerParentForm" instead
    val steps: List<Node> = listOf()
) : MtbStep(), FormStep


@Serializable
@SerialName("flankerInstructionForm")
data class FlankerInstructionForm(
    override val identifier: String,
    @SerialName("inputFields")
    override val children: List<Node> = listOf(),
    val isInstruction: Boolean = true,
    val flankerImageNames: List<String> = listOf(),
    val flankerType: String? = null
) : MtbStep(), FormStep

//Can 'flankerInstruction' steps with 'inputFields' be changed to 'flankerInstructionForm'?
@Serializable
@SerialName("flankerInstruction")
data class FlankerInstructionStep(
    override val identifier: String,
    override val fullInstructionsOnly: Boolean = false,
    val branchingNavigationRules: List<MtbStepRule> = listOf(),
    val flankerImageNames: List<String> = listOf(),
    val htmlText: String? = null,
    //val inputFields: List<Node> = listOf(),
    val stepBackTo: String? = null,
    val stepGroup: String? = null,
    val stepName: String? = null
) : MtbStep(), InstructionStep

@Serializable
abstract class FlankerChoiceInputField<ValueType, ScoreType> : Node {
    abstract val choices: List<FlankerChoiceOption<ValueType, ScoreType>>
    var symbol: String? = null

    // Not used
    override val resultIdentifier: String?
        get() = null
    override val comment: String?
        get() = null
    override val hideButtons: List<ButtonAction>
        get() = listOf()
    override val buttonMap: Map<ButtonAction, ButtonActionInfo>
        get() = mapOf()
}

@Serializable
@SerialName("singleChoice")
data class StringIntChoiceInputField(
    override val identifier: String,
    override val choices: List<FlankerChoiceOption<String, Int>>
) : FlankerChoiceInputField<String, Int>()

@Serializable
@SerialName("singleChoice")
data class IntIntChoiceInputField(
    override val identifier: String,
    override val choices: List<FlankerChoiceOption<Int, Int>>
) : FlankerChoiceInputField<Int, Int>()

@Serializable
@SerialName("singleChoice")
data class DoubleIntChoiceInputField(
    override val identifier: String,
    override val choices: List<FlankerChoiceOption<Double, Int>>
) : FlankerChoiceInputField<Double, Int>()

@Serializable
data class FlankerChoiceOption<ValueType, ScoreType> (
    val value: ValueType,
    val score: ScoreType? = null,
    val itemResponseOID: String,
    @SerialName("text")
    override val fieldLabel: String?,
    @SerialName("image")
    override val icon: FetchableImage? = null,
    override val exclusive: Boolean = false
) : ChoiceOption {
    override fun jsonValue(selected: Boolean): JsonElement? = when (value) {
        is Number -> JsonPrimitive(value)
        else -> JsonPrimitive("$value")
    }
}

@Serializable
data class MtbStepRule(
    val criteria: List<MtbCriteria>,
    val description: String,
    val nextStepName: String,
    val stepGroup: String? = null
)

@Serializable
data class MtbCriteria(
    val description: String,
    val duration: Int? = null,
    val name: String
)

@Serializable
abstract class MtbStep() : StepObject() {
    val timeout: Int? = null
    @SerialName("text")
    override var detail: String? = null
    override val resultIdentifier: String? = null
    @SerialName("image")
    override val imageInfo: ImageInfo? = null
}



