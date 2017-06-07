package wdl4s.wom.executable

import wdl4s.wom.callable.Callable
import wdl4s.wom.graph.{ExecutionInputNode, Graph}
import wdl4s.wom.graph.GraphNodePort.WorkflowInputSource

/**
  * Closely related to the WdlNamespace, contains a set of Workflows and Tasks with a single Callable selected as the
  * entry point.
  */
final case class Executable(entryPoint: Callable) {
  def graph: Graph = {
    val inputNodes: Map[Callable.InputDefinition, ExecutionInputNode] = entryPoint.inputs.toList.map { i =>
      i -> ExecutionInputNode(i.name, i.womType)
    }.toMap

    val inputLinkings: Map[Callable.InputDefinition, WorkflowInputSource] = inputNodes.map { case (i, n) =>
      i -> n.singleOutputPort
    }

    Graph.fromNodes(inputNodes.values.toSet ++ entryPoint.graph.nodes)
  }
}
