package wdl4s.wom.graph

import wdl4s.wom.callable.{Callable, TaskDefinition, WorkflowDefinition}
import wdl4s.wom.graph.GraphNodePort.{DeclarationOutputPort, InputPort, OutputPort, RequiredInputPort}

// TODO: Lots of copy/paste between the two call types. It'd be nice to abstract it out and validate that all inputs are correctly provided.
trait Call extends GraphNode {
  def callType: String
}

/**
  * In WDL, a call to a task. In CWL, a task step (or single task execution, of course).
  *
  * @param name The name or alias of the task call
  * @param task The definition of the task being called
  * @param inputLinks All of the inputs required by the task definition, mapped to the output ports of upstream graph nodes.
  */
case class TaskCall(name: String, task: TaskDefinition, inputLinks: Map[Callable.InputDefinition, OutputPort]) extends Call {
  override val callType = "call"

  private val inputMapping: Map[Callable.InputDefinition, InputPort] = inputLinks map {
    case (inputDef, output) => inputDef -> RequiredInputPort(this, inputDef.name, inputDef.womType, output)
  }

  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
  override def outputPorts: Set[GraphNodePort.OutputPort] = task.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
}

/**
  * A workflow being called or executed.
  *
  * @param name The name or alias of the task call
  * @param workflow The definition of the workflow being called
  * @param inputLinks All of the inputs required by the task definition, mapped to the output ports of upstream graph nodes.
  */
case class WorkflowCall(name: String, workflow: WorkflowDefinition, inputLinks: Map[Callable.InputDefinition, OutputPort]) extends Call {
  override val callType = "call"

  private val inputMapping: Map[Callable.InputDefinition, InputPort] = inputLinks map {
    case (inputDef, output) => inputDef -> RequiredInputPort(this, inputDef.name, inputDef.womType, output)
  }

  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
  override def outputPorts: Set[GraphNodePort.OutputPort] = workflow.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
}
