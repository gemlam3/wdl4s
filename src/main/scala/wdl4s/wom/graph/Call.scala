package wdl4s.wom.graph

import wdl4s.wom.callable.Callable.RequiredInputDefinition
import wdl4s.wom.callable.{Callable, TaskDefinition, WorkflowDefinition}
import wdl4s.wom.graph.GraphNodePort.{DeclarationOutputPort, InputPort, UnsatisfiedInputPort}

import scala.language.postfixOps

// TODO: Lots of copy/paste between the two call types. It'd be nice to abstract it out and validate that all inputs are correctly provided.
sealed trait Call extends GraphNode {
  def name: String
  def callType: String
  def callable: Callable

  val inputMapping: Map[Callable.InputDefinition, InputPort] = callable.inputs collect {
    case inputDef @ RequiredInputDefinition(name, womType) => inputDef -> UnsatisfiedInputPort(this, name, womType)
  } toMap
}

/**
  * In WDL, a call to a task. In CWL, a task step (or single task execution, of course).
  */
trait TaskCall extends Call {
  def task: TaskDefinition
  override val callType = "call"
  override val callable = task

  override lazy val outputPorts: Set[GraphNodePort.OutputPort] = task.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
  private[graph] override final def copyWithInputsReplaced(inputs: Set[InputPort]): Graph = ConnectedTaskCall(name, task, inputs)
}

private final case class UnconnectedTaskCall(name: String, task: TaskDefinition) extends TaskCall {
  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
}
private final case class ConnectedTaskCall(name: String, task: TaskDefinition, suppliedInputs: Set[GraphNodePort.InputPort]) extends TaskCall {
  override def inputPorts: Set[GraphNodePort.InputPort] = suppliedInputs
}

object TaskCall {
  def apply(name: String, task: TaskDefinition): TaskCall = UnconnectedTaskCall(name, task)
}

sealed trait WorkflowCall extends Call {
  def workflow: WorkflowDefinition
  override val callType = "workflow"
  override val callable = workflow

  override lazy val outputPorts: Set[GraphNodePort.OutputPort] = workflow.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
  private[graph] override final def copyWithInputsReplaced(inputs: Set[InputPort]): Graph = ConnectedWorkflowCall(name, workflow, inputs)
}

private final case class UnconnectedWorkflowCall(name: String, workflow: WorkflowDefinition) extends WorkflowCall {
  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
}

private final case class ConnectedWorkflowCall(name: String, workflow: WorkflowDefinition, suppliedInputs: Set[GraphNodePort.InputPort]) extends WorkflowCall {
  override def inputPorts: Set[GraphNodePort.InputPort] = suppliedInputs
}
