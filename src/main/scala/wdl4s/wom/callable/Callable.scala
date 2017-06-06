package wdl4s.wom.callable

import lenthall.util.TryUtil
import wdl4s.wdl.{NoLookup, WdlExpression}
import wdl4s.wdl.expression.WdlFunctions
import wdl4s.wdl.types.WdlType
import wdl4s.wdl.values.WdlValue
import wdl4s.wom.callable.Callable._
import wdl4s.wom.graph.GraphNodePort.WorkflowInputSource
import wdl4s.wom.graph.{GraphNode, GraphNodePort}

import scala.util.Try

trait Callable {
  def name: String

  /**
    * Given the linkings that can provide all inputs for this callable, generate a graph for this callable.
    * @param inputLinkings Linkings from outputs of other graph nodes to all inputs required by this callable's definition.
    * @return The graph of this callable.
    */
  def graph(inputLinkings: Map[Callable.InputDefinition, WorkflowInputSource]): Set[_ >: GraphNode]
  def inputs: Set[_ <: InputDefinition]
  def outputs: Set[OutputDefinition]

  // TODO: This function probably doesn't belong here... I dunno, but just feels a bit backwards to me for a definition to be evaluating outputs?
  def evaluateOutputs(//knownInputs: WorkflowCoercedInputs,
                      wdlFunctions: WdlFunctions[WdlValue]
                      //outputResolver: OutputResolver = NoOutputResolver,
                      //shards: Map[Scatter, Int] = Map.empty[Scatter, Int]
                     ): Try[Map[Callable.OutputDefinition, WdlValue]] = {

    def evaluateOutput(outputMap: Map[Callable.OutputDefinition, Try[WdlValue]], outputDefinition: Callable.OutputDefinition): Map[Callable.OutputDefinition, Try[WdlValue]] = {
      //      val currentOutputs = outputMap collect {
      //        case (outputName, value) if value.isSuccess => outputName.fullyQualifiedName -> value.get
      //      }
      //def knownValues = currentOutputs ++ knownInputs
      val lookup = NoLookup //lookupFunction(knownValues, wdlFunctions, outputResolver, shards, output)
      val coerced = outputDefinition.expression.evaluate(lookup, wdlFunctions) flatMap outputDefinition.womType.coerceRawValue
      val workflowOutput = outputDefinition -> coerced

      outputMap + workflowOutput
    }

    val evaluatedOutputs = outputs.foldLeft(Map.empty[Callable.OutputDefinition, Try[WdlValue]])(evaluateOutput)

    TryUtil.sequenceMap(evaluatedOutputs, "Failed to evaluate workflow outputs.\n")
  }
}

object Callable {
  sealed trait InputDefinition {
    def name: String
    def womType: WdlType
  }
  final case class DeclaredInputDefinition(name: String, womType: WdlType, expression: WdlExpression) extends InputDefinition
  final case class RequiredInputDefinition(name: String, womType: WdlType) extends InputDefinition
  // Might be another input definition type, InputDefinitionWithDefault
  case class OutputDefinition(name: String, womType: WdlType, expression: WdlExpression)
}
