package wdl4s

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
import wdl4s.wom.callable.Callable.{DeclaredInputDefinition, OutputDefinition, RequiredInputDefinition}
import wdl4s.wom.callable.{Callable, TaskDefinition, WorkflowDefinition}
import wdl4s.wom.graph.TaskCall
import org.rabix.bindings.model.Application
import cats.syntax.either._

package object cwl {

  //TODO how to provide WDLExpressions?
  def outputDefinitions(app: Application): Set[Callable.OutputDefinition] = app.getOutputs.asScala.map{
    i => OutputDefinition(i.getId, typeMap(i.getDataType.getType), null)
  }.toSet

  def inputDefinitions(app: Application): Set[Callable.InputDefinition] = app.getInputs.asScala.map{
    ip =>
      RequiredInputDefinition(ip.getId, typeMap(ip.getDataType.getType)): Callable.InputDefinition // this map lookup will easily break
  }.toSet

  def parseCommandLineTool(in: String): Either[Throwable, CWLCommandLineTool] = {
    val appUrl = URIHelper.createDataURI(in)
    Either.catchNonFatal{
      BindingsFactory.create(appUrl)
        .translateToDAG(new Job(appUrl, new java.util.HashMap[String, AnyRef]()))
        .getApp
        .asInstanceOf[CWLCommandLineTool]
    }
  }

  def parseWorkflow(path: String): Either[Throwable, CWLWorkflow] = {
    val appUrl = URIHelper.createURI(URIHelper.FILE_URI_SCHEME, path)
    Either.catchNonFatal{
      BindingsFactory
        .create(appUrl)
        .translateToDAG(new Job(appUrl, new java.util.HashMap[String, AnyRef]()))
        .getApp
        .asInstanceOf[CWLWorkflow]
    }
  }

  def baseCommand(app: CWLCommandLineTool): Either[Throwable, List[StringCommandPart]] = 
    Either.catchNonFatal{
      app.getBaseCmd(null). //You might think this null is gross but the impl does not use the arg
        asInstanceOf[java.util.List[String]]. //I know. Ewww.
        asScala.
        map(StringCommandPart.apply).
        toList
    }

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

}
