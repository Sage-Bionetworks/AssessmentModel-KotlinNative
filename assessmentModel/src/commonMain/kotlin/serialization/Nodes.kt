
package org.sagebionetworks.assessmentmodel.serialization

import kotlin.collections.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.sagebionetworks.assessmentmodel.ActiveStep
import org.sagebionetworks.assessmentmodel.ActiveStepCommand
import org.sagebionetworks.assessmentmodel.Assessment
import org.sagebionetworks.assessmentmodel.AssessmentInfo
import org.sagebionetworks.assessmentmodel.AssessmentPlaceholder
import org.sagebionetworks.assessmentmodel.AssessmentRegistryProvider
import org.sagebionetworks.assessmentmodel.AssessmentResult
import org.sagebionetworks.assessmentmodel.AsyncActionConfiguration
import org.sagebionetworks.assessmentmodel.AsyncActionContainer
import org.sagebionetworks.assessmentmodel.ButtonAction
import org.sagebionetworks.assessmentmodel.ButtonActionInfo
import org.sagebionetworks.assessmentmodel.ContentNode
import org.sagebionetworks.assessmentmodel.CountdownStep
import org.sagebionetworks.assessmentmodel.FormStep
import org.sagebionetworks.assessmentmodel.ImageInfo
import org.sagebionetworks.assessmentmodel.InstructionStep
import org.sagebionetworks.assessmentmodel.ModuleInfo
import org.sagebionetworks.assessmentmodel.Node
import org.sagebionetworks.assessmentmodel.NodeContainer
import org.sagebionetworks.assessmentmodel.OverviewStep
import org.sagebionetworks.assessmentmodel.PermissionInfo
import org.sagebionetworks.assessmentmodel.PermissionType
import org.sagebionetworks.assessmentmodel.ResultSummaryStep
import org.sagebionetworks.assessmentmodel.Section
import org.sagebionetworks.assessmentmodel.SpokenInstructionTiming
import org.sagebionetworks.assessmentmodel.Step
import org.sagebionetworks.assessmentmodel.TransformableAssessment
import org.sagebionetworks.assessmentmodel.TransformableNode
import org.sagebionetworks.assessmentmodel.navigation.DirectNavigationRule
import org.sagebionetworks.assessmentmodel.navigation.IdentifierPath
import org.sagebionetworks.assessmentmodel.navigation.SurveyNavigationRule
import org.sagebionetworks.assessmentmodel.resourcemanagement.copyResourceInfo
import org.sagebionetworks.assessmentmodel.survey.BaseType
import org.sagebionetworks.assessmentmodel.survey.ChoiceQuestion
import org.sagebionetworks.assessmentmodel.survey.ComboBoxQuestion
import org.sagebionetworks.assessmentmodel.survey.ComparableSurveyRule
import org.sagebionetworks.assessmentmodel.survey.InputItem
import org.sagebionetworks.assessmentmodel.survey.MultipleInputQuestion
import org.sagebionetworks.assessmentmodel.survey.Question
import org.sagebionetworks.assessmentmodel.survey.SimpleQuestion
import org.sagebionetworks.assessmentmodel.survey.SkipCheckboxInputItem
import org.sagebionetworks.assessmentmodel.survey.SurveyRuleOperator
import org.sagebionetworks.assessmentmodel.survey.UIHint

val nodeSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        subclass(ActiveStepObject::class)
        subclass(AssessmentPlaceholderObject::class)
        subclass(AssessmentObject::class)
        subclass(ChoiceQuestionObject::class)
        subclass(ComboBoxQuestionObject::class)
        subclass(CountdownStepObject::class)
        subclass(FormStepObject::class)
        subclass(InstructionStepObject::class)
        subclass(MultipleInputQuestionObject::class)
        subclass(OverviewStepObject::class)
        subclass(ResultSummaryStepObject::class)
        subclass(SimpleQuestionObject::class)
        subclass(SectionObject::class)
        subclass(StringChoiceQuestionObject::class)
        subclass(TransformableAssessmentObject::class)
        subclass(TransformableNodeObject::class)
    }
    polymorphic(TransformableAssessment::class) {
        subclass(TransformableAssessmentObject::class)
    }
    polymorphic(Question::class) {
        subclass(ChoiceQuestionObject::class)
        subclass(ComboBoxQuestionObject::class)
        subclass(MultipleInputQuestionObject::class)
        subclass(SimpleQuestionObject::class)
        subclass(StringChoiceQuestionObject::class)
    }
}

@Serializable
abstract class NodeObject : ContentNode, DirectNavigationRule {
    override var comment: String? = null
    override var title: String? = null
    override var subtitle: String? = null
    override var detail: String? = null
    override var footnote: String? = null
    @SerialName("shouldHideActions")
    override var hideButtons: List<ButtonAction> = listOf()
    @SerialName("actions")
    override var buttonMap: Map<ButtonAction, ButtonActionInfo> = mapOf()

    @SerialName("nextStepIdentifier")
    override var nextNodeIdentifier: String? = null

    open fun copyFrom(original: ContentNode) {
        this.comment = original.comment
        this.title = original.title
        this.subtitle = original.subtitle
        this.detail = original.detail
        this.footnote = original.footnote
        this.hideButtons = original.hideButtons
        this.buttonMap = original.buttonMap
        if (original is DirectNavigationRule) {
            this.nextNodeIdentifier = original.nextNodeIdentifier
        }
    }

    protected fun setButton(key: ButtonAction, value: ButtonActionInfo?) {
        val map = buttonMap.toMutableMap()
        if (value == null) {
            map.remove(key)
        } else {
            map[key] = value
        }
        buttonMap = map
    }
}

@Serializable
abstract class StepObject : NodeObject(), Step {
    override var spokenInstructions: Map<SpokenInstructionTiming, String>? = null
    override var viewTheme: ViewThemeObject? = null

    override fun copyFrom(original: ContentNode) {
        super.copyFrom(original)
        if (original is StepObject) {
            this.spokenInstructions = original.spokenInstructions
            this.viewTheme = original.viewTheme
        }
    }
}

@Serializable
abstract class IconNodeObject : NodeObject() {
    @SerialName("icon")
    @Serializable(ImageNameSerializer::class)
    override var imageInfo: FetchableImage? = null

    override fun copyFrom(original: ContentNode) {
        super.copyFrom(original)
        if (original is IconNodeObject) {
            this.imageInfo = original.imageInfo
        }
    }
}

/**
 * A concrete implementation of an [AssessmentPlaceholder].
 */
@Serializable
@SerialName("assessmentPlaceholder")
data class AssessmentPlaceholderObject(
    override val identifier: String,
    override val assessmentInfo: AssessmentInfoObject,
    override val comment: String? = null,
    override val title: String? = null,
    override val subtitle: String? = null,
    override val detail: String? = null
) : AssessmentPlaceholder

@Serializable
data class AssessmentInfoObject(
    override val identifier: String,
    override val versionString: String? = null,
    override val schemaIdentifier: String? = null,
    override val estimatedMinutes: Int = 0
) : AssessmentInfo

/**
 * Transformable Nodes
 */

@Serializable
@SerialName("transform")
data class TransformableNodeObject(
    override val identifier: String,
    override val resourceName: String,
    override val versionString: String? = null,
    override val comment: String? = null
) : TransformableNode

@Serializable
@SerialName("transformableAssessment")
data class TransformableAssessmentObject(
    override val identifier: String,
    override val resourceName: String,
    override val versionString: String? = null,
    override val estimatedMinutes: Int = 0,
    override val schemaIdentifier: String? = null,
    override val comment: String? = null,
    override val title: String? = null,
    override val subtitle: String? = null,
    override val detail: String? = null
) : TransformableAssessment

/**
 * NodeContainer
 */

@Serializable
abstract class NodeContainerObject : IconNodeObject(), NodeContainer {
    override var progressMarkers: List<String>? = null

    override fun copyFrom(original: ContentNode) {
        super.copyFrom(original)
        if (original is NodeContainer) {
            this.progressMarkers = original.progressMarkers
        }
    }
}

@Serializable
@SerialName("assessment")
data class AssessmentObject(
    override val identifier: String,
    @SerialName("steps")
    override val children: List<Node>,
    override val versionString: String? = null,
    override val schemaIdentifier: String? = null,
    override var estimatedMinutes: Int = 0,
    @SerialName("asyncActions")
    override val backgroundActions: List<AsyncActionConfiguration> = listOf()
) : NodeContainerObject(), Assessment, AsyncActionContainer {
    override fun createResult(): AssessmentResult = super<Assessment>.createResult()
    override fun unpack(originalNode: Node?, moduleInfo: ModuleInfo, registryProvider: AssessmentRegistryProvider): AssessmentObject {
        imageInfo?.copyResourceInfo(moduleInfo.resourceInfo)
        val copyChildren = children.map {
            it.unpack(null, moduleInfo, registryProvider)
        }
        val identifier = originalNode?.identifier ?: this.identifier
        val copy = copy(identifier = identifier, children = copyChildren)
        copy.copyFrom(this)
        return copy
    }
}

@Serializable
@SerialName("section")
data class SectionObject(
    override val identifier: String,
    @SerialName("steps")
    override val children: List<Node>,
    @SerialName("asyncActions")
    override val backgroundActions: List<AsyncActionConfiguration> = listOf()
) : NodeContainerObject(), Section, AsyncActionContainer {
    override fun unpack(originalNode: Node?, moduleInfo: ModuleInfo, registryProvider: AssessmentRegistryProvider): SectionObject {
        imageInfo?.copyResourceInfo(moduleInfo.resourceInfo)
        val copyChildren = children.map { it.unpack(null, moduleInfo, registryProvider) }
        val identifier = originalNode?.identifier ?: this.identifier
        val copy = copy(identifier = identifier, children = copyChildren)
        copy.copyFrom(this)
        return copy
    }
}

/**
 * Information steps
 */

@Serializable
@SerialName("instruction")
data class InstructionStepObject(
    override val identifier: String,
     @SerialName("image")
     override var imageInfo: ImageInfo? = null,
     override var fullInstructionsOnly: Boolean = false
) : StepObject(), InstructionStep

@Serializable
@SerialName("overview")
data class OverviewStepObject(
    override val identifier: String,
    @SerialName("image")
    override var imageInfo: ImageInfo? = null,
    override var icons: List<IconInfoObject>? = null,
    override var permissions: List<PermissionInfoObject>? = null
) : StepObject(), OverviewStep {
    override var learnMore: ButtonActionInfo?
        get() = buttonMap[ButtonAction.Navigation.LearnMore]
        set(value) = setButton(ButtonAction.Navigation.LearnMore, value)
}

@Serializable
data class PermissionInfoObject(
    override val permissionType: PermissionType,
    override val optional: Boolean = false,
    override val requiresBackground: Boolean = false,
    override val reason: String? = null,
    override val restrictedMessage: String? = null,
    override val deniedMessage: String? = null
) : PermissionInfo

@Serializable
@SerialName("feedback")
data class ResultSummaryStepObject(
    override val identifier: String,
    override val scoringResultPath: IdentifierPath? = null,
    override var resultTitle: String? = null,
    @SerialName("image")
    override var imageInfo: ImageInfo? = null
) : StepObject(), ResultSummaryStep

/**
 * Survey steps
 */

@Serializable
@SerialName("form")
data class FormStepObject(
    override val identifier: String,
    @SerialName("image")
    override val imageInfo: ImageInfo? = null,
    // TODO: syoung 04/22/2020 iOS defines the child nodes as "inputFields" but may want to change that?
    //  Basically, what is the keyword that makes sense for the nodes in a collection where the "step" shows multiple
    //  questions as a part of a single step?
    @SerialName("inputFields")
    override val children: List<Node>
) : StepObject(), FormStep

@Serializable
abstract class QuestionObject : StepObject(), Question, SurveyNavigationRule {
    @SerialName("image")
    override var imageInfo: ImageInfo? = null
    override var optional: Boolean = true
    override var surveyRules: List<ComparableSurveyRuleObject>? = null

    override fun copyFrom(original: ContentNode) {
        super.copyFrom(original)
        if (original is Question) {
            this.imageInfo = original.imageInfo
            this.optional = original.optional
        }
        if (original is QuestionObject) {
            this.surveyRules = original.surveyRules
        }
    }
}

@Serializable
@SerialName("simpleQuestion")
data class SimpleQuestionObject(
    override val identifier: String,
    override val inputItem: InputItem,
    override var skipCheckbox: SkipCheckboxInputItem? = null
) : QuestionObject(), SimpleQuestion

@Serializable
@SerialName("multipleInputQuestion")
data class MultipleInputQuestionObject(
    override val identifier: String,
    override val inputItems: List<InputItem>,
    override var sequenceSeparator: String? = null,
    override var skipCheckbox: SkipCheckboxInputItem? = null
) : QuestionObject(), MultipleInputQuestion

@Serializable
@SerialName("choiceQuestion")
data class ChoiceQuestionObject(
    override val identifier: String,
    override val choices: List<ChoiceOptionObject>,
    override val baseType: BaseType = BaseType.STRING,
    @SerialName("singleChoice")
    override var singleAnswer: Boolean = true,
    override var uiHint: UIHint = UIHint.Choice.ListItem
) : QuestionObject(), ChoiceQuestion

@Serializable
@SerialName("stringChoiceQuestion")
data class StringChoiceQuestionObject(
    override val identifier: String,
    @SerialName("choices")
    val items: List<String>,
    @SerialName("singleChoice")
    override var singleAnswer: Boolean = true,
    override var uiHint: UIHint = UIHint.Choice.ListItem
) : QuestionObject(), ChoiceQuestion {
    override val choices: List<ChoiceOptionObject>
        get() = items.map { ChoiceOptionObject(fieldLabel = it, value = JsonPrimitive(it)) }
    override val baseType: BaseType
        get() = BaseType.STRING
}

@Serializable
@SerialName("comboBoxQuestion")
data class ComboBoxQuestionObject(
    override val identifier: String,
    override val choices: List<ChoiceOptionObject>,
    override val otherInputItem: InputItem = defaultOtherInputItem,
    @SerialName("singleChoice")
    override var singleAnswer: Boolean = false,
    override var uiHint: UIHint = UIHint.Choice.Checkbox
) : QuestionObject(), ComboBoxQuestion {
    companion object {
        val defaultOtherInputItem: StringTextInputItemObject
            get() {
                val otherInputItem = StringTextInputItemObject()
                otherInputItem.fieldLabel = Localization.localizeString("Other")
                return otherInputItem
            }
    }
}

@Serializable
data class ComparableSurveyRuleObject(
    override val matchingAnswer: JsonElement = JsonNull,
    override val skipToIdentifier: String? = null,
    override val ruleOperator: SurveyRuleOperator? = null,
    override val accuracy: Double = 0.00001
) : ComparableSurveyRule

/**
 * Active steps
 */

@Serializable
abstract class BaseActiveStepObject : StepObject(), ActiveStep {
    override var requiresBackgroundAudio: Boolean = false
    override var shouldEndOnInterrupt: Boolean = false
    @SerialName("image")
    override var imageInfo: ImageInfo? = null

    // TODO: fix java.lang.IllegalAccessError
    @SerialName("commands")
    private var commandStrings: Set<String> = setOf()

    override var commands: Set<ActiveStepCommand>
        get() = ActiveStepCommand.fromStrings(commandStrings)
        set(value) { commandStrings = value.map { it.name.decapitalize() }.toSet() }

    override fun copyFrom(original: ContentNode) {
        super.copyFrom(original)
        if (original is BaseActiveStepObject) {
            this.requiresBackgroundAudio = original.requiresBackgroundAudio
            this.shouldEndOnInterrupt = original.shouldEndOnInterrupt
            this.imageInfo = original.imageInfo
            this.commandStrings = original.commandStrings
        }
    }
}

@Serializable
@SerialName("active")
data class ActiveStepObject(
    override val identifier: String,
    override val duration: Double
) : BaseActiveStepObject()

// CountdownStepObject will have a default timer of 5 seconds,
// as well as an auto transition to next page.
@Serializable
@SerialName("countdown")
data class CountdownStepObject(
    override val identifier: String,
    override val duration: Double = 5.0,
    override val fullInstructionsOnly: Boolean = false
) : BaseActiveStepObject(), CountdownStep {
    override var commands: Set<ActiveStepCommand>
        get() = super.commands union
                        setOf(
                            ActiveStepCommand.StartTimerAutomatically,
                            ActiveStepCommand.ContinueOnFinish)
        set(value) { super.commands = value }
}
