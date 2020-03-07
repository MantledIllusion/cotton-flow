package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.SortOrderProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

import java.util.Comparator;
import java.util.function.Function;

/**
 * {@link ComponentBuilder} for {@link Grid}s.
 */
public class GridBuilder<E> extends AbstractComponentBuilder<Grid<E>, GridBuilder<E>>
        implements HasSizeBuilder<Grid<E>, GridBuilder<E>>, HasStyleBuilder<Grid<E>, GridBuilder<E>>,
        FocusableBuilder<Grid<E>, GridBuilder<E>>, HasEnabledBuilder<Grid<E>, GridBuilder<E>>,
        HasItemsBuilder<Grid<E>, E, GridBuilder<E>>, HasDataProviderBuilder<Grid<E>, E, GridBuilder<E>> {

    /**
     * {@link EntityBuilder} for {@link Grid.Column}s.
     */
    public final class GridColumnBuilder extends AbstractEntityBuilder<Grid.Column<E>, GridColumnBuilder> implements Configurer<Grid<E>> {

        private final Function<Grid<E>, Grid.Column<E>> columnSupplier;

        private GridColumnBuilder(Function<Grid<E>, Grid.Column<E>> columnSupplier) {
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
        public GridBuilder<E> add() {
            return GridBuilder.this;
        }
    }

    @Override
    protected Grid<E> instantiate() {
        return new Grid<>();
    }

    /**
     * Builder method, configures a new column.
     *
     * @see Grid#addColumn(String)
     * @param propertyName
     *            The name of the property; might <b>not</b> be null.
     * @return this
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
     * @return this
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
     * @return this
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
     * @return this
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
     * @param valueProvider
     *            The value provider; might <b>not</b> be null.
     * @param sortingProperties
     *            The properties to sort by, might be null.
     * @return this
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
     * @param valueProvider
     *            The component provider; might <b>not</b> be null.
     * @return this
     */
    public <V extends Component> GridColumnBuilder configureComponentColumn(ValueProvider<E, V> valueProvider) {
        GridColumnBuilder columnBuilder = new GridColumnBuilder(grid -> grid.addComponentColumn(valueProvider));
        configure(columnBuilder);
        return columnBuilder;
    }
}
