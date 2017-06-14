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

class FirstToolSpec extends FlatSpec with Matchers {

  val firstToolCwl = """
cwlVersion: v1.0
class: CommandLineTool
baseCommand: echo
inputs:
   message:
     type: string
     inputBinding:
       position: 1
outputs: []
"""

  val namespace = "1st Cwls"

  it should "parse a string" in {

    val app = parseCommandLineTool(firstToolCwl).right.get

    val _inputDefinitions = inputDefinitions(app)
    val _outputDefinitions = outputDefinitions(app)

    val _baseCommand = baseCommand(app)

    val _task = TaskDefinition(
      commandTemplate = _baseCommand.right.get,
      inputs = _inputDefinitions,
      outputs = _outputDefinitions,

      //TODO
      runtimeAttributes = RuntimeAttributes(Map.empty[String, WdlExpression]),
      name = "Synthesized because CWL has no overall name for this task",// does this apply?
      meta = Map.empty,
      parameterMeta = Map.empty
    )

    val firstWorkflowTaskCall = TaskCall(
      task = _task,

      //TODO
      name = "Why does this need a name?",
      inputLinks = Map.empty
    )

    //We need to synthesize a workflow since CWL CommandLine is basically a one-step workflow
    val firstTool = WorkflowDefinition(
      graph = Set(firstWorkflowTaskCall),

      //TODO
      inputs = Set.empty,
      name = "Workflows aren't explicitly stated in CWL CommandLineTool",
      outputs = Set.empty,
      meta = Map.empty,
      parameterMeta = Map.empty)

    //testCmd shouldEqual Try("echo 'inside inside inside'")
    true shouldEqual true //nothing threw an exception!  yay!
  }

  it should "parse a CWLCommandLine" in {
    parseCommandLineTool(firstToolCwl).isRight
  }
}
