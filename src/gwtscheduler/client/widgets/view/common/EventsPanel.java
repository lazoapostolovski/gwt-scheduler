package gwtscheduler.client.widgets.view.common;

import gwtscheduler.client.interfaces.uievents.lasso.AbstractLassoEvent;
import gwtscheduler.client.interfaces.uievents.lasso.LassoCancelSelectionEvent;
import gwtscheduler.client.interfaces.uievents.lasso.LassoEndSelectionEvent;
import gwtscheduler.client.interfaces.uievents.lasso.LassoEventHandler;
import gwtscheduler.client.interfaces.uievents.lasso.LassoStartSelectionEvent;
import gwtscheduler.client.interfaces.uievents.lasso.LassoUpdateSelectionEvent;
import gwtscheduler.client.interfaces.uievents.resize.WidgetResizeEvent;
import gwtscheduler.client.interfaces.uievents.resize.WidgetResizeHandler;
import gwtscheduler.client.modules.AppInjector;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * @author malp
 */
public class EventsPanel extends AbsolutePanel implements WidgetResizeHandler, LassoEventHandler {

  /**
   * Default constructor.
   */
  public EventsPanel() {
    AppInjector.GIN.getInjector().getEventBus().addHandler(AbstractLassoEvent.getType(), this);
    getElement().getStyle().setPosition(Position.ABSOLUTE);
  }

  @Override
  public void onCancelSelection(LassoCancelSelectionEvent event) {
  }

  @Override
  public void onEndSelection(LassoEndSelectionEvent event) {
  }

  @Override
  public void onStartSelection(LassoStartSelectionEvent event) {
  }

  @Override
  public void onUpdateSelection(LassoUpdateSelectionEvent event) {
  }

  @Override
  public void onResize(WidgetResizeEvent event) {
  }

}
