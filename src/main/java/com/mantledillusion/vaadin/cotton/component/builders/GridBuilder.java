package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasDataProviderBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasSimpleDataProviderBuilder;
import com.vaadin.flow.component.grid.*;

/**
 * {@link ComponentBuilder} for {@link Grid}s.
 *
 * @param <E> The element type of the {@link Grid}
 * @param <F> The filter type of the {@link Grid}
 */
public class GridBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends AbstractGridBuilder<Grid<E>, GridBuilder<E, F>, E, F>
        implements HasSimpleDataProviderBuilder<Grid<E>, E, F, GridBuilder<E, F>> {

    private GridBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static GridBuilder<Object, HasDataProviderBuilder.ConfigurableFilter<Object>> create() {
        return new GridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> GridBuilder<E, HasDataProviderBuilder.ConfigurableFilter<E>> create(Class<E> elementType) {
        return new GridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param <F> The filter type.
     * @param elementType The class type of the element; might be null.
     * @param filterType The class type of the filter; might be null.
     * @return A new instance, never null.
     */
    public static <E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> GridBuilder<E, F> create(Class<E> elementType,
                                                                                                       Class<F> filterType) {
        return new GridBuilder<>();
    }

    @Override
    protected Grid<E> instantiate() {
        return new Grid<>();
    }
}
