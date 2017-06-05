package wdl4s.wom.graph

import wdl4s.wom.callable.{Callable, TaskDefinition}
import wdl4s.wom.graph.GraphNodePort.{DeclarationOutputPort, InputPort, OutputPort, RequiredInputPort}

trait Call extends GraphNode {
  def callType: String
}

case class TaskCall(name: String, task: TaskDefinition, inputLinks: Map[Callable.InputDefinition, OutputPort]) extends Call {
  override val callType = "call"

  private val inputMapping: Map[Callable.InputDefinition, InputPort] = inputLinks map {
    case (inputDef, output) => inputDef -> RequiredInputPort(this, inputDef.name, inputDef.womType, output)
  }

  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
  override def outputPorts: Set[GraphNodePort.OutputPort] = task.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
}
