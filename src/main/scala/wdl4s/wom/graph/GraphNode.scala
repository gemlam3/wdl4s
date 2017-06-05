package wdl4s.wom.graph

import wdl4s.wdl.WdlExpression
import wdl4s.wdl.types.WdlType
import wdl4s.wom.graph.GraphNode._

trait GraphNode {
  /**
    * The set of all graph nodes which are (transitively) upstream from this one.
    */
  lazy val upstreamAncestry = calculateUpstreamAncestry(Set.empty, this)
  lazy val upstream: Set[GraphNode] = inputPorts.map(_.graphNode)

  def inputPorts: Set[GraphNodePort.InputPort]
  def outputPorts: Set[GraphNodePort.OutputPort]
}

object GraphNode {
  // A recursive traversal with a fancy trick to avoid double-counting:
  private def calculateUpstreamAncestry(currentSet: Set[GraphNode], graphNode: GraphNode): Set[GraphNode] = {
    val setWithUpstream = currentSet ++ graphNode.upstream
    val updatesNeeded = graphNode.upstream -- currentSet
    updatesNeeded.foldLeft(setWithUpstream)(calculateUpstreamAncestry)
  }
}
