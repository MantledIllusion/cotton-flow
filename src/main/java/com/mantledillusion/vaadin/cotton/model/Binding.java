package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.data.epiphy.context.Context;

import java.util.function.Supplier;

/**
 * Represents the configurable binding of a property.
 */
public abstract class Binding {

    private static final Supplier<AccessMode> DEFAULT_AUDITOR = () -> AccessMode.READ_WRITE;

    /**
     * Represents the modes a {@link Binding} can allow access of a properties data to.
     */
    public enum AccessMode {

        /**
         * Full access, reading and writing.
         */
        READ_WRITE,

        /**
         * Reading access only.
         */
        READ_ONLY,

        /**
         * No access at all.
         */
        PROHIBIT;

        private AccessMode or(AccessMode other) {
            return ordinal() < other.ordinal() ? this : other;
        }
    }

    private Supplier<AccessMode> bindingAuditor = DEFAULT_AUDITOR;
    private AccessMode accessMode;

    Binding() {}

    protected final AccessMode getAccessMode() {
        return this.accessMode;
    }

    protected final void refreshAccessMode() {
        this.accessMode = this.bindingAuditor.get();
        accessModeChanged(this.accessMode != AccessMode.PROHIBIT);
    }

    /**
     * Builder method.
     * <p>
     * Adds the given binding auditor to the binding to restrict it.
     * <p>
     * The given {@link Supplier}'s result will be used to determine at which {@link AccessMode} this binding is
     * expected to allow access to the data of its bound property.
     * <p>
     * When this method is never used so no binding auditor is ever specified, a default auditor will allow general
     * {@link AccessMode#READ_WRITE} access to the property.
     * <p>
     * When multiple binding auditors are specified using this method, the most generous {@link AccessMode} determined
     * by the auditors will be used.
     *
     * @param bindingAuditor The binding auditor; might <b>not</b> be null.
     * @return this
     */
    public final Binding withRestriction(Supplier<AccessMode> bindingAuditor) {
        if (this.bindingAuditor == DEFAULT_AUDITOR) {
            this.bindingAuditor = bindingAuditor;
        } else {
            Supplier<AccessMode> current = this.bindingAuditor;
            this.bindingAuditor = () -> current.get().or(bindingAuditor.get());
        }
        refreshAccessMode();
        return this;
    }

    void accessModeChanged(boolean couple) {};

    abstract void valueChanged(Context context, ModelBinder.UpdateType type);
}
