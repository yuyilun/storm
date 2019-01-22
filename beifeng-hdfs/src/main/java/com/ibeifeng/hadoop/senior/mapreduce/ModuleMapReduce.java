package com.ibeifeng.hadoop.senior.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ModuleMapReduce extends Configured implements Tool {

	// step1 map
	public static class ModuleMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			// Nothing
		}

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// TODO
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			// Nothing
		}
	}

	// step2 reduce
	public static class ModuleReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			// // Nothing
		}

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			// TODO
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			// Nothing
		}

	}

	// ste3 driver
	public int run(String[] args) throws Exception {
		// 1 get configuration
		// Configuration configuration = new Configuration();
		Configuration configuration = getConf();

		// 2 create job
		Job job = Job.getInstance(configuration, this.getClass()
				.getSimpleName());
		// set jar
		job.setJarByClass(this.getClass());

		// 3 set job
		// input -> map ->reduce -> output
		// 3.1 input
		Path inputPath = new Path(args[0]);
		FileInputFormat.setInputPaths(job, inputPath);

		// 3.2 map
		job.setMapperClass(ModuleMapper.class);
		
		//TODO
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		/***************************** shuffle *********************************/
		// 1) partitioner
		// job.setPartitionerClass(cls);

		// 2) sort
		// job.setSortComparatorClass(cls);

		// 3) optional ,combiner
		// job.setCombinerClass(cls);

		// 4) group
		// job.setGroupingComparatorClass(cls);

		/***************************** shuffle *******************************/

		// 3.3 reduce
		job.setReducerClass(ModuleReduce.class);
		
		//TODO
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// set reduce number
		job.setNumReduceTasks(2);

		// 3.4 output
		Path outputPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, outputPath);

		boolean isSuccess = job.waitForCompletion(true);

		return isSuccess ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {

		// int status = new WordCountMapReduce().run(args);
		Configuration configuration = new Configuration();

		// set compress
		// configuration.set("mapreduce.map.output.compress", "true");
		//configuration.set("mapreduce.map.output.compress.codec","mapreduce.map.output.compress");

		int status = ToolRunner.run(configuration,
				new ModuleMapReduce(), args);
		
		System.exit(status);
	}
}