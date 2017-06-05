package wdl4s.wom.graph

import wdl4s.wdl.WdlExpression
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
  }

  /**
    * For any graph node that uses declarations to produce outputs:
    */
  final case class DeclarationOutputPort(graphNode: GraphNode, name: String, womType: WdlType, expression: WdlExpression)
  // TODO: final case class ScatterOutputPort(...)
  // TODO: final case class ConditionalOutputPort(...)

  final case class WorkflowInputSource(workflowInput: WorkflowInput) extends OutputPort {
    override val name = workflowInput.name
    override val womType = workflowInput.womType
    override val graphNode = workflowInput
  }
}
