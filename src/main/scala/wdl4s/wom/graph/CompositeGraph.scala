package wdl4s.wom.graph

import lenthall.validation.ErrorOr.ErrorOr

private[graph] final case class CompositeGraph(nodes: Set[_ <: GraphNode]) extends Graph {
  override val inputPorts = ???
  override val outputPorts = ???

  // TODO: Implement
  override def withSuppliedInputs(inputs: Map[String, GraphNodePort.InputPort]): ErrorOr[Graph] = ???
}
