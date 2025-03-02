package tyrian.runtime

import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom._
import snabbdom.modules._
import tyrian.Attr
import tyrian.Attribute
import tyrian.Event
import tyrian.Html
import tyrian.NamedAttribute
import tyrian.Property
import tyrian.PropertyBoolean
import tyrian.PropertyString
import tyrian.RawTag
import tyrian.Tag
import tyrian.Text

object Rendering:

  private def buildNodeData[Msg](attrs: List[Attr[Msg]], onMsg: Msg => Unit): VNodeData =
    val as: List[(String, String)] =
      attrs.collect {
        case Attribute(n, v)   => (n, v)
        case NamedAttribute(n) => (n, "")
      }

    val props: List[(String, PropValue)] =
      attrs.collect {
        case PropertyString(n, v)  => (n, v)
        case PropertyBoolean(n, v) => (n, v)
      }

    val events: List[(String, EventHandler)] =
      attrs.collect { case Event(n, msg, preventDefault, stopPropagation, stopImmediatePropagation) =>
        val callback: dom.Event => Unit = { (e: dom.Event) =>
          if preventDefault then e.preventDefault()
          if stopPropagation then e.stopPropagation()
          if stopImmediatePropagation then e.stopImmediatePropagation()

          onMsg(msg.asInstanceOf[dom.Event => Msg](e))
        }

        (n, EventHandler(callback))
      }

    VNodeData.empty.copy(
      props = props.toMap,
      attrs = as.toMap,
      on = events.toMap
    )

  def toVNode[Msg](html: Html[Msg], onMsg: Msg => Unit): VNode =
    html match
      case RawTag(name, attrs, html) =>
        val data = buildNodeData(attrs, onMsg)
        val elm  = dom.document.createElement(name)
        elm.innerHTML = html
        val vNode = snabbdom.toVNode(elm)
        vNode.data = data
        vNode

      case Tag(name, attrs, children) =>
        val data = buildNodeData(attrs, onMsg)
        val childrenElem: Array[VNode] =
          children.toArray.map {
            case t: Text            => VNode.text(t.value)
            case subHtml: Html[Msg] => toVNode(subHtml, onMsg)
          }

        h(name, data, childrenElem)

  private lazy val patch: Patch =
    snabbdom.init(
      Seq(
        Attributes.module,
        Classes.module,
        Props.module,
        Styles.module,
        EventListeners.module,
        Dataset.module
      )
    )

  def render[Model, Msg](oldNode: Element | VNode, model: Model, view: Model => Html[Msg], onMsg: Msg => Unit): VNode =
    oldNode match
      case em: Element => patch(em, Rendering.toVNode(view(model), onMsg))
      case vn: VNode   => patch(vn, Rendering.toVNode(view(model), onMsg))
