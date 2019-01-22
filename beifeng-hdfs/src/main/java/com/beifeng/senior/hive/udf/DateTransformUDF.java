package com.beifeng.senior.hive.udf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 1. Implement one or more methods named "evaluate" which will be called by
 * Hive.
 * 
 * 2."evaluate" should never be a void method. However it can return "null" if
 * needed.
 * 
 * @author yu100
 *
 */
public class DateTransformUDF extends UDF {

	private final SimpleDateFormat inputformat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
	private final SimpleDateFormat outputformat = new SimpleDateFormat("yyyyMMddHHmmss");

	public Text evaluate(Text input) {
		Text output = new Text();
		// validate
		if (null == input) {
			return output;
		}
		if (null == input.toString()) {
			return output;
		}
		Date inputDate;
		try {
			inputDate = inputformat.parse(input.toString());
			String outputDate = outputformat.format(inputDate);
			output.set(outputDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return output;
		}
		return output;
	}

	public static void main(String[] args) {
		System.out.println(new DateTransformUDF().evaluate(new Text("31/Aug/2015:00:04:37 +0800")));
	}

}
