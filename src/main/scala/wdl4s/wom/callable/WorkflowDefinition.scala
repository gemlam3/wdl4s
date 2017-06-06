package wdl4s.wom.callable

import wdl4s.wom.graph.GraphNodePort.WorkflowInputSource
import wdl4s.wom.graph.GraphNode

import scala.language.postfixOps

final case class WorkflowDefinition(name: String,
                                    inputs: Set[_ <: Callable.InputDefinition],
                                    outputs: Set[Callable.OutputDefinition],
                                    inputlessTemplateGraph: Set[_ >: GraphNode],
                                    meta: Map[String, String],
                                    parameterMeta: Map[String, String]) extends Callable {

  override def toString = s"[Workflow $name]"

  /**
    * Given a set of links that can provide all the inputs, generate a fully connected graph.
    *
    * @param inputLinkings Linkings from outputs of other graph nodes to all inputs required by this callable's definition.
    * @return The graph of this callable, linked to the specified inputs.
    */
  private def linkInputs(inputLinkings: Map[Callable.InputDefinition, WorkflowInputSource]): Set[GraphNode] = ???

  // TODO: Stamp out a completed graph from the templateGraph (whatever that looks like)

  override def graph(inputLinkings: Map[Callable.InputDefinition, WorkflowInputSource]): Set[GraphNode] = linkInputs(inputLinkings)
}
