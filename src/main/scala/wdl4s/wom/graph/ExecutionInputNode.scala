package wdl4s.wom.graph

import wdl4s.wdl.types.WdlType
import wdl4s.wom.graph.GraphNodePort.WorkflowInputSource

final case class ExecutionInputNode(name: String, womType: WdlType) extends GraphNode {
  override def inputPorts: Set[GraphNodePort.InputPort] = Set.empty
  override def outputPorts: Set[GraphNodePort.OutputPort] = Set(singleOutputPort)
  val singleOutputPort: WorkflowInputSource = WorkflowInputSource(this)
}
