package wdl4s.cwl

import org.scalatest.{FlatSpec, Matchers}
import wdl4s.wdl.expression.NoFunctions
import wdl4s.wdl.values._

import scala.util.Try

import org.rabix.bindings.cwl.bean.{CWLCommandLineTool, CWLInputPort, CWLWorkflow}
import wdl4s._
import org.rabix.bindings.BindingsFactory
import org.rabix.bindings.helper.URIHelper
import org.rabix.bindings.model.Job
import java.util.Collections

import wdl4s.wdl.{RuntimeAttributes, WdlExpression}

import scala.collection.JavaConverters._
import wdl4s.wdl.command.StringCommandPart
import wdl4s.wdl.types._
import wdl4s.wdl.values.WdlString
import wdl4s.wom.callable.Callable.{DeclaredInputDefinition, RequiredInputDefinition}
import wdl4s.wom.callable.{Callable, TaskDefinition, WorkflowDefinition}
import wdl4s.wom.graph.TaskCall

class FirstWorkflowSpec extends FlatSpec with Matchers {

  val namespace = "First Workflow"

  it should "parse right" in {

    val app2 = parseWorkflow("/Users/danb/wdl4s/src/test/resources/1st-workflow.cwl").right.get

    val wd = WorkflowDefinition(
      name = "",
      inputs = Set.empty,
      outputs = Set.empty,
      graph = Set.empty,
      meta = Map.empty,
    parameterMeta = Map.empty)

  }
}

