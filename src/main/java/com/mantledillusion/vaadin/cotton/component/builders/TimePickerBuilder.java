package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;

/**
 * {@link ComponentBuilder} for {@link TimePicker}s.
 */
public class TimePickerBuilder extends AbstractComponentBuilder<TimePicker, TimePickerBuilder>
		implements HasSizeBuilder<TimePicker, TimePickerBuilder>, HasStyleBuilder<TimePicker, TimePickerBuilder>,
		FocusableBuilder<TimePicker, TimePickerBuilder>, HasEnabledBuilder<TimePicker, TimePickerBuilder>,
		HasValueBuilder<TimePicker, LocalTime, TimePickerBuilder> {

	@Override
	public TimePicker instantiate() {
		return new TimePicker();
	}

	/**
	 * Builder method, configures the label to set.
	 * 
	 * @see TimePicker#setLabel(String)
	 * @param msgId
	 *            The text to set to the label, or a message id to localize; might be null.
	 * @return this
	 */
	public TimePickerBuilder setLabel(String msgId) {
		return configure(timePicker -> timePicker.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the {@link Locale} to set.
	 * 
	 * @see TimePicker#setLocale(Locale)
	 * @param locale
	 *            The locale to set; might <b>not</b> be null. be null.
	 * @return this
	 */
	public TimePickerBuilder setLocale(Locale locale) {
		return configure(timePicker -> timePicker.setLocale(locale));
	}

	/**
	 * Builder method, configures the minimal date.
	 * 
	 * @see TimePicker#setMin(String)
	 * @param time
	 *            The minimal time; might be null.
	 * @return this
	 */
	public TimePickerBuilder setMinDate(String time) {
		return configure(timePicker -> timePicker.setMin(time));
	}

	/**
	 * Builder method, configures the maximal date.
	 * 
	 * @see TimePicker#setMax(String)
	 * @param time
	 *            The maximal time; might be null.
	 * @return this
	 */
	public TimePickerBuilder setMaxDate(String time) {
		return configure(timePicker -> timePicker.setMax(time));
	}

	/**
	 * Builder method, configures the placeholder text that might be displayed when
	 * nothing is selected.
	 * 
	 * @see TimePicker#setPlaceholder(String)
	 * @param msgId
	 *            The placeholder text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public TimePickerBuilder setPlaceholder(String msgId) {
		return configure(timePicker -> timePicker.setPlaceholder(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the selection to be required.
	 * 
	 * @see TimePicker#setRequired(boolean)
	 * @param required
	 *            True if the week number should be marked required, false otherwise.
	 * @return this
	 */
	public TimePickerBuilder setRequired(boolean required) {
		return configure(timePicker -> timePicker.setRequired(required));
	}

	/**
	 * Builder method, configures the duration in between picker steps.
	 * 
	 * @see TimePicker#setStep(Duration)
	 * @param step
	 *            The duration between steps, evenly dividing an hour a day; might <b>not</b> be null.
	 * @return this
	 */
	public TimePickerBuilder setWeekNumbersVisible(Duration step) {
		return configure(timePicker -> timePicker.setStep(step));
	}
}