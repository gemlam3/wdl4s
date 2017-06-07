package wdl4s.wom.graph

import wdl4s.wom.callable.Callable.RequiredInputDefinition
import wdl4s.wom.callable.{Callable, TaskDefinition, WorkflowDefinition}
import wdl4s.wom.graph.GraphNodePort.{DeclarationOutputPort, InputPort, UnsatisfiedInputPort}

import scala.language.postfixOps

// TODO: Lots of copy/paste between the two call types. It'd be nice to abstract it out and validate that all inputs are correctly provided.
sealed trait Call extends GraphNode {
  def callType: String
  def callable: Callable

  val inputMapping: Map[Callable.InputDefinition, InputPort] = callable.inputs collect {
    case inputDef @ RequiredInputDefinition(name, womType) => inputDef -> UnsatisfiedInputPort(this, name, womType)
  } toMap
}

/**
  * In WDL, a call to a task. In CWL, a task step (or single task execution, of course).
  *
  * @param name The name or alias of the task call
  * @param task The definition of the task being called
  */
case class TaskCall(name: String, task: TaskDefinition) extends Call {
  override val callType = "call"
  override val callable = task

  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
  override def outputPorts: Set[GraphNodePort.OutputPort] = task.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
}

/**
  * A workflow being called or executed.
  *
  * @param name The name or alias of the task call
  * @param workflow The definition of the workflow being called
  */
case class WorkflowCall(name: String, workflow: WorkflowDefinition) extends Call {
  override val callType = "workflow"
  override val callable = workflow

  override def inputPorts: Set[GraphNodePort.InputPort] = inputMapping.values.toSet
  override def outputPorts: Set[GraphNodePort.OutputPort] = workflow.outputs.map(o => DeclarationOutputPort(this, o.name, o.womType))
}
