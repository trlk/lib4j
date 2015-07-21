package libj.poi.plugin;

import libj.poi.Plugin;
import libj.poi.util.MoneyToStr;
import libj.poi.util.MoneyToStr.Currency;
import libj.poi.util.MoneyToStr.Language;
import libj.poi.util.MoneyToStr.Pennies;
import libj.utils.Num;

public class AmountInWords implements Plugin {

	Language lang;

	public AmountInWords(Language lang) {
		this.lang = lang;
	}

	public boolean hasFunc(String funcName) {
		return funcName.equals("amountInWords");
	}

	public String call(String funcName, Object[] args) {
		return amountInWords(args);
	}

	private String amountInWords(Object[] args) {
		
		Double amount = Num.toDouble(args[0].toString());
		
		String currency;
		
		if (args.length > 1) {
			currency = args[1].toString();	
		} else if (lang == Language.UKR) {
			currency = Currency.UAH.name();
		} else if (lang == Language.RUS) {
			currency = Currency.RUB.name();
		} else {
			currency = Currency.PER100.name();
		}
		
		MoneyToStr moneyToStr = new MoneyToStr(currency, lang, Pennies.NUMBER);
		
		return moneyToStr.convert(amount);
	}

}
