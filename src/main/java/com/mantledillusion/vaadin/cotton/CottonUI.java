package com.mantledillusion.vaadin.cotton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Bus;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.vaadin.cotton.event.responsive.AfterResponsiveRefreshEvent;
import com.mantledillusion.vaadin.cotton.event.responsive.BeforeResponsiveRefreshEvent;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http900NoSessionContextException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http903NotImplementedException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive.ScreenClass;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.internal.UIInternals;
import com.vaadin.flow.shared.Registration;

/**
 * {@link UI} extension that is used by Cotton.
 */
public final class CottonUI extends UI {

	private static final long serialVersionUID = 1L;

	private static final Method REGISTER_LISTENER;

	static {
		try {
			REGISTER_LISTENER = UIInternals.class.getDeclaredMethod("addListener", Class.class, Object.class);
			REGISTER_LISTENER.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new Http903NotImplementedException("The method addNavigationListener() in "
					+ UIInternals.class.getSimpleName() + " is not available.");
		}
	}

	/**
	 * A listener that may be added to the {@link CottonUI}
	 * using
	 * {@link CottonUI#addAfterLoginListener(AfterLoginListener)}.
	 * <p>
	 * Will be notified after a new {@link User} logs in.
	 */
	@FunctionalInterface
	public interface AfterLoginListener {

		/**
		 * Is called after a {@link User} logs in.
		 * 
		 * @param event The dispatched {@link AfterLoginEvent}; might <b>not</b> be null.
		 */
		void afterLogin(AfterLoginEvent event);
	}

	/**
	 * A listener that may be added to the {@link CottonUI}
	 * using
	 * {@link CottonUI#addBeforeLogoutListener(BeforeLogoutListener)}.
	 * <p>
	 * Will be notified after a new {@link User} logs in.
	 */
	@FunctionalInterface
	public interface BeforeLogoutListener {

		/**
		 * Is called before a {@link User} logs out.
		 * 
		 * @param event The dispatched {@link BeforeLogoutEvent}; might <b>not</b> be null.
		 */
		void beforeLogout(BeforeLogoutEvent event);
	}

	/**
	 * A listener that may be added to the {@link CottonUI} using
	 * {@link CottonUI#addAfterResponsiveRefreshListener(AfterResponsiveRefreshListener)}.
	 * <p>
	 * Will be notified after a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
	 */
	@FunctionalInterface
	public interface AfterResponsiveRefreshListener {

		/**
		 * Is called after a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
		 *
		 * @param event The dispatched {@link AfterResponsiveRefreshEvent}; might <b>not</b> be null.
		 */
		void afterRefresh(AfterResponsiveRefreshEvent event);
	}

	/**
	 * A listener that may be added to the {@link CottonUI} using
	 * {@link CottonUI#addBeforeResponsiveRefreshListener(BeforeResponsiveRefreshListener)}.
	 * <p>
	 * Will be notified before a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
	 */
	@FunctionalInterface
	public interface BeforeResponsiveRefreshListener {

		/**
		 * Is called before a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
		 *
		 * @param event The dispatched {@link BeforeResponsiveRefreshEvent}; might <b>not</b> be null.
		 */
		void beforeRefresh(BeforeResponsiveRefreshEvent event);
	}

	private Injector uiInjector;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		this.uiInjector = ((CottonSession) getInternals().getSession()).createInSessionContext(Injector.class);
	}

	<T extends HasElement> T exchangeInjectedView(Class<T> type) {
		this.uiInjector.destroyAll();
		return this.uiInjector.instantiate(type,
				Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.FALSE.toString()));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		((CottonSession) getInternals().getSession()).destroyInSessionContext(this.uiInjector);
	}

	/**
	 * Adds a listener that will be notified after a new {@link User} has logged in.
	 * 
	 * @param listener
	 *            The listener to add; might <b>not</b> be null.
	 * @return The handler to remove the event listener with, never null
	 */
	public Registration addAfterLoginListener(AfterLoginListener listener) {
		return register(AfterLoginListener.class, listener);
	}

	/**
	 * Adds a listener that will be notified before a new {@link User} will log in.
	 * 
	 * @param listener
	 *            The listener to add; might <b>not</b> be null.
	 * @return The handler to remove the event listener with, never null
	 */
	public Registration addBeforeLogoutListener(BeforeLogoutListener listener) {
		return register(BeforeLogoutListener.class, listener);
	}

	/**
	 * Adds a listener that will be notified after a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
	 *
	 * @param listener
	 *            The listener to add; might <b>not</b> be null.
	 * @return The handler to remove the event listener with, never null
	 */
	public Registration addAfterResponsiveRefreshListener(AfterResponsiveRefreshListener listener) {
		return register(AfterResponsiveRefreshListener.class, listener);
	}

	/**
	 * Adds a listener that will be notified before a @{@link Responsive} view has exchanged its @{@link ScreenClass}.
	 *
	 * @param listener
	 *            The listener to add; might <b>not</b> be null.
	 * @return The handler to remove the event listener with, never null
	 */
	public Registration addBeforeResponsiveRefreshListener(BeforeResponsiveRefreshListener listener) {
		return register(BeforeResponsiveRefreshListener.class, listener);
	}

	private <E> Registration register(Class<E> listenerType, E listener) {
		if (listener == null) {
			throw new Http901IllegalArgumentException("Cannot register a null " + listenerType.getSimpleName());
		}
		try {
			return (Registration) REGISTER_LISTENER.invoke(getInternals(), listenerType, listener);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Http500InternalServerErrorException("Failed to register " + listenerType.getSimpleName(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> List<E> getNavigationListeners(Class<E> navigationHandler) {
		if (navigationHandler.isAssignableFrom(AccessHandler.class)) {
			List<E> listeners = new ArrayList<>();
			listeners.add((E) CottonSession.current().getAccessHandler());
			listeners.addAll(super.getNavigationListeners(navigationHandler));
			return Collections.unmodifiableList(listeners);
		} else {
			return super.getNavigationListeners(navigationHandler);
		}
	}

	public static CottonUI current() {
		if (getCurrent() == null) {
			throw new Http900NoSessionContextException();
		}
		return (CottonUI) getCurrent();
	}
}
