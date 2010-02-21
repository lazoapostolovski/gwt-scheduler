package gwtscheduler.client.widgets.view.common;

import static gwtscheduler.client.utils.Constants.LASSO_ZINDEX;
import static gwtscheduler.client.utils.Constants.LASSO_ZINDEX_SELECTING;
import gwtscheduler.client.modules.AppInjector;
import gwtscheduler.client.resources.Resources;
import gwtscheduler.client.widgets.common.LassoStrategy;
import gwtscheduler.client.widgets.common.ComplexGrid;
import gwtscheduler.client.widgets.common.event.AbstractLassoEvent;
import gwtscheduler.client.widgets.common.event.GenericLassoElementFactory;
import gwtscheduler.client.widgets.common.event.HasLassoHandlers;
import gwtscheduler.client.widgets.common.event.LassoEndSelectionEvent;
import gwtscheduler.client.widgets.common.event.LassoEventHandler;
import gwtscheduler.client.widgets.common.event.LassoStartSelectionEvent;
import gwtscheduler.client.widgets.common.event.LassoUpdateSelectionEvent;
import gwtscheduler.client.widgets.common.event.WidgetResizeEvent;
import gwtscheduler.client.widgets.common.event.WidgetResizeHandler;

import java.util.List;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Lasso panel. Responsible for displaying user lasso selections.
 * @author malp
 */
class LassoPanel extends AbsolutePanel implements HasLassoHandlers, MouseDownHandler, MouseMoveHandler, MouseUpHandler, WidgetResizeHandler {

  /** the lasso subject grid */
  private ComplexGrid subject;
  /** indicates if a lasso is being selected or not */
  private boolean isMouseDown = false;
  /** the lasso starting position */
  private int[] startPos;
  /** the lasso strategy */
  private LassoStrategy strategy;

  /** lasso container */
  private AbsolutePanel lassoPanel;

  /** elements factory */
  private LassoElementFactory lassoFactory;
  /** event bus */
  private EventBus evtBus;

  /**
   * Default constructor.
   * @param strat
   */
  LassoPanel() {
    addStyleName(Resources.dayWeekCss().lassoPanel());

    lassoFactory = new GenericLassoElementFactory();
    evtBus = AppInjector.GIN.getInjector().getEventBus();

    // lasso/ events set up
    lassoPanel = new LassoContainer();

    addDomHandler(this, MouseDownEvent.getType());
    addDomHandler(this, MouseUpEvent.getType());
    addDomHandler(this, MouseMoveEvent.getType());

    add(lassoPanel, 0, 0);
  }

  /**
   * Sets the current lasso strategy.
   * @param strat the strategy
   */
  public void setStrategy(LassoStrategy strat) {
    strategy = strat;
  }

  /**
   * Selects a range of lasso cells.
   * @param p1 the first position
   * @param p2 the second position
   */
  private void selectRange(int[] p1, int[] p2) {
    List<int[]> range = strategy.getBlocks(subject, p1, p2);
    assert range.size() >= 2 : "Event blocks are less than 2.";
    assert range.size() % 2 == 0 : "Odd number of events";

    for (int i = 0; i < range.size(); i += 2) {
      int[] from = range.get(i);
      int[] to = range.get(i + 1);
      Widget lasso = lassoFactory.createLassoElement(subject, from, to);

      int[] coords = calculateLeftTop(from);
      lassoPanel.add(lasso, coords[0], coords[1]);
    }
  }

  /**
   * Sets the lasso subject.
   * @param subject the subject
   */
  void setLassoSubject(ComplexGrid subject) {
    assert subject != null : "Lasso subject cannot be null.";
    this.subject = subject;
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    if (isMouseDown) {
      return;
    }
    // TODO verify if the lasso panel was the src
    isMouseDown = true;
    lassoPanel.clear();
    Element lassoEl = lassoPanel.getElement();
    DOM.setIntStyleAttribute(lassoEl, "zIndex", LASSO_ZINDEX_SELECTING);

    startPos = calculateCellPosition(event);
    selectRange(startPos, startPos);
    evtBus.fireEvent(new LassoStartSelectionEvent(subject, startPos));
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    if (!isMouseDown) {
      return;
    }
    int[] pos = calculateCellPosition(event);
    // TODO this is not very efficient
    // in the future we must take account if we need to clear or not
    // right now we're clearing because one could start selecting in one dir
    // and then move to opposite dir, so we need to make sure that previous
    // selected
    // cells are deselected
    lassoPanel.clear();
    selectRange(startPos, pos);
    evtBus.fireEvent(new LassoUpdateSelectionEvent(subject, pos));
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
    lassoPanel.clear();
    DOM.setIntStyleAttribute(lassoPanel.getElement(), "zIndex", LASSO_ZINDEX);

    isMouseDown = false;
    // show events dialog
    int[] endPos = calculateCellPosition(event);
    evtBus.fireEvent(new LassoEndSelectionEvent(subject, startPos, endPos));
  }

  /**
   * Calculates the cell position for a given mouse event
   * @param event the mouse event
   * @return the cell position
   */
  private int[] calculateCellPosition(MouseEvent<?> event) {
    int x = event.getRelativeX(getElement());
    int y = event.getRelativeY(getElement());

    int rowPos = (y / (subject.getHeight() / subject.getRowNum()));
    int colPos = (x / (subject.getWidth() / subject.getColNum()));
    return new int[] {rowPos, colPos};
  }

  /**
   * Calculates the top and left coordinates for a given cell position.
   * @param cellPos the cell position
   * @return
   */
  private int[] calculateLeftTop(int[] cellPos) {
    assert cellPos != null : "Cell position cannot be null";
    assert cellPos.length == 2 : "Position length != 2";

    int rowH = Math.round((float) subject.getHeight() / subject.getRowNum());
    int colW = Math.round((float) subject.getWidth() / subject.getColNum());
    return new int[] {cellPos[1] * colW, cellPos[0] * rowH};
  }

  @Override
  public void onResize(WidgetResizeEvent event) {
    lassoPanel.clear();
    DOM.setIntStyleAttribute(lassoPanel.getElement(), "zIndex", LASSO_ZINDEX);
    // reposition events
  }

  /**
   * Utility class to facilitate event registration.
   * @author malp
   */
  private static class LassoContainer extends AbsolutePanel implements HasMouseDownHandlers, HasMouseMoveHandlers, HasMouseUpHandlers {

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
      return addDomHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
      return addDomHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
      return addDomHandler(handler, MouseMoveEvent.getType());
    }
  }

  @Override
  public HandlerRegistration addLassoHandler(LassoEventHandler handler) {
    return addHandler(handler, AbstractLassoEvent.getType());
  }

}