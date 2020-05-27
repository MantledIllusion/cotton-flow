package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.Component;

/**
 * Provider for automated login.
 * <p>
 * Is triggered for example when a @{@link com.mantledillusion.vaadin.cotton.viewpresenter.Restricted} @{@link com.vaadin.flow.router.Route} is visited.
 * <p>
 * Use one of the factory methods...<br>
 * - {@link #byView(Class)}<br>
 * - {@link #byUserProvider(UserProvider)}<br>
 * ... to for instantiation.
 */
public final class LoginProvider {

	/**
	 * Provider for {@link User} instances when automated login is triggered.
	 */
	public interface UserProvider {

		/**
		 * Provides a {@link User} instance to login.
		 * 
		 * @return A {@link User} instance; never null.
		 */
		User provide();


		boolean isSilent();
	}

	final Class<? extends Component> loginView;
	final UserProvider userProvider;

	private LoginProvider(Class<? extends Component> loginView, UserProvider userProvider) {
		this.loginView = loginView;
		this.userProvider = userProvider;
	}

	/**
	 * Factory method for creating a {@link LoginProvider} using a login view.
	 * 
	 * @param loginViewType The login view type to inject and use for login;
	 *                      might <b>not</b> be null
	 * @return A new {@link LoginProvider}, never null
	 */
	public static final LoginProvider byView(Class<? extends Component> loginViewType) {
		if (loginViewType == null) {
			throw new Http901IllegalArgumentException("Cannot create a login provider from a null login view");
		}
		return new LoginProvider(loginViewType, null);
	}

	/**
	 * Factory method for creating a {@link LoginProvider} using a
	 * {@link UserProvider}.
	 * 
	 * @param userProvider The {@link UserProvider} to use for login; might
	 *                     <b>not</b> be null
	 * @return A new {@link LoginProvider}, never null
	 */
	public static final LoginProvider byUserProvider(UserProvider userProvider) {
		if (userProvider == null) {
			throw new Http901IllegalArgumentException("Cannot create a login provider from a null user provider");
		}
		return new LoginProvider(null, userProvider);
	}
}
