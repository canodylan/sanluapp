import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formats a number as currency, truncating (never rounding) to a maximum number
 * of decimal places and only showing decimals when the value actually has them.
 */
@Pipe({
  name: 'truncateCurrency',
  standalone: true
})
export class TruncateCurrencyPipe implements PipeTransform {
  transform(
    value: number | string | null | undefined,
    currencyCode: string = 'EUR',
    locale: string | string[] = 'es-ES',
    maxDecimals = 2
  ): string {
    if (value === null || value === undefined || value === '') {
      return this.formatNumber(0, currencyCode, locale, 0);
    }

    const numericValue = typeof value === 'number' ? value : Number(value);
    if (Number.isNaN(numericValue)) {
      return String(value);
    }

    const factor = Math.pow(10, maxDecimals);
    const truncated = Math.trunc(numericValue * factor) / factor;
    const fixed = truncated.toFixed(maxDecimals);
    const [, decimalsPart = ''] = fixed.split('.');
    const trimmedDecimals = decimalsPart.replace(/0+$/, '');
    const fractionDigits = Math.min(maxDecimals, trimmedDecimals.length);

    return this.formatNumber(truncated, currencyCode, locale, fractionDigits);
  }

  private formatNumber(
    amount: number,
    currencyCode: string,
    locale: string | string[],
    fractionDigits: number
  ): string {
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: currencyCode,
      minimumFractionDigits: fractionDigits,
      maximumFractionDigits: fractionDigits
    }).format(amount);
  }
}
