package com.mantledillusion.vaadin.cotton;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.mantledillusion.vaadin.cotton.model.Binding;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;

/**
 * Interface for types that represent a single {@link User} of an
 * {@link CottonUI}.
 */
public interface User {

	/**
	 * A binding auditor determining {@link Binding.AccessMode}s in relation to the current {@link User}'s rights, ready
	 * to be used at {@link Binding#withRestriction(Supplier)}.
	 * <p>
	 * Instantiate using...<br>
	 * - {@link #forAnonymous(Binding.AccessMode)}<br>
	 * - {@link #forUser(Binding.AccessMode, String...)}<br>
	 */
	final class UserRightBindingAuditor implements Supplier<Binding.AccessMode> {

		private final Binding.AccessMode mode;
		private final boolean loggedIn;
		private final Set<String> rightIds;

		private UserRightBindingAuditor(Binding.AccessMode mode, boolean loggedIn, Set<String> rightIds) {
			this.mode = mode;
			this.loggedIn = loggedIn;
			this.rightIds = rightIds;
		}

		/**
		 * Returns whether the currently logged in {@link User} has sufficient rights for the {@link Binding.AccessMode}
		 * of this binding auditor.
		 *
		 * @return The {@link Binding.AccessMode} of this auditor, or {@link Binding.AccessMode#HIDDEN}, if the
		 * currently logged in {@link User} has insufficient rights, never null
		 */
		@Override
		public Binding.AccessMode get() {
			return this.loggedIn ? (WebEnv.isLoggedIn() && WebEnv.getLoggedInUser().hasRights(this.rightIds) ?
					this.mode : Binding.AccessMode.HIDDEN) : this.mode;
		}

		/**
		 * Factory method.
		 * <p>
		 * Creates a new binding auditor for the given {@link Binding.AccessMode} to be available to anonymous 
		 * visitors that are <b>not</b> logged in as a {@link User}.
		 *
		 * @param mode 		The {@link Binding.AccessMode} to allow
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor forAnonymous(Binding.AccessMode mode) {
			return new UserRightBindingAuditor(mode, false, Collections.emptySet());
		}

		/**
		 * Factory method.
		 * <p>
		 * Creates a new binding auditor for the given {@link Binding.AccessMode} to be available to a logged in 
		 * {@link User} with the given rights.
		 *
		 * @param mode 		The {@link Binding.AccessMode} to allow
		 * @param rightIds  The IDs of the rights the {@link User} has to have to be allowed having the given mode; 
		 *                  might <b>not</b> be null, empty means the {@link User} being logged in is enough.
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor forUser(Binding.AccessMode mode, String... rightIds) {
			return new UserRightBindingAuditor(mode, true, new HashSet<>(Arrays.asList(rightIds)));
		}
	}

	/**
	 * Has to return whether this {@link User} instance owns the rights of the given
	 * rightIds.
	 * <P>
	 * This method will be called whenever the {@link User} tries to navigate to a
	 * URL whose view is @{@link Restricted} to {@link User}s with certain rights.
	 * 
	 * @param rightIds The IDs of the rights this {@link User} is asked to have;
	 *                 never null, might <b>not</b> be empty.
	 * @return True if the {@link User} owns <b>all</b> of the rights behind the
	 *         given IDs, false otherwise.
	 */
	boolean hasRights(Set<String> rightIds);
}
