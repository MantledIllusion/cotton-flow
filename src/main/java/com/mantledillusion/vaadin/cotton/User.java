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
	 * - {@link #readWrite(String...)}<br>
	 * - {@link #readOnly(String...)}<br>
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
		 * @return The {@link Binding.AccessMode} of this auditor, or {@link Binding.AccessMode#PROHIBIT}, if the
		 * currently logged in {@link User} has insufficient rights, never null
		 */
		@Override
		public Binding.AccessMode get() {
			return this.loggedIn ? (WebEnv.isLoggedIn() && WebEnv.getLoggedInUser().hasRights(this.rightIds) ?
					this.mode : Binding.AccessMode.PROHIBIT) : this.mode;
		}

		/**
		 * Factory method.
		 *
		 * Creates a new binding auditor for the {@link Binding.AccessMode#READ_WRITE} to be available to {@link User}s
		 * with the given rights.
		 *
		 * @param rightIds  The IDs of the rights the {@link User} has to have to be allowed
		 *                  {@link Binding.AccessMode#READ_WRITE}; might <b>not</b> be null, empty means the {@link User}
		 *                  being logged in is enough.
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor readWrite(String... rightIds) {
			return new UserRightBindingAuditor(Binding.AccessMode.READ_WRITE, true, new HashSet<>(Arrays.asList(rightIds)));
		}

		/**
		 * Factory method.
		 *
		 * Creates a new binding auditor for the {@link Binding.AccessMode#READ_WRITE} to be available to anonymous
		 * visitors that are <b>not</b> logged in as a {@link User}.
		 *
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor anonymousReadWrite() {
			return new UserRightBindingAuditor(Binding.AccessMode.READ_WRITE, false, Collections.emptySet());
		}

		/**
		 * Factory method.
		 *
		 * Creates a new binding auditor for the {@link Binding.AccessMode#READ_ONLY} to be available to {@link User}s
		 * with the given rights.
		 *
		 * @param rightIds  The IDs of the rights the {@link User} has to have to be allowed
		 *                  {@link Binding.AccessMode#READ_ONLY}; might <b>not</b> be null, empty means the {@link User}
		 *                  being logged in is enough.
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor readOnly(String... rightIds) {
			return new UserRightBindingAuditor(Binding.AccessMode.READ_ONLY, true, new HashSet<>(Arrays.asList(rightIds)));
		}

		/**
		 * Factory method.
		 *
		 * Creates a new binding auditor for the {@link Binding.AccessMode#READ_ONLY} to be available to anonymous
		 * visitors that are <b>not</b> logged in as a {@link User}.
		 *
		 * @return A new {@link UserRightBindingAuditor} instance, never null
		 */
		public static UserRightBindingAuditor anonymousReadOnly() {
			return new UserRightBindingAuditor(Binding.AccessMode.READ_ONLY, false, Collections.emptySet());
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
