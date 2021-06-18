import sbt._
import scala.sys.process._

object AttributeGen {

  def genAttributesAndProperties: String =
    s"""  def attribute(name: String, value: String): Attr[Nothing]  = Attribute(name, value)
    |  def attributes(as: (String, String)*): List[Attr[Nothing]] = as.toList.map(p => Attribute(p._1, p._2))
    |  def property(name: String, value: String): Attr[Nothing]   = Property(name, value)
    |  def properties(ps: (String, String)*): List[Attr[Nothing]] = ps.toList.map(p => Property(p._1, p._2))
    |
    |  def onEvent[E <: org.scalajs.dom.Event, M](name: String, msg: E => M): Attr[M] = Event(name, msg)
    |  
    |""".stripMargin

  def genAttr(tag: AttributeType): String =
    tag match {
      case Normal(name, attrName, types) => genNormal(name, attrName, types)
      case NoValue(name, attrName)       => genNoValue(name, attrName)
      case EventEmitting(name, attrName) => genEventEmitting(name, attrName)
    }

  def genNormal(attrName: String, realName: Option[String], types: List[String]): String = {
    val attr = realName.getOrElse(attrName.toLowerCase)
    types.map { t =>
      s"""  def $attrName(value: $t): Attr[Nothing] = Attribute("$attr", ${if (t == "String") "value"
      else "value.toString"})
      |
      |""".stripMargin
    }.mkString
  }

  def genNoValue(attrName: String, realName: Option[String]): String = {
    val attr = realName.getOrElse(attrName.toLowerCase)
    s"""  def $attrName: Attr[Nothing] = Attribute("$attr", "$attr")
    |
    |""".stripMargin
  }

  def genEventEmitting(attrName: String, realName: Option[String]): String = {
    val attr = realName.getOrElse {
      val n = attrName.toLowerCase
      if(n.startsWith("on")) n.substring(2)
      else n
    }
    s"""  def $attrName[M](msg: M): Attr[M] = onEvent("$attr", (_: dom.Event) => msg)
    |
    |""".stripMargin
  }

  def template(moduleName: String, fullyQualifiedPath: String, contents: String): String =
    s"""package $fullyQualifiedPath
    |
    |import org.scalajs.dom
    |
    |// GENERATED by AttributeGen.scala - DO NOT EDIT
    |trait $moduleName {
    |
    |$contents
    |
    |}
    """.stripMargin

  def gen(moduleName: String, fullyQualifiedPath: String, sourceManagedDir: File): Seq[File] = {
    println("Generating Html Tags")

    val contents: String =
      genAttributesAndProperties + attrList.map(genAttr).mkString

    val file: File =
      sourceManagedDir / (moduleName + ".scala")

    val newContents: String =
      template(moduleName, fullyQualifiedPath, contents)

    IO.write(file, newContents)

    println("Written: " + file.getCanonicalPath)

    Seq(file)
  }

  def attrList: List[AttributeType] =
    List(
      Normal("accept"),
      Normal("accessKey"),
      Normal("action"),
      Normal("alt"),
      NoValue("async"),
      Normal("autoComplete"),
      NoValue("autoFocus"),
      NoValue("autoPlay"),
      Normal("charset"),
      NoValue("checked"),
      Normal("cite"),
      Normal("`class`", "class"),
      Normal("cls", "class"),
      Normal("className", "class"),
      Normal("_class", "class"),
      Normal("cols").withTypes("String", "Int"),
      Normal("colSpan").withTypes("String", "Int"),
      Normal("content"),
      Normal("contentEditable").withTypes("String", "Boolean"),
      NoValue("controls"),
      Normal("coords"),
      Normal("data"),
      Normal("dateTime"),
      NoValue("default"),
      NoValue("defer"),
      Normal("dir"),
      Normal("dirname"),
      NoValue("disabled"),
      NoValue("download"),
      Normal("draggable").withTypes("String", "Boolean"),
      Normal("encType"),
      Normal("forId", "for"),
      Normal("htmlFor", "for"),
      Normal("form"),
      Normal("formAction"),
      Normal("headers"),
      Normal("height").withTypes("String", "Int"),
      NoValue("hidden"),
      Normal("high").withTypes("String", "Int", "Double"),
      Normal("href"),
      Normal("hrefLang"),
      Normal("http"),
      Normal("id"),
      NoValue("isMap"),
      Normal("kind"),
      Normal("label"),
      Normal("lang"),
      Normal("list"),
      NoValue("loop"),
      Normal("low").withTypes("String", "Int", "Double"),
      Normal("max").withTypes("String", "Int"),
      Normal("maxLength").withTypes("String", "Int"),
      Normal("media"),
      Normal("method"),
      Normal("min").withTypes("String", "Int"),
      Normal("multiple"),
      Normal("muted"),
      Normal("name"),
      NoValue("noValidate"),
      EventEmitting("onAbort"),
      EventEmitting("onAfterPrint"),
      EventEmitting("onBeforePrint"),
      EventEmitting("onBeforeUnload"),
      EventEmitting("onBlur"),
      EventEmitting("onCanPlay"),
      EventEmitting("onCanPlayThrough"),
      EventEmitting("onChange", "change"),
      EventEmitting("onClick", "click"),
      EventEmitting("onContextMenu"),
      EventEmitting("onCopy"),
      EventEmitting("onCueChange"),
      EventEmitting("onCut"),
      EventEmitting("onDblClick"),
      EventEmitting("onDoubleClick", "dblclick"),
      EventEmitting("onDrag"),
      EventEmitting("onDragEnd"),
      EventEmitting("onDragEnter"),
      EventEmitting("onDragLeave"),
      EventEmitting("onDragOver"),
      EventEmitting("onDragStart"),
      EventEmitting("onDrop"),
      EventEmitting("onDurationChange"),
      EventEmitting("onEmptied"),
      EventEmitting("onEnded"),
      EventEmitting("onError"),
      EventEmitting("onFocus"),
      EventEmitting("onHashChange"),
      EventEmitting("onInput"),
      EventEmitting("onInvalid"),
      EventEmitting("onKeyDown"),
      EventEmitting("onKeyPress"),
      EventEmitting("onKeyUp"),
      EventEmitting("onLoad"),
      EventEmitting("onLoadedData"),
      EventEmitting("onLoadedMetadata"),
      EventEmitting("onLoadStart"),
      EventEmitting("onMouseDown"),
      EventEmitting("onMousemove"),
      EventEmitting("onMouseOut"),
      EventEmitting("onMouseOver"),
      EventEmitting("onMouseUp"),
      EventEmitting("onMouseWheel"),
      EventEmitting("onOffline"),
      EventEmitting("onOnline"),
      EventEmitting("onPageHide"),
      EventEmitting("onPageShow"),
      EventEmitting("onPaste"),
      EventEmitting("onPause"),
      EventEmitting("onPlay"),
      EventEmitting("onPlaying"),
      EventEmitting("onPopState"),
      EventEmitting("onProgress"),
      EventEmitting("onRateChange"),
      EventEmitting("onReset"),
      EventEmitting("onResize"),
      EventEmitting("onScroll"),
      EventEmitting("onSearch"),
      EventEmitting("onSeeked"),
      EventEmitting("onSeeking"),
      EventEmitting("onSelect"),
      EventEmitting("onStalled"),
      EventEmitting("onStorage"),
      EventEmitting("onSubmit"),
      EventEmitting("onSuspend"),
      EventEmitting("onTimeUpdate"),
      EventEmitting("onToggle"),
      EventEmitting("onUnload"),
      EventEmitting("onVolumeChange"),
      EventEmitting("onWaiting"),
      EventEmitting("onWheel"),
      NoValue("open"),
      Normal("optimum").withTypes("String", "Int", "Double"),
      Normal("pattern"),
      Normal("placeholder"),
      Normal("poster"),
      Normal("preload"),
      NoValue("readOnly"),
      Normal("rel"),
      NoValue("required"),
      NoValue("reversed"),
      Normal("rows").withTypes("String", "Int"),
      Normal("rowSpan").withTypes("String", "Int"),
      NoValue("sandbox"),
      Normal("scope"),
      NoValue("selected"),
      Normal("shape"),
      Normal("size").withTypes("String", "Int"),
      Normal("sizes"),
      Normal("span").withTypes("String", "Int"),
      Normal("spellCheck").withTypes("String", "Boolean"),
      Normal("src"),
      Normal("srcDoc"),
      Normal("srcLang"),
      Normal("srcSet"),
      Normal("start").withTypes("String", "Int"),
      Normal("step").withTypes("String", "Int"),
      Normal("style"),
      Normal("tabIndex").withTypes("String", "Int"),
      Normal("target"),
      Normal("title"),
      Normal("translate"),
      Normal("`type`", "type"),
      Normal("_type", "type"),
      Normal("typ", "type"),
      Normal("tpe", "type"),
      Normal("useMap"),
      Normal("value").withTypes("String", "Int", "Boolean"),
      Normal("width").withTypes("String", "Int"),
      Normal("wrap")
    )

}

sealed trait AttributeType
final case class Normal(name: String, attrName: Option[String], types: List[String]) extends AttributeType {
  def withTypes(newTypes: String*): Normal =
    Normal(name, attrName, newTypes.toList)
}
object Normal {
  def apply(name: String): Normal                   = Normal(name, None, List("String"))
  def apply(name: String, attrName: String): Normal = Normal(name, Some(attrName), List("String"))
}
final case class NoValue(name: String, attrName: Option[String]) extends AttributeType
object NoValue {
  def apply(name: String): NoValue                   = NoValue(name, None)
  def apply(name: String, attrName: String): NoValue = NoValue(name, Some(attrName))
}
final case class EventEmitting(name: String, attrName: Option[String]) extends AttributeType
object EventEmitting {
  def apply(name: String): EventEmitting                   = EventEmitting(name, None)
  def apply(name: String, attrName: String): EventEmitting = EventEmitting(name, Some(attrName))
}
