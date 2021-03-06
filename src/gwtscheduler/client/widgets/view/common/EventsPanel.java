package gwtscheduler.client.widgets.view.common;

import gwtscheduler.client.modules.AppInjector;
import gwtscheduler.client.widgets.common.event.AppointmentEvent;
import gwtscheduler.client.widgets.common.event.AppointmentHandler;
import gwtscheduler.client.widgets.common.event.WidgetResizeEvent;
import gwtscheduler.client.widgets.common.event.WidgetResizeHandler;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.Label;

/**
 * This class is responsible for displaying events.
 * @author malp
 */
public class EventsPanel extends AbstractGridOverlay implements WidgetResizeHandler, AppointmentHandler {

  /**
   * Default constructor.
   */
  public EventsPanel() {
    AppInjector.GIN.getInjector().getEventBus().addHandler(AppointmentEvent.getType(), this);
    getElement().getStyle().setPosition(Position.ABSOLUTE);
    //TODO fix zIndex
    //    getElement().getStyle().setZIndex(Constants.EVENTS_ZINDEX);
  }

  @Override
  public void onAddEvent(AppointmentEvent evt) {
    int[] from = calculateLeftTop(evt.from);
    //    int[] to = calculateLeftTop(evt.to);
    Label label = new Label("test");
    add(label, from[0], from[1]);
  }

  @Override
  public void onResize(WidgetResizeEvent event) {
    //redraw
    super.onResize(event);
  }

}
