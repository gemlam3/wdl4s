package wdl4s.wom.graph

import wdl4s.wdl.types.WdlType

sealed trait GraphNodePort {
  def graphNode: GraphNode
}

object GraphNodePort {

  sealed trait InputPort extends GraphNodePort {
    def name: String
    def womType: WdlType
    def prerequisites: Set[OutputPort]
  }

  sealed trait OutputPort extends GraphNodePort {
    def name: String
    def womType: WdlType

    // TODO: Might want a backwards link to the InputPorts that use this?
  }

  final case class WorkflowOutputSink(executionOutput: ExecutionOutputNode) extends InputPort {
    override val name = executionOutput.name
    override val womType = executionOutput.womType
    override val graphNode = executionOutput

    override def prerequisites: Set[OutputPort] = ???
  }

  /**
    * For any graph node that uses a declarations to produce outputs (e.g. call, declaration):
    */
  final case class DeclarationOutputPort(graphNode: GraphNode, name: String, womType: WdlType) extends OutputPort
  // TODO: final case class ScatterOutputPort(...)
  // TODO: final case class ConditionalOutputPort(...)

  /**
    * For workflow inputs to provide values as a source:
    */
  final case class WorkflowInputSource(executionInput: ExecutionInputNode) extends OutputPort {
    override val name = executionInput.name
    override val womType = executionInput.womType
    override val graphNode = executionInput
  }
}
