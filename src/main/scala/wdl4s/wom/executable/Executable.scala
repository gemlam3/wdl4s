package wdl4s.wom.executable

import wdl4s.wom.callable.Callable
import wdl4s.wom.graph.{ExecutionInputNode, GraphNode}
import wdl4s.wom.graph.GraphNodePort.WorkflowInputSource

/**
  * Closely related to the WdlNamespace, contains a set of Workflows and Tasks with a single Callable selected as the
  * entry point.
  */
final case class Executable(entryPoint: Callable) {
  def graph: Set[_ >: GraphNode] = {
    val inputNodes: Map[Callable.InputDefinition, ExecutionInputNode] = entryPoint.inputs.toList.map { i =>
      i -> ExecutionInputNode(i.name, i.womType)
    }.toMap

    val inputLinkings: Map[Callable.InputDefinition, WorkflowInputSource] = inputNodes.map { case (i, n) =>
      i -> n.singleOutputPort
    }

    inputNodes.values.toSet ++ entryPoint.graph(inputLinkings)
  }
}
