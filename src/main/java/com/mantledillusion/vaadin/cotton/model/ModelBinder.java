package com.mantledillusion.vaadin.cotton.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mantledillusion.data.epiphy.ModelPropertyNode;
import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.data.epiphy.context.reference.PropertyIndex;
import com.mantledillusion.data.epiphy.context.reference.PropertyRoute;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.shared.Registration;

abstract class ModelBinder<ModelType> implements ModelHandler<ModelType> {

	private static final Procedure NOOP = () -> {};
	private static final Consumer NOCONSUME = value -> {};

	private interface Procedure {

		void trigger();
	}

	private interface Binding {

		void update(Context context, UpdateType type);

		void unbind();
	}

	enum UpdateType {
		EXCHANGE, ADD, REMOVE;
	}

	private final Context context;
	private final Map<Property<ModelType, ?>, List<Binding>> bindings = new IdentityHashMap<>();

	protected ModelBinder(Context context) {
		this.context = context;
	}

	protected Context getContext() {
		return this.context;
	}

	// ######################################################################################################################################
	// ########################################################## CONSUMER BINDING ##########################################################
	// ######################################################################################################################################

	private static final class ConsumerBinding implements Binding {

		private final Procedure valueReader;

		private ConsumerBinding(Procedure valueReader) {
			this.valueReader = valueReader;
		}

		@Override
		public void update(Context context, UpdateType type) {
			this.valueReader.trigger();
		}

		@Override
		public void unbind() {
			// Not required
		}
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(Consumer<FieldValueType> consumer, Property<ModelType, FieldValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(ModelBinder.this.get(property));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(Consumer<FieldValueType> consumer,
														 Converter<FieldValueType, PropertyValueType> converter,
														 Property<ModelType, PropertyValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(converter.toField(ModelBinder.this.get(property)));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	// ######################################################################################################################################
	// ########################################################## HASVALUE BINDING ##########################################################
	// ######################################################################################################################################

	@SuppressWarnings("rawtypes")
	private static final class HasValueBinding implements Binding, ValueChangeListener {

		private static final long serialVersionUID = 1L;

		private final Procedure valueReader;
		private final Procedure valueWriter;
		private final Procedure enablementUpdater;
		private final Registration registration;
		private boolean synchronizing = false;

		@SuppressWarnings("unchecked")
		private HasValueBinding(HasValue<?, ?> hasValue, Procedure valueReader, Procedure valueWriter,
				Procedure enablementSwitch) {
			this.valueReader = valueReader;
			this.valueWriter = valueWriter;
			this.enablementUpdater = enablementSwitch;
			this.registration = hasValue.addValueChangeListener(this);
		}

		@Override
		public synchronized void valueChanged(ValueChangeEvent event) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.valueWriter.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public synchronized void update(Context context, UpdateType type) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.enablementUpdater.trigger();
				this.valueReader.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public void unbind() {
			this.registration.remove();
		}
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(HasValue<?, FieldValueType> hasValue,
									  Property<ModelType, FieldValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue
				.setValue(ObjectUtils.defaultIfNull(ModelBinder.this.get(property), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property.isWritable()) {
			valueWriter = () -> ModelBinder.this.set(property, hasValue.getValue());
		} else {
			valueWriter = NOOP;
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(HasValue<?, FieldValueType> hasValue,
														 Converter<FieldValueType, PropertyValueType> converter,
														 Property<ModelType, PropertyValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue.setValue(
				ObjectUtils.defaultIfNull(converter.toField(ModelBinder.this.get(property)), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property.isWritable()) {
			valueWriter = () -> ModelBinder.this.set(property, converter.toProperty(hasValue.getValue()));
		} else {
			valueWriter = NOOP;
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	private synchronized <FieldValueType> void createHasValueBinding(Property<ModelType, ?> property,
			HasValue<?, FieldValueType> hasValue, Procedure valueReader, Procedure valueWriter) {
		Consumer<Boolean> enabler;
		if (hasValue instanceof HasEnabled) {
			enabler = enable -> ((HasEnabled) hasValue).setEnabled(enable);
		} else {
			enabler = NOCONSUME;
		}

		Procedure enablementSwitch = () -> {
			boolean exists = ModelBinder.this.exists(property);
			hasValue.setReadOnly(!property.isWritable() || !exists);
			enabler.accept(exists);
		};

		addBinding(property, new HasValueBinding(hasValue, valueReader, valueWriter, enablementSwitch));
	}

	// ######################################################################################################################################
	// ######################################################## DATAPROVIDER BINDING ########################################################
	// ######################################################################################################################################

	private static final class PropertyDataProviderBinding<R> implements Binding {

		private final Consumer<R> updater;
		private final BiConsumer<R, UpdateType> modifier;
		private final Function<Context, R> indexRetriever;

		private PropertyDataProviderBinding(Consumer<R> updater, BiConsumer<R, UpdateType> modifier,
				Function<Context, R> indexRetriever) {
			this.updater = updater;
			this.modifier = modifier;
			this.indexRetriever = indexRetriever;
		}

		@Override
		public void update(Context context, UpdateType type) {
			R propertyReference = this.indexRetriever.apply(context);
			if (type == UpdateType.EXCHANGE) {
				this.updater.accept(propertyReference);
			} else {
				this.modifier.accept(propertyReference, type);
			}
		}

		@Override
		public void unbind() {
			// not required
		}
	}
	
	// ListedProperty

	public static final class ListedPropertyDataProvider<E> extends ListDataProvider<E> {

		private static final long serialVersionUID = 1L;

		private final List<E> elements;

		private ListedPropertyDataProvider() {
			super(new ArrayList<>());
			this.elements = (List<E>) getItems();
		}

		private void exchange(List<E> elements) {
			this.elements.clear();
			if (elements != null) {
				this.elements.addAll(elements);
			}
			refreshAll();
		}

		private void add(E element, int index) {
			this.elements.add(index, element);
			this.refreshItem(element);
		}

		private void remove(int index) {
			E element = this.elements.remove(index);
			this.refreshItem(element);
		}

		private void refresh(E element) {
			this.refreshItem(element);
		}
	}

	/**
	 * Builds and binds a {@link DataProvider} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <ElementType>
	 *            The element type of the {@link DataProvider} to provide.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 * @return A new {@link DataProvider} that is bound to the given {@link Property}; never null
	 */
	public <ElementType> ListedPropertyDataProvider<ElementType> provide(Property<ModelType, List<ElementType>> property) {
		if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		ListedPropertyDataProvider<ElementType> provider = new ListedPropertyDataProvider<>();

		Consumer<Integer> updater = index -> {
			List<ElementType> elements = ModelBinder.this.get(property);
			if (index == null) {
				provider.exchange(elements);
			} else {
				provider.refresh(elements.get(index));
			}
		};
		BiConsumer<Integer, UpdateType> modifier = (index, type) -> {
			if (type == UpdateType.ADD) {
				List<ElementType> elements = ModelBinder.this.get(property);
				ElementType element = elements.get(index);
				provider.add(element, index);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(index);
			}
		};
		Function<Context, Integer> indexRetriever = context -> context.containsReference(property) ?
				context.getReference(property, PropertyIndex.class).getReference() : null;

		addBinding(property, new PropertyDataProviderBinding<>(updater, modifier, indexRetriever));

		return provider;
	}

	/**
	 * Builds and binds a {@link DataProvider} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <ElementType>
	 *            The element type of the {@link DataProvider} to provide.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the provider's
	 *            and the properties' element types; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 * @return A new {@link DataProvider} that is bound to the given {@link Property}; never null
	 */
	public <ElementType, PropertyValueType> ListedPropertyDataProvider<ElementType> provide(
			Converter<ElementType, PropertyValueType> converter,
			Property<ModelType, List<PropertyValueType>> property) {
		if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		ListedPropertyDataProvider<ElementType> provider = new ListedPropertyDataProvider<>();

		Consumer<Integer> updater = index -> {
			@SuppressWarnings("unchecked")
			List<ElementType> elements = ObjectUtils
					.defaultIfNull(ModelBinder.this.get(property), (List<PropertyValueType>) Collections.emptyList())
					.stream().map(converter::toField).collect(Collectors.toList());
			if (index == null) {
				provider.exchange(elements);
			} else {
				provider.refresh(elements.get(index));
			}
		};
		BiConsumer<Integer, UpdateType> modifier = (index, type) -> {
			if (type == UpdateType.ADD) {
				@SuppressWarnings("unchecked")
				List<ElementType> elements = ObjectUtils
						.defaultIfNull(ModelBinder.this.get(property),
								(List<PropertyValueType>) Collections.emptyList())
						.stream().map(converter::toField).collect(Collectors.toList());
				ElementType element = elements.get(index);
				provider.add(element, index);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(index);
			}
		};
		Function<Context, Integer> indexRetriever = context -> context.containsReference(property) ?
				context.getReference(property, PropertyIndex.class).getReference() : null;

		addBinding(property, new PropertyDataProviderBinding<>(updater, modifier, indexRetriever));

		return provider;
	}
	
	// NodedProperty
	
	public static final class NodePropertyDataProvider<N> extends TreeDataProvider<N> {

		private static final long serialVersionUID = 1L;
		
		private final TreeData<N> treeData;
		
		public NodePropertyDataProvider() {
			super(new TreeData<>());
			this.treeData = getTreeData();
		}
		
		@SuppressWarnings("unchecked")
		private void exchange(N root, ModelPropertyNode<?, N> property) {
			this.treeData.clear();
			if (root != null) {
				this.treeData.addRootItems(root);
				addChildren(root, property);
			}
			refreshAll();
		}
		
		@SuppressWarnings("unchecked")
		private void add(N parent, N node, ModelPropertyNode<?, N> property) {
			this.treeData.addItem(parent, node);
			this.treeData.moveAfterSibling(node, property.getNodeRetriever().predecessor(parent, node));
			addChildren(node, property);
			refreshAll();
		}
		
		private void addChildren(N parent, ModelPropertyNode<?, N> property) {
			if (parent != null) {
				for (N node: property.getNodeRetriever().iterate(parent)) {
					this.treeData.addItem(parent, node);
					addChildren(node, property);
				}
			}
		}
		
		private void remove(N node) {
			this.treeData.removeItem(node);
			refreshAll();
		}

		private void refresh(N node) {
			this.refreshItem(node);
		}
	}
	
	public <ElementType> NodePropertyDataProvider<ElementType> provide(ModelPropertyNode<ModelType, ElementType> property) {
		if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		NodePropertyDataProvider<ElementType> provider = new NodePropertyDataProvider<>();

		Consumer<Context[]> updater = indices -> {
			if (indices == null) {
				provider.exchange(ModelBinder.this.get(property), property);
			} else {
				provider.refresh(ModelBinder.this.get(property, Context.of(PropertyRoute.of(property.getNodeRetriever(), indices))));
			}
		};
		BiConsumer<Context[], UpdateType> modifier = (indices, type) -> {
			Context context = null;
			if (indices.length > 1) {
				context = Context.of(PropertyRoute.of(property.getNodeRetriever(), Arrays.copyOf(indices, indices.length-1)));
			}
			ElementType parent = ModelBinder.this.get(property, context);

			ElementType node = ModelBinder.this.get(property, Context.of(PropertyRoute.of(property.getNodeRetriever(), indices)));
			if (type == UpdateType.ADD) {
				provider.add(parent, node, property);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(node);
			}
		};
		Function<Context, Context[]> indexRetriever = context -> context.containsReference(property.getNodeRetriever()) ?
				context.getReference(property.getNodeRetriever(), PropertyRoute.class).getReference() : null;

		addBinding(property, new PropertyDataProviderBinding<>(updater, modifier, indexRetriever));

		return provider;
	}

	// ######################################################################################################################################
	// ########################################################## BINDING HANDLING ##########################################################
	// ######################################################################################################################################

	private void addBinding(Property<ModelType, ?> property, Binding binding) {
		if (!this.bindings.containsKey(property)) {
			this.bindings.put(property, new ArrayList<>());
		}
		this.bindings.get(property).add(binding);
	}

	synchronized void update(UpdateType type) {
		this.bindings.forEach((property, bindings) -> bindings.forEach(binding -> binding.update(context, type)));
	}

	synchronized void update(Property<ModelType, ?> property, Context context, UpdateType type) {
		// RUN OVER ALL BINDINGS
		boundPropertyLoop: for (Property<ModelType, ?> boundProperty : this.bindings.keySet()) {
			// IF THE UPDATE'S PROPERTY IS A PARENT OF THE BINDING'S PROPERTY...
			Set<Property<?, ?>> boundHierarchy = boundProperty.getHierarchy();
			Set<Property<?, ?>> hierarchy = property.getHierarchy();
			if (boundHierarchy.containsAll(hierarchy) || hierarchy.containsAll(boundHierarchy)) {
				// GO OVER ALL OF THE UPDATE'S CONTEXTABLE PROPERTIES
				for (Property<?, ?> contextedProperty : property.getHierarchy()) {
					// CHECK IF THERE IS A KEY FOR THE CONTEXTED PROPERTY THAT IS UNEQUAL TO THE ONE IN THIS BINDER'S CONTEXT
					if (context.containsReference(contextedProperty) && this.context.containsReference(contextedProperty)
							&& !context.getReference(contextedProperty).equals(this.context.getReference(contextedProperty))) {
						// IF THERE IS ONE, THE BINDING IS NOT AFFECTED BY THE UPDATE
						continue boundPropertyLoop;
					}
				}
				this.bindings.get(boundProperty).forEach(binding -> binding.update(context, type));
			}
		}
	}

	@PreDestroy
	private synchronized void destroy() {
		Iterator<Entry<Property<ModelType, ?>, List<Binding>>> mapIter = this.bindings.entrySet().iterator();
		while (mapIter.hasNext()) {
			Iterator<Binding> bindingIter = mapIter.next().getValue().iterator();
			while (bindingIter.hasNext()) {
				bindingIter.next().unbind();
				bindingIter.remove();
			}
			mapIter.remove();
		}
	}
}