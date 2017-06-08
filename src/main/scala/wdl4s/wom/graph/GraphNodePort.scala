package wdl4s.wom.graph

import wdl4s.wdl.types.{WdlArrayType, WdlOptionalType, WdlType}
import wdl4s.wdl.values.WdlValue

sealed trait GraphNodePort {
  def graphNode: GraphNode
}

object GraphNodePort {

  // TODO: It'd be really cool if these could be typed (eg InputPort[WdlString], OutputPort[WdlInteger] but
  // TODO: we'd have to think about coercion... maybe some sort of implicit CoercionSocket[WdlString, WdlInteger]...?
  sealed trait InputPort extends GraphNodePort {
    def name: String
    def womType: WdlType
    def upstream: Option[OutputPort]
  }
  sealed trait UnconnectedInputPort extends InputPort {
    override def upstream = None
  }

  sealed trait OutputPort extends GraphNodePort {
    def name: String
    def womType: WdlType

    // TODO: Might end up wanting a backwards link to the InputPorts that use this (eg def downstream: Set[InputPort])?
  }

  final case class UnsatisfiedInputPort(graphNode: GraphNode, name: String, womType: WdlType) extends UnconnectedInputPort
  final case class UnconnectedInputPortWithDefault(graphNode: GraphNode, name: String, womType: WdlType, default: WdlValue) extends UnconnectedInputPort
  final case class ConnectedInputPort(graphNode: GraphNode, name: String, womType: WdlType, upstreamPort: OutputPort) extends InputPort {
    override def upstream = Option(upstreamPort)
  }

  final case class WorkflowOutputSink(executionOutput: ExecutionOutputNode) extends InputPort {
    override val name = executionOutput.name
    override val womType = executionOutput.womType
    override val graphNode = executionOutput

    override def upstream: Option[OutputPort] = ??? // TODO: executionOutput.expression.prerequisiteGraphNodes
  }

  /**
    * For any graph node that uses a declarations to produce outputs (e.g. call, declaration):
    */
  final case class DeclarationOutputPort(graphNode: GraphNode, name: String, womType: WdlType) extends OutputPort

  // TODO: For these next two, the graphNode should be a ScatterNode and IfNode respectively (once those exist):
  /**
    * Represents the gathered output from a call/declaration in a scatter.
    */
  final case class ScatterGathererPort(graphNode: GraphNode, name: String, womType: WdlArrayType, outputToGather: OutputPort) extends OutputPort
  final case class ConditionalOutputPort(graphNode: GraphNode, name: String, womType: WdlOptionalType, outputToExpose: OutputPort) extends OutputPort

  /**
    * For workflow inputs to provide values as a source:
    */
  final case class WorkflowInputSource(executionInput: ExecutionInputNode) extends OutputPort {
    override val name = executionInput.name
    override val womType = executionInput.womType
    override val graphNode = executionInput
  }
}
