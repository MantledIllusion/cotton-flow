package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasDataProviderBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasHierarchicalDataProviderBuilder;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.treegrid.CollapseEvent;
import com.vaadin.flow.component.treegrid.ExpandEvent;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.function.ValueProvider;

/**
 * {@link ComponentBuilder} for {@link TreeGrid}s.
 *
 * @param <E> The element type of the {@link TreeGrid}
 * @param <F> The filter type of the {@link TreeGrid}
 */
public class TreeGridBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends AbstractGridBuilder<TreeGrid<E>, TreeGridBuilder<E, F>, E, F>
        implements HasHierarchicalDataProviderBuilder<TreeGrid<E>, E, F, TreeGridBuilder<E, F>> {

    private TreeGridBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static TreeGridBuilder<Object, HasDataProviderBuilder.ConfigurableFilter<Object>> create() {
        return new TreeGridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> TreeGridBuilder<E, HasDataProviderBuilder.ConfigurableFilter<E>> create(Class<E> elementType) {
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
    public static <E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> TreeGridBuilder<E, F> create(Class<E> elementType,
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
     * @param <V> The column's value type.
     * @param valueProvider
     *            The value provider; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public <V> GridColumnBuilder configureHierarchyColumn(ValueProvider<E, V> valueProvider) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addHierarchyColumn(valueProvider));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a listener for {@link ExpandEvent}s.
     *
     * @see TreeGrid#addExpandListener(ComponentEventListener)
     * @param listener
     *          The listener to add; might <b>not</b> be null.
     * @return this
     */
    public TreeGridBuilder<E, F> addExpandListener(ComponentEventListener<ExpandEvent<E, TreeGrid<E>>> listener) {
        return configure(treeGrid -> treeGrid.addExpandListener(listener));
    }

    /**
     * Builder method, configures a listener for {@link CollapseEvent}s.
     *
     * @see TreeGrid#addCollapseListener(ComponentEventListener)
     * @param listener
     *          The listener to add; might <b>not</b> be null.
     * @return this
     */
    public TreeGridBuilder<E, F> addCollapseListener(ComponentEventListener<CollapseEvent<E, TreeGrid<E>>> listener) {
        return configure(treeGrid -> treeGrid.addCollapseListener(listener));
    }
}
