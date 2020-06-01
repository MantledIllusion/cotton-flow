package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.vaadin.cotton.User;

/**
 * A configurer for {@link Binding.AccessMode}s.
 *
 * @param <ConfigurerType> The type of the configurer, allowing methods to return <code>this</code> in builder fashion.
 */
public interface AuditingConfigurer<ConfigurerType> {

    /**
     * Applies the given {@link Binding.AccessMode}.
     * <p>
     * Does not require the user to be logged in.
     * <p>
     * Note that depending on the mode set to {@link ModelBinder#setAuditMode(Binding.AuditMode)}, the
     * {@link Expression}s are evaluated differently:<br>
     * - {@link Binding.AuditMode#GENEROUS}: On the base of {@link Binding.AccessMode#READ_WRITE}, the most
     * restrictive {@link Binding.AccessMode} applying is used.<br>
     * - {@link Binding.AuditMode#RESTRICTIVE}: On the base of {@link Binding.AccessMode#HIDDEN}, the most
     * generous {@link Binding.AccessMode} applying is used.<br>
     *
     * @param mode The mode to set the auditing for; might <b>not</b> be null.
     * @return this.
     */
    default ConfigurerType setAudit(Binding.AccessMode mode) {
        return setAudit(mode, false, null);
    }

    /**
     * Applies the given {@link Binding.AccessMode}.
     * <p>
     * Note that depending on the mode set to {@link ModelBinder#setAuditMode(Binding.AuditMode)}, the
     * {@link Expression}s are evaluated differently:<br>
     * - {@link Binding.AuditMode#GENEROUS}: On the base of {@link Binding.AccessMode#READ_WRITE}, the most
     * restrictive {@link Binding.AccessMode} applying is used.<br>
     * - {@link Binding.AuditMode#RESTRICTIVE}: On the base of {@link Binding.AccessMode#HIDDEN}, the most
     * generous {@link Binding.AccessMode} applying is used.<br>
     *
     * @param mode The mode to set the auditing for; might <b>not</b> be null.
     * @param requiresLogin Determines if the given {@link Binding.AccessMode} requires a {@link User} being authenticated.
     * @return this.
     */
    default ConfigurerType setAudit(Binding.AccessMode mode, boolean requiresLogin) {
        return setAudit(mode, requiresLogin, null);
    }

    /**
     * Wraps the given right ID using {@link Expression#of(Object)} to determine if the given
     * {@link Binding.AccessMode} applies.
     * <p>
     * Requires the user to be logged in.
     * <p>
     * Note that depending on the mode set to {@link ModelBinder#setAuditMode(Binding.AuditMode)}, the
     * {@link Expression}s are evaluated differently:<br>
     * - {@link Binding.AuditMode#GENEROUS}: On the base of {@link Binding.AccessMode#READ_WRITE}, the most
     * restrictive {@link Binding.AccessMode} applying is used.<br>
     * - {@link Binding.AuditMode#RESTRICTIVE}: On the base of {@link Binding.AccessMode#HIDDEN}, the most
     * generous {@link Binding.AccessMode} applying is used.<br>
     *
     * @param mode The mode to set the auditing for; might <b>not</b> be null.
     * @param rightId The right ID the {@link User} has to own with; might be null.
     * @return this.
     */
    default ConfigurerType setAudit(Binding.AccessMode mode, String rightId) {
        return setAudit(mode, true, rightId == null ? null : Expression.of(rightId));
    }

    /**
     * Sets the given {@link Expression} to determine if the given {@link Binding.AccessMode} applies.
     * <p>
     * Requires the user to be logged in.
     * <p>
     * Note that depending on the mode set to {@link ModelBinder#setAuditMode(Binding.AuditMode)}, the
     * {@link Expression}s are evaluated differently:<br>
     * - {@link Binding.AuditMode#GENEROUS}: On the base of {@link Binding.AccessMode#READ_WRITE}, the most
     * restrictive {@link Binding.AccessMode} applying is used.<br>
     * - {@link Binding.AuditMode#RESTRICTIVE}: On the base of {@link Binding.AccessMode#HIDDEN}, the most
     * generous {@link Binding.AccessMode} applying is used.<br>
     *
     * @param mode The mode to set the auditing for; might <b>not</b> be null.
     * @param rightExpression The {@link Expression}s to evaluate with; might be null.
     * @return this.
     */
    default ConfigurerType setAudit(Binding.AccessMode mode, Expression<String> rightExpression) {
        return setAudit(mode, true, rightExpression);
    }

    /**
     * Sets the given {@link Expression} to determine if the given {@link Binding.AccessMode} applies.
     * <p>
     * Note that depending on the mode set to {@link ModelBinder#setAuditMode(Binding.AuditMode)}, the
     * {@link Expression}s are evaluated differently:<br>
     * - {@link Binding.AuditMode#GENEROUS}: On the base of {@link Binding.AccessMode#READ_WRITE}, the most
     * restrictive {@link Binding.AccessMode} applying is used.<br>
     * - {@link Binding.AuditMode#RESTRICTIVE}: On the base of {@link Binding.AccessMode#HIDDEN}, the most
     * generous {@link Binding.AccessMode} applying is used.<br>
     *
     * @param mode The mode to set the auditing for; might <b>not</b> be null.
     * @param requiresLogin Determines if the given {@link Binding.AccessMode} requires a {@link User} being
     *                      authenticated; note that if set to <code>false</code>, any given right expression is ignored.
     * @param rightExpression The {@link Expression}s to evaluate with; might be null.
     * @return this.
     */
    ConfigurerType setAudit(Binding.AccessMode mode, boolean requiresLogin, Expression<String> rightExpression);
}
