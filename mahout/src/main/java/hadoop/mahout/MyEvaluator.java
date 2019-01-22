package hadoop.mahout;

import java.io.File;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * 评估推荐模型
 * @author yu100
 *
 */
public class MyEvaluator {
	
	
	
	public static void main(String[] args) throws Exception {
		
		//准备数据 这里是电影评分数据
		//File file = new File("D:\\Download\\ml-20m\\ratings.csv");
		File file = new File("D:\\Download\\ml-10m\\ml-10M100K\\ratings.dat");
		//将数据加载到内存中，GroupLensDataModel是针对开放电影评论数据的
        DataModel dataModel = new org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel(file);
        
        //推荐评估，使用均方根
        //RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
		
        //推荐评估，使用平均差值
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel dataModel1) throws TasteException {
				// 
				UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel1);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2,similarity, dataModel1);
				return new GenericUserBasedRecommender(dataModel1, neighborhood, similarity);
			}
        };
        
        // 用70%的数据用作训练，剩下的30%用来测试
        double score = evaluator.evaluate(builder, null, dataModel, 0.9, 1.0);
        //最后得出的评估值越小，说明推荐结果越好
        System.out.println(score);
		
	}
	
	

}
