package gwtscheduler.client.widgets.view.week;

import gwtscheduler.client.widgets.view.common.AbstractCompositeDayView;
import gwtscheduler.client.widgets.view.common.AbstractDayView;

/**
 *
 */
public class CompositeWeekView extends AbstractCompositeDayView {

	@Override
	protected AbstractDayView createDayView() {
		return new WeekView();
	}

}