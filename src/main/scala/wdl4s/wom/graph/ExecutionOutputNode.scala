package wdl4s.wom.graph

import wdl4s.wdl.WdlExpression
import wdl4s.wdl.types.WdlType
import wdl4s.wom.graph.GraphNodePort.WorkflowOutputSink

final case class ExecutionOutputNode(name: String, womType: WdlType, expression: WdlExpression) extends GraphNode {
  override def inputPorts: Set[GraphNodePort.InputPort] = Set(WorkflowOutputSink(this))
  override def outputPorts: Set[GraphNodePort.OutputPort] = Set.empty
}
