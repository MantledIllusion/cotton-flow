package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.mantledillusion.vaadin.cotton.exception.http900.Http902IllegalStateException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.SortOrderProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link ComponentBuilder} for {@link Grid}s.
 *
 * @param <E> The element type of the {@link Grid}
 * @param <F> The filter type of the {@link Grid}
 */
public class GridBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends
        AbstractComponentBuilder<Grid<E>, GridBuilder<E, F>> implements
        HasSizeBuilder<Grid<E>, GridBuilder<E, F>>,
        HasThemeVariantBuilder<Grid<E>, GridBuilder<E, F>, GridVariant>,
        HasStyleBuilder<Grid<E>, GridBuilder<E, F>>,
        FocusableBuilder<Grid<E>, GridBuilder<E, F>>,
        HasEnabledBuilder<Grid<E>, GridBuilder<E, F>>,
        HasItemsBuilder<Grid<E>, E, GridBuilder<E, F>>,
        HasDataProviderBuilder<Grid<E>, E, F, GridBuilder<E, F>> {

    /**
     * {@link EntityBuilder} for {@link Grid.Column}s.
     */
    public final class GridColumnBuilder extends AbstractEntityBuilder<Grid.Column<E>, GridColumnBuilder> implements
            Configurer<Grid<E>> {

        private final Function<Grid<E>, Grid.Column<E>> columnSupplier;

        private GridColumnBuilder(Function<Grid<E>, Grid.Column<E>> columnSupplier) {
            super(GridBuilder.this);
            this.columnSupplier = columnSupplier;
        }

        @Override
        public void configure(Grid<E> component) {
            apply(this.columnSupplier.apply(component));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s key.
         *
         * @see Grid.Column#setKey(String)
         * @param key The key; might <b>not</b> be null.
         * @return this
         */
        public GridColumnBuilder setKey(String key) {
            return configure(column -> column.setKey(key));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s alignment.
         *
         * @see Grid.Column#setTextAlign(ColumnTextAlign)
         * @param textAlign The alignment; might be null.
         * @return this
         */
        public GridColumnBuilder setTextAlign(ColumnTextAlign textAlign) {
            return configure(column -> column.setTextAlign(textAlign));
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValue The @link HasValue}; might <b>not</b> be null.
         * @param filterProperty A {@link Property} to adopt a change to the {@link HasValue}'s value into the
         *                       {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(C hasValue,
                                                                                     Property<F, V> filterProperty) {
            return setFilter((Supplier<C>) () -> hasValue, filterProperty::set);
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValueBuilder A {@link ComponentBuilder} for {@link HasValue}s; might <b>not</b> be null.
         * @param filterProperty A {@link Property} to adopt a change to the {@link HasValue}'s value into the
         *                       {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(ComponentBuilder<C ,?> hasValueBuilder,
                                                                                     Property<F, V> filterProperty) {
            return setFilter((Supplier<C>) hasValueBuilder::build, filterProperty::set);
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValueSupplier A supplier for {@link HasValue}s; might <b>not</b> be null.
         * @param filterProperty A {@link Property} to adopt a change to the {@link HasValue}'s value into the
         *                       {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(Supplier<C> hasValueSupplier,
                                                                                     Property<F, V> filterProperty) {
            return setFilter(hasValueSupplier, filterProperty::set);
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValue The @link HasValue}; might <b>not</b> be null.
         * @param filterChangeConsumer A {@link BiConsumer} to adopt a change to the {@link HasValue}'s value into the
         *                             {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(C hasValue,
                                                                                     BiConsumer<F, V> filterChangeConsumer) {
            return setFilter((Supplier<C>) () -> hasValue, filterChangeConsumer);
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValueBuilder A {@link ComponentBuilder} for {@link HasValue}s; might <b>not</b> be null.
         * @param filterChangeConsumer A {@link BiConsumer} to adopt a change to the {@link HasValue}'s value into the
         *                             {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(ComponentBuilder<C ,?> hasValueBuilder,
                                                                                     BiConsumer<F, V> filterChangeConsumer) {
            return setFilter((Supplier<C>) hasValueBuilder::build, filterChangeConsumer);
        }

        /**
         * Builder method, configures a {@link HasValue} as header to the column whose value is automatically bound
         * to the {@link Grid}'s filter.
         *
         * @param <C> The {@link HasValue}'s {@link Component} type.
         * @param <V> The {@link HasValue}'s value type.
         * @param hasValueSupplier A supplier for {@link HasValue}s; might <b>not</b> be null.
         * @param filterChangeConsumer A {@link BiConsumer} to adopt a change to the {@link HasValue}'s value into the
         *                             {@link Grid}'s filter; might <b>not</b> be null.
         * @return this
         */
        public <C extends Component & HasValue<?, V>, V> GridColumnBuilder setFilter(Supplier<C> hasValueSupplier,
                                                                                     BiConsumer<F, V> filterChangeConsumer) {
            return configure(column -> {
                if (!contains(ConfigurableFilter.class)) {
                    throw new Http902IllegalStateException("Cannot configure a filter column without a data provider " +
                            "with filter being configured.");
                }
                F filter = (F) get(ConfigurableFilter.class);
                C hasValue = hasValueSupplier.get();
                hasValue.addValueChangeListener(event -> {
                    filterChangeConsumer.accept(filter, event.getValue());
                });
                column.setHeader(hasValue);
            });
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s header.
         *
         * @see Grid.Column#setHeader(String)
         * @param msgId The header, or a message id to localize; might be null.
         * @return this
         */
        public GridColumnBuilder setHeader(String msgId) {
            return configure(column -> column.setHeader(WebEnv.getTranslation(msgId)));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s header.
         *
         * @see Grid.Column#setHeader(Component)
         * @param component The header component; might be null.
         * @return this
         */
        public GridColumnBuilder setHeader(Component component) {
            return configure(column -> column.setHeader(component));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s footer.
         *
         * @see Grid.Column#setFooter(String)
         * @param msgId The footer, or a message id to localize; might be null.
         * @return this
         */
        public GridColumnBuilder setFooter(String msgId) {
            return configure(column -> column.setFooter(WebEnv.getTranslation(msgId)));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s footer.
         *
         * @see Grid.Column#setFooter(Component)
         * @param component The footer component; might be null.
         * @return this
         */
        public GridColumnBuilder setFooter(Component component) {
            return configure(column -> column.setFooter(component));
        }

        /**
         * Builder method, configures the {@link Grid.Column} to be visible.
         *
         * @see Grid.Column#setVisible(boolean)
         * @param visible True if the column should be visible, false otherwise.
         * @return this
         */
        public GridColumnBuilder setVisible(boolean visible) {
            return configure(column -> column.setVisible(visible));
        }

        /**
         * Builder method, configures the {@link Grid.Column} to be frozen.
         *
         * @see Grid.Column#setFrozen(boolean)
         * @param frozen True if the column should be frozen, false otherwise.
         * @return this
         */
        public GridColumnBuilder setFrozen(boolean frozen) {
            return configure(column -> column.setFrozen(frozen));
        }

        /**
         * Builder method, configures the {@link Grid.Column} to be resizable.
         *
         * @see Grid.Column#setResizable(boolean)
         * @param resizable True if the column should be resizable, false otherwise.
         * @return this
         */
        public GridColumnBuilder setResizable(boolean resizable) {
            return configure(column -> column.setResizable(resizable));
        }

        /**
         * Builder method, configures the {@link Grid.Column} to grow flexibly.
         *
         * @see Grid.Column#setFlexGrow(int)
         * @param flexGrow The flex grow ratio.
         * @return this
         */
        public GridColumnBuilder setFlexGrow(int flexGrow) {
            return configure(column -> column.setFlexGrow(flexGrow));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s width to be set undefined.
         *
         * @see Grid.Column#setWidth(String)
         * @return this
         */
        public GridColumnBuilder setWidthUndefined() {
            return configure(hasSize -> hasSize.setWidth(null));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s width to be set to an exact pixel value.
         *
         * @see Grid.Column#setWidth(String)
         * @param px
         *            The pixel width to set
         * @return this
         */
        public GridColumnBuilder setExactWidth(int px) {
            return configure(hasSize -> hasSize.setWidth(px + CSS_PX));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s width to be set to a percental share of its parent's width.
         *
         * @see Grid.Column#setWidth(String)
         * @param pct
         *            The percental width to set
         * @return this
         */
        public GridColumnBuilder setPercentalWidth(int pct) {
            return configure(hasSize -> hasSize.setWidth(pct + CSS_PCT));
        }

        /**
         * Builder method, configures the {@link Grid.Column}'s width to be set to 100% percental share of its parent's width.
         *
         * @see Grid.Column#setWidth(String)
         * @return this
         */
        public GridColumnBuilder setWidthFull() {
            return configure(hasSize -> hasSize.setWidth(100 + CSS_PCT));
        }

        /**
         * Builder method, configures a CSS class name generator for the {@link Grid.Column}.
         *
         * @see Grid.Column#setClassNameGenerator(SerializableFunction)
         * @param classNameGenerator The generator; might <b>not</b> be null.
         * @return this
         */
        public GridColumnBuilder setClassNameGenerator(SerializableFunction<E, String> classNameGenerator) {
            return configure(column -> column.setClassNameGenerator(classNameGenerator));
        }

        /**
         * Builder method, configures an element comparator for the {@link Grid.Column}.
         *
         * @see Grid.Column#setComparator(Comparator)
         * @param comparator The comparator; might <b>not</b> be null.
         * @return this
         */
        public GridColumnBuilder setComparator(Comparator<E> comparator) {
            return configure(column -> column.setComparator(comparator));
        }

        /**
         * Builder method, configures an element comparator for the {@link Grid.Column}.
         *
         * @see Grid.Column#setComparator(ValueProvider)
         * @param <V> The value type of the column.
         * @param keyExtractor The comparator; might <b>not</b> be null.
         * @return this
         */
        public <V extends Comparable<? super V>> GridColumnBuilder setComparator(ValueProvider<E, V> keyExtractor) {
            return configure(column -> column.setComparator(keyExtractor));
        }

        /**
         * Builder method, configures the {@link Grid.Column} to be sortable.
         *
         * @see Grid.Column#setSortable(boolean)
         * @param sortable True if the column should be sortable, false otherwise.
         * @return this
         */
        public GridColumnBuilder setSortable(boolean sortable) {
            return configure(column -> column.setSortable(sortable));
        }

        /**
         * Builder method, configures the properties to sort the {@link Grid.Column} with.
         *
         * @see Grid.Column#setSortProperty(String[])
         * @param properties The properties to sort the column by; might be null.
         * @return this
         */
        public GridColumnBuilder setSortProperty(String... properties) {
            return configure(column -> column.setSortProperty(properties));
        }

        /**
         * Builder method, configures the provider to sort the {@link Grid.Column} with.
         *
         * @see Grid.Column#setSortOrderProvider(SortOrderProvider)
         * @param provider The provider to sort the column by; might be null.
         * @return this
         */
        public GridColumnBuilder setSortOrderProvider(SortOrderProvider provider) {
            return configure(column -> column.setSortOrderProvider(provider));
        }

        /**
         * Builder method, configures an editor component for the {@link Grid.Column}.
         *
         * @see Grid.Column#setEditorComponent(Component)
         * @param component The component; might be null.
         * @return this
         */
        public GridColumnBuilder setEditorComponent(Component component) {
            return configure(column -> column.setEditorComponent(component));
        }

        /**
         * Builder method, configures a callback for generating editor components for the {@link Grid.Column}.
         *
         * @see Grid.Column#setEditorComponent(SerializableFunction)
         * @param componentCallback The callback; might be null.
         * @return this
         */
        public GridColumnBuilder setEditorComponent(SerializableFunction<E, ? extends Component> componentCallback) {
            return configure(column -> column.setEditorComponent(componentCallback));
        }

        /**
         * Adds the currently configured column to the {@link Grid} being build by the returned {@link GridBuilder}.
         *
         * @return The {@link GridBuilder} that started this {@link GridColumnBuilder}, never null
         */
        public GridBuilder<E, F> add() {
            return GridBuilder.this;
        }
    }

    private GridBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static GridBuilder<Object, ConfigurableFilter<Object>> create() {
        return new GridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> GridBuilder<E, ConfigurableFilter<E>> create(Class<E> elementType) {
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
    public static <E, F extends ConfigurableFilter<E>> GridBuilder<E, F> create(Class<E> elementType,
                                                                                Class<F> filterType) {
        return new GridBuilder<>();
    }

    @Override
    protected Grid<E> instantiate() {
        return new Grid<>();
    }

    @Override
    public String toVariantName(GridVariant variant) {
        return variant.getVariantName();
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(ValueProvider, String...)
     * @param <V> The value type of the column.
     * @param property
     *            The {@link Property}; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public <V extends Comparable<? super V>> GridColumnBuilder configureColumn(Property<E, V> property) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(property::get, property.getId()));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(String)
     * @param propertyName
     *            The name of the property; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public GridColumnBuilder configureColumn(String propertyName) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(propertyName));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(Renderer)
     * @param renderer
     *            The renderer; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public GridColumnBuilder configureColumn(Renderer<E> renderer) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(renderer));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(Renderer, String[])
     * @param renderer
     *            The renderer; might <b>not</b> be null.
     * @param sortingProperties
     *            The properties to sort by, might be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public GridColumnBuilder configureColumn(Renderer<E> renderer, String... sortingProperties) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(renderer, sortingProperties));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(ValueProvider)
     * @param valueProvider
     *            The value provider; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public GridColumnBuilder configureColumn(ValueProvider<E, ?> valueProvider) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(valueProvider));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(ValueProvider)
     * @param <V> The value type of the column.
     * @param valueProvider
     *            The value provider; might <b>not</b> be null.
     * @param sortingProperties
     *            The properties to sort by, might be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public <V extends Comparable<? super V>> GridColumnBuilder configureColumn(ValueProvider<E, V> valueProvider, String... sortingProperties) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addColumn(valueProvider, sortingProperties));
        configure(columnBuilder);
        return columnBuilder;
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(ValueProvider)
     * @param <V> The value type of the column.
     * @param valueProvider
     *            The component provider; might <b>not</b> be null.
     * @return A new {@link GridColumnBuilder}, never null
     */
    public <V extends Component> GridColumnBuilder configureComponentColumn(ValueProvider<E, V> valueProvider) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addComponentColumn(valueProvider));
        configure(columnBuilder);
        return columnBuilder;
    }
}
