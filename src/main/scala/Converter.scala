import org.rabix.bindings.cwl.bean.{CWLCommandLineTool, CWLInputPort, CWLWorkflow}
import wdl4s._
import org.rabix.bindings.BindingsFactory
import org.rabix.bindings.helper.URIHelper
import org.rabix.bindings.model.Job
import java.util.Collections

import scala.collection.JavaConverters._
import wdl4s.wdl._
import wdl4s.wdl.command.StringCommandPart
import wdl4s.wom.callable.Callable.DeclaredInputDefinition
import wdl4s.wom.callable.TaskDefinition

case class Cwl(cwlVersion: String, `class`: String, baseCommand: String, inputs: Message, outputs: Array[Nothing])
case class Message(`type`: String, inputBinding: InputBinding)
case class InputBinding(position: Int)

object Converter extends App{

  //******************
  //EXAMPLE 1
  //******************
  val appUrl = URIHelper.createDataURI(O.i2);
  val b = BindingsFactory.create(appUrl);

  val inputs:java.util.Map[String, AnyRef] = Collections.singletonMap("in", "bla");
  val node = b.translateToDAG(new Job(appUrl, inputs));
  val app = node.getApp.asInstanceOf[CWLCommandLineTool]

//  println(node.getApp)
  println(s"inputs: ${app.getInputs}")
//  println(s"version: ${app.getVersion}")
//  println(s"base command: ${app.getBaseCmd(null)}")


  val wf = WdlWorkflow(
    unqualifiedName = "",
    workflowOutputWildcards = Seq.empty,
    wdlSyntaxErrorFormatter = null,
    meta = Map.empty,
    parameterMeta = Map.empty,
    ast = null
  )

  app.getInputs.asScala.foreach{
    ip =>  println(ip.getDataType.getType)
  }

  //val declaredInputDefinition = DeclaredInputDefinition()

  val baseCommand = app.getBaseCmd(null).asInstanceOf[java.util.List[String]].asScala.map(StringCommandPart.apply)

  val _runtimeAttributes = RuntimeAttributes(Map.empty[String, WdlExpression])


  //val inputs = DeclaredInputDefinition()

  //TODO: WHERE DO INPUTS GO?
  val task = TaskDefinition(
    commandTemplate = baseCommand,

    //TODO
    name = "",// does this apply?
    runtimeAttributes = _runtimeAttributes,
    meta = Map.empty,
    parameterMeta = Map.empty,
    outputs = Set.empty  ,
    inputs = Set.empty
  )

  //******************
  //EXAMPLE 2
  //******************
  val appUrl2 = URIHelper.createURI(URIHelper.FILE_URI_SCHEME, "/Users/danb/wdl4s/1st-workflow.cwl")
  val b2 = BindingsFactory.create(appUrl);

  val inputs2:java.util.Map[String, AnyRef] = Collections.singletonMap("in", "bla");
  val node2 = b2.translateToDAG(new Job(appUrl2, inputs2))


  val app2 = node2.getApp.asInstanceOf[CWLWorkflow]
  //println(s"steps ${app2.getSteps}")
  //println(s"type ${app2.getType}")
  //println(s"data links ${app2.getDataLinks}")

}

object O {
  val i2 = """
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

  val firstWorkflowCwl=
"""
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
}


