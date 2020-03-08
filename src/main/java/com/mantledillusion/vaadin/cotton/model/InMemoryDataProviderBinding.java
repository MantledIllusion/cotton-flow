package com.mantledillusion.vaadin.cotton.model;

import com.vaadin.flow.data.provider.InMemoryDataProvider;

import java.util.function.Supplier;

/**
 * A specific type of {@link Binding} to a {@link InMemoryDataProvider}.
 *
 * @param <ElementType> The element type of the {@link InMemoryDataProvider}.
 */
public abstract class InMemoryDataProviderBinding<ElementType> extends Binding<ElementType> {

    private final InMemoryDataProvider<ElementType> dataProvider;

    InMemoryDataProviderBinding(Supplier<Binding.AccessMode> bindingAuditor,
                                InMemoryDataProvider<ElementType> dataProvider) {
        super(bindingAuditor);
        this.dataProvider = dataProvider;
    }

    /**
     * Returns the bound {@link InMemoryDataProvider}.
     *
     * @return The {@link InMemoryDataProvider}, never null
     */
    public InMemoryDataProvider<ElementType> getDataProvider() {
        return dataProvider;
    }
}
