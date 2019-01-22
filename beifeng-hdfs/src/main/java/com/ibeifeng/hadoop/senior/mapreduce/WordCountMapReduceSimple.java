package com.ibeifeng.hadoop.senior.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountMapReduceSimple {

	// step1 map
	public static class WordCountMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		private Text mapOutputKey = new Text();

		private final IntWritable mapOutputValue = new IntWritable(1);

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String lineValue = value.toString();

			StringTokenizer tokenizer = new StringTokenizer(lineValue);

			while (tokenizer.hasMoreTokens()) {
				String wordValue = tokenizer.nextToken();
				mapOutputKey.set(wordValue);
				context.write(mapOutputKey, mapOutputValue);
			}
		}
	}

	// step2 reduce
	public static class WordCountReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		
		private IntWritable outputValue = new IntWritable();
		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for(IntWritable value : values){
				sum += value.get();
			}
			outputValue.set(sum);
			context.write(key, outputValue);
		}
	}

	// ste3 driver
	public int run(String[] args) throws Exception {
		// step1 get configuration
		Configuration configuration = new Configuration();
		// step2 create job
		Job job = Job.getInstance(configuration, this.getClass()
				.getSimpleName());
		// set jar
		job.setJarByClass(this.getClass());

		// step3 set job
		// input -> map ->reduce -> output
		// 3.1 input
		Path inputPath = new Path(args[0]);
		FileInputFormat.setInputPaths(job, inputPath);

		// 3.2 map
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		// 3.3 reduce
		job.setReducerClass(WordCountReduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// 3.4 output
		Path outputPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, outputPath);

		boolean isSuccess = job.waitForCompletion(true);

		return isSuccess ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {

		int status = new WordCountMapReduceSimple().run(args);
		System.exit(status);
	}

}
