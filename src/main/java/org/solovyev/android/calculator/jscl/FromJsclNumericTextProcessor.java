/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.calculator.model.TextProcessor;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:48 PM
 */
class FromJsclNumericTextProcessor implements TextProcessor<String> {

	@NotNull
	@Override
	public String process(@NotNull String result) throws ParseException {
		try {
			final Double doubleValue = Double.valueOf(result);

			if (doubleValue.isInfinite()) {
				result = MathType.INFINITY;
			} else {
				result = CalculatorEngine.instance.format(doubleValue);
			}
		} catch (NumberFormatException e) {
			result = result.replace(MathType.INFINITY_JSCL, MathType.INFINITY);
			if (result.contains(MathType.IMAGINARY_NUMBER_JSCL)) {
				try {
					result = createResultForComplexNumber(result.replace(MathType.IMAGINARY_NUMBER_JSCL, MathType.IMAGINARY_NUMBER));
				} catch (NumberFormatException e1) {
					// throw original one
					throw new ParseException(e);
				}

			}
		}

		return result;
	}

	private String format(@NotNull String value) {
		return CalculatorEngine.instance.format(Double.valueOf(value));
	}

	protected String createResultForComplexNumber(@NotNull final String s) {
		final Complex complex = new Complex();

		String result = "";
		// may be it's just complex number
		int plusIndex = s.lastIndexOf("+");
		if (plusIndex >= 0) {
			complex.setReal(format(s.substring(0, plusIndex)));
			result += complex.getReal();
			result += "+";
		} else {
			plusIndex = s.lastIndexOf("-");
			if (plusIndex >= 0) {
				complex.setReal(format(s.substring(0, plusIndex)));
				result += complex.getReal();
				result += "-";
			}
		}


		int multiplyIndex = s.indexOf("*");
		if (multiplyIndex >= 0) {
			complex.setImaginary(format(s.substring(plusIndex >= 0 ? plusIndex + 1 : 0, multiplyIndex)));
			result += complex.getImaginary();

		}

		result += MathType.IMAGINARY_NUMBER;

		return result;
	}

	private class Complex {

		@Nullable
		private String real;

		@Nullable
		private String imaginary;

		@Nullable
		public String getReal() {
			return real;
		}

		public void setReal(@Nullable String real) {
			this.real = real;
		}

		@Nullable
		public String getImaginary() {
			return imaginary;
		}

		public void setImaginary(@Nullable String imaginary) {
			this.imaginary = imaginary;
		}
	}

}