package wdl4s.wom.graph

import lenthall.validation.ErrorOr.ErrorOr
import wdl4s.wom.graph.GraphNode._
import wdl4s.wom.graph.GraphNodePort.{ConnectedInputPort, InputPort}
import cats.implicits._

trait GraphNode extends Graph {
  /**
    * The set of all graph nodes which are (transitively) upstream from this one.
    */
  lazy val upstreamAncestry = calculateUpstreamAncestry(Set.empty, this)
  lazy val upstream: Set[GraphNode] = inputPorts.collect {
    case ConnectedInputPort(_, _, _, outputPort) => outputPort.graphNode
  }
  final override val nodes = Set(this)

  private[graph] def copyWithInputsReplaced(inputs: Set[GraphNodePort.InputPort]): Graph

  /**
    * Makes a new graph of outputs joined to inputs.
    * @param inputs The inputs
    * @return A new graph containing the requested linkings
    */
  final override def withSuppliedInputs(inputs: Map[String, GraphNodePort.InputPort]): ErrorOr[Graph] = {
    // TODO: Implement
    ???
  }
}

object GraphNode {
  // A recursive traversal with a fancy trick to avoid double-counting:
  private def calculateUpstreamAncestry(currentSet: Set[GraphNode], graphNode: GraphNode): Set[GraphNode] = {
    val setWithUpstream = currentSet ++ graphNode.upstream
    val updatesNeeded = graphNode.upstream -- currentSet
    updatesNeeded.foldLeft(setWithUpstream)(calculateUpstreamAncestry)
  }

  def inputPortNamesMatch(required: Set[InputPort], provided: Set[InputPort]): ErrorOr[Unit] = {
    def requiredInputFound(r: InputPort): ErrorOr[Unit] = provided.find(_.name == r.name) match {
      case Some(p) => if (r.womType.isCoerceableFrom(p.womType)) ().validNel else s"Cannot link a ${p.womType.toWdlString} to the input ${r.name}: ${r.womType}".invalidNel
      case None => s"The required input ${r.name}: ${r.womType.toWdlString} was not provided.".invalidNel
    }

    required.toList.traverse(requiredInputFound).void
  }
}
