package com.mantledillusion.vaadin.cotton.event.responsive;

import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive.ScreenClass;

import java.util.EventObject;

/**
 * Event that is dispatched after a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
 */
public class AfterResponsiveRefreshEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public AfterResponsiveRefreshEvent(Object source) {
		super(source);
	}
}
