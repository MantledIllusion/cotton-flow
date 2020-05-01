package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasDataProviderBuilder;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.function.ValueProvider;

/**
 * {@link ComponentBuilder} for {@link TreeGrid}s.
 *
 * @param <E> The element type of the {@link TreeGrid}
 * @param <F> The filter type of the {@link TreeGrid}
 */
public class TreeGridBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends AbstractGridBuilder<TreeGrid<E>, TreeGridBuilder<E, F>, E, F> {

    private TreeGridBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static TreeGridBuilder<Object, ConfigurableFilter<Object>> create() {
        return new TreeGridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> TreeGridBuilder<E, ConfigurableFilter<E>> create(Class<E> elementType) {
        return new TreeGridBuilder<>();
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
    public static <E, F extends ConfigurableFilter<E>> TreeGridBuilder<E, F> create(Class<E> elementType,
                                                                                    Class<F> filterType) {
        return new TreeGridBuilder<>();
    }

    @Override
    protected TreeGrid<E> instantiate() {
        return new TreeGrid<>();
    }

    /**
     * Builder method, configures a new column.
     *
     * @see TreeGrid#addHierarchyColumn(ValueProvider)
     * @param valueProvider
     *            The value provider; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public GridColumnBuilder configureHierarchyColumn(ValueProvider<E, ?> valueProvider) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addHierarchyColumn(valueProvider));
        configure(columnBuilder);
        return columnBuilder;
    }
}
