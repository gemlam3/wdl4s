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

case class Cwl(cwlVersion: String, `class`: String, baseCommand: String, inputs: Message, outputs: Array[Nothing])
case class Message(`type`: String, inputBinding: InputBinding)
case class InputBinding(position: Int)

object Converter extends App{
  /*
  ASSUMPTIONS

  Input bindings (i.e. relative positions on command line) are not respected, as one must be explicit about inputs in cromwell.
   */

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

  //ASSUMPTIONS
  val taskName = "Synthesized because CWL has no overall name for this task"
  val taskCallName = "Why does this need a name?"
  val _runtimeAttributes = RuntimeAttributes(Map.empty[String, WdlExpression]) //Not sure how CWL declares these if at all
  val workflowName = "Workflows aren't explicitly stated in CWL CommandLineTool"


  //Bunny magic
  val appUrl = URIHelper.createDataURI(firstToolCwl)
  val b = BindingsFactory.create(appUrl)

  //val inputs:java.util.Map[String, AnyRef] = Collections.singletonMap("in", "bla")  //Need the type annotation here for next line
  //val node = b.translateToDAG(new Job(appUrl, inputs))
  val node = b.translateToDAG(new Job(appUrl, new java.util.HashMap[String, AnyRef]()))
  val app = node.getApp.asInstanceOf[CWLCommandLineTool]

  import org.rabix.bindings.model.DataType.Type._

  val typeMap: Map[org.rabix.bindings.model.DataType.Type, WdlType] =
    Map(
      STRING -> WdlStringType,
      BOOLEAN -> WdlBooleanType,
      INT -> WdlIntegerType,
      FLOAT -> WdlFloatType,
      FILE -> WdlFileType//,

      //TODO
      //MAP -> WdlMapType //Needs Type arguments
      //ARRAY -> WdlArrayType  //Needs type argument

    )
  val inputDefinitions: Set[Callable.InputDefinition] = app.getInputs.asScala.map{
    ip =>
      RequiredInputDefinition(ip.getId, typeMap(ip.getDataType.getType))
  }.toSet

  val baseCommand = app.getBaseCmd(null).asInstanceOf[java.util.List[String]].asScala.map(StringCommandPart.apply)


  //need input definition from inputs

  val _task = TaskDefinition(
    name = taskName,// does this apply?
    commandTemplate = baseCommand,
    inputs = inputDefinitions,
    runtimeAttributes = _runtimeAttributes,

    //TODO
    meta = Map.empty,
    parameterMeta = Map.empty,
    outputs = Set.empty
  )


  val firstWorkflowTaskCall = TaskCall(
    name = taskCallName,
    task = _task,
    inputLinks = ???)


  //We need to synthesize a workflow since CWL CommandLine is basically a one-step workflow
  val firstTool = WorkflowDefinition(
    name = workflowName,
    inputs = Set.empty,
    outputs = Set.empty,
    graph = Set(firstWorkflowTaskCall),
    meta = Map.empty,
    parameterMeta = Map.empty)


  //******************
  //EXAMPLE 2
  //not actually used as bunny needs a file URI to work (I don't know why)
  //******************
  val firstWorkflowCwl= """
cwlVersion: v1.0
class: Workflow
inputs:
  inp: File
  ex: string

outputs:
  classout:
    type: File
    outputSource: compile/classfile

steps:
  untar:
    run: tar-param.cwl
    in:
      tarfile: inp
      extractfile: ex
    out: [example_out]

  compile:
    run: arguments.cwl
    in:
      src: untar/example_out
    out: [classfile]

""".stripMargin
  val appUrl2 = URIHelper.createURI(URIHelper.FILE_URI_SCHEME, "/Users/danb/wdl4s/1st-workflow.cwl")
  val b2 = BindingsFactory.create(appUrl);

  val inputs2:java.util.Map[String, AnyRef] = Collections.singletonMap("in", "bla");
  val node2 = b2.translateToDAG(new Job(appUrl2, inputs2))

  val app2 = node2.getApp.asInstanceOf[CWLWorkflow]

  val wd = WorkflowDefinition(
    name = "",
    inputs = Set.empty,
    outputs = Set.empty,
    graph = Set.empty,
    meta = Map.empty,
    parameterMeta = Map.empty)

}


