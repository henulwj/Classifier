package classifier;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;


public class FlowsProClassifier {
	
	//无残空构造函数
	public FlowsProClassifier(){
		
	}
	
	//有标签数据集
	private Instances labelInsts;
	
	//无标签数据集
	private Instances unlabelInsts;
	
	//训练集原始数据标签
	private FastVector originLabel;
	
	//流标签增殖后的训练集  
	private Instances totalInsts;
	
	//聚类中心点
	private Instances clusterCentroids;
	
	//聚类之前设置的聚类簇数目
	private int numPreClusters;
	
	//聚类之后产生的聚类簇数目  
	private int numClusters;
	
	//K-Means方法产生的聚类簇
	private Instances[] clusterGroup;
	
	//K-Means方法的聚类之后的误差平方和
	double squaredError;

	//K-Means方法产生的聚类簇的标签id
	private int[] clusterClass;
	
	//K-Means方法产生的聚类簇的标签
	private String[] clusterLabel;
	
	//K-Means方法计算计算两个流之间的距离的方法类
	private DistanceFunction distanceFunction;
	
	public Instances getLabelInsts() {
		return labelInsts;
	}

	public void setLabelInsts(Instances labelInsts) {
		this.labelInsts = labelInsts;
	}

	public Instances getUnlabelInsts() {
		return unlabelInsts;
	}

	public void setUnlabelInsts(Instances unlabelInsts) {
		this.unlabelInsts = unlabelInsts;
	}

	public FastVector getOriginLabel() {
		return originLabel;
	}

	public void setOriginLabel(FastVector originLabel) {
		this.originLabel = originLabel;
	}

	public Instances getTotalInsts() {
		return totalInsts;
	}

	public void setTotalInsts(Instances totalInsts) {
		this.totalInsts = totalInsts;
	}

	public Instances getClusterCentroids() {
		return clusterCentroids;
	}

	public void setClusterCentroids(Instances clusterCentroids) {
		this.clusterCentroids = clusterCentroids;
	}

	public int getNumPreClusters() {
		return numPreClusters;
	}

	public void setNumPreClusters(int numPreClusters) {
		this.numPreClusters = numPreClusters;
	}

	public int getNumClusters() {
		return numClusters;
	}

	public void setNumClusters(int numClusters) {
		this.numClusters = numClusters;
	}

	public Instances[] getClusterGroup() {
		return clusterGroup;
	}

	public void setClusterGroup(Instances[] clusterGroup) {
		this.clusterGroup = clusterGroup;
	}

	public double getSquaredError() {
		return squaredError;
	}

	public void setSquaredError(double squaredError) {
		this.squaredError = squaredError;
	}
	
	public int[] getClusterClass() {
		return clusterClass;
	}

	public void setClusterClass(int[] clusterClass) {
		this.clusterClass = clusterClass;
	}

	public String[] getClusterLabel() {
		return clusterLabel;
	}

	public void setClusterLabel(String[] clusterLabel) {
		this.clusterLabel = clusterLabel;
	}

	public DistanceFunction getDistanceFunction() {
		return distanceFunction;
	}

	public void setDistanceFunction(DistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	//读取数据集文件转换为Instances
	public Instances getFileInstances( String fileName ) throws FileNotFoundException,IOException   {
		
		FileReader frData = new FileReader( fileName ); 
		Instances inst = new Instances( frData ); 
		return inst;
	}
	
	//合并两个具有相同属性的Instances
	public Instances combineInstances(Instances srcA,Instances srcB){
		Instances result = new Instances(srcA,srcA.numInstances()+srcB.numInstances());
		for (int i = 0; i < srcA.numInstances(); i++) {
			result.add(srcA.instance(i));
		}
		for (int i = 0; i < srcB.numInstances(); i++) {
			result.add(srcB.instance(i));
		}
		return result;
	}
	
	//保存训练集的原始流标签，计算Traning Purity需要用到
	public void preserveOriginLable(Instances label,Instances unlabel) {
		Instances result = combineInstances(label, unlabel);
		originLabel = new FastVector(result.numInstances());
		for (int i = 0; i < result.numInstances(); i++) {
			originLabel.addElement( result.instance(i).value(result.numAttributes()-1));
		}
		Attribute labelAttr = unlabel.attribute(unlabel.numAttributes()-1);
		unlabel.deleteAttributeAt(unlabel.numAttributes()-1);
		unlabel.insertAttributeAt(labelAttr,unlabel.numAttributes());
		
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(unlabel);
//		saver.setFile(new File("F:/isp/training/unlabelled/5000/unlabelled-copy.arff"));
//		saver.writeBatch();
	}
	
	
	//比对三元组，进行流标签增殖
	public void flowLabelPropagation() {
		Instance instA;
		Instance instB;
		for (int i = 0; i < labelInsts.numInstances(); i++) {
			instA=labelInsts.instance(i);
			for (int j = 0; j < unlabelInsts.numInstances(); j++) {
				instB=unlabelInsts.instance(j);				
				if((instB.isMissing(instB.numAttributes()-1))){
					//实际使用的数据集汇总没有协议字段，故在此只比较目的IP和目的端口
					if(((instA.stringValue(2)).equals(instB.stringValue(2)))&&
							(instA.value(3)==instB.value(3))){//&&(instA.stringValue(4)==instB.stringValue(4)
						instB.setValue((instB.numAttributes()-1), instA.stringValue(instA.numAttributes()-1));
					}
				}
			}	
		}
		
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(unlabelInsts);
//		saver.setFile(new File("F:/isp-preprocess/uncompletelabelled.arff"));
//		saver.writeBatch();
	}
	
	
	//利用K-Means聚类方法构造分类器
	public void constructNCC() throws Exception {
		totalInsts = this.combineInstances(labelInsts, unlabelInsts);	//合并有标签数据集和无标签数据集
		int numPreLabelled = labelInsts.numInstances();	//有标签数据集中的流的个数
		//聚类不用五元组，故删除五元组，由于数据集中没有协议，故在此只删除源IP，源端口，目的IP，目的端口
		int num = 4;
		while (num-- > 0){
			totalInsts.deleteAttributeAt(0);
		}
		
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(clustersInsts);
//		saver.setFile(new File("F:/isp-preprocess/afterFLPClustersInsts.arff"));
//		saver.writeBatch();
		
		Attribute classAttr = totalInsts.attribute(totalInsts.numAttributes()-1);	//保存流标签属性
		FastVector classVal = new FastVector(totalInsts.numInstances());	//保存流标签增殖后训练集的流标签
		for (int i = 0; i < totalInsts.numInstances(); i++) {
			classVal.addElement( totalInsts.instance(i).value(totalInsts.numAttributes()-1));	
			
		}
		//对数据集进行聚类，不需要流标签，删除
		totalInsts.deleteAttributeAt(totalInsts.numAttributes()-1);
		
		//调用Weka中的K-Means算法进行聚类
		SimpleKMeans KMean = new SimpleKMeans();	//实例化K-Means方法
		
		KMean.setNumClusters(this.numPreClusters);	//设置K-Means方法聚类簇数目
		
		KMean.setPreserveInstancesOrder(true);	//设置聚类之后数据集保持原来的顺序
		
		KMean.setDontReplaceMissingValues(true);	//设置聚类是不替换确实的流标签
		
		KMean.setSeed(100);	//设置聚类随机种子数
		
		KMean.buildClusterer(totalInsts);	//建立聚类器，开始聚类
		
		squaredError =  KMean.getSquaredError();	//得到聚类之后的误差平方和
		
		clusterCentroids = KMean.getClusterCentroids();	//得到聚类簇的中心点
		
		distanceFunction = KMean.getDistanceFunction();	//得到聚类使用的距离方法类
		
		//复制以一个流标签标签属性，用来保存原始流标签
		Attribute orginClassAttr = classAttr.copy("originClass");	
		//训练集增加保存原始流标签的属性
		totalInsts.insertAttributeAt(orginClassAttr,totalInsts.numAttributes());
		//训练集增加保存流标签增殖后的流标签属性
		totalInsts.insertAttributeAt(classAttr,totalInsts.numAttributes());
		//给增加的原始流标签属性赋值
		for (int i = 0; i < totalInsts.numInstances(); i++) {
			double dou =  (double)(originLabel.elementAt(i));
			if(Double.isNaN(dou))	//数据集流标签是缺失的
			{
				totalInsts.instance(i).setMissing(totalInsts.numAttributes()-2);	//设置流标签缺失
			}
			else{	//不缺失，则设置对应的流标签
				int index = (int)dou;
				totalInsts.instance(i).setValue(totalInsts.numAttributes()-2, classAttr.value(index));
			}
		}
		//给增加的流标签增殖之后的流标签属性赋值
		for (int i = 0; i < totalInsts.numInstances(); i++) {
			double dou =  (double)(classVal.elementAt(i));
			if(Double.isNaN(dou))
			{
				totalInsts.instance(i).setMissing(totalInsts.numAttributes()-1);
			}
			else{
				int index = (int)dou;
				totalInsts.instance(i).setValue(totalInsts.numAttributes()-1, classAttr.value(index));
			}
		}
		//得到聚类之后的聚类簇数目
		numClusters = KMean.getNumClusters();
		//得到聚类之后每个流被分配到的聚类id
		int[] assignment = KMean.getAssignments();
		
		clusterClass = new int[numClusters];	//实例化聚类簇标签id数组
		clusterLabel = new String[numClusters];	//实例化聚类簇标签数组
		clusterGroup = new Instances[numClusters];	//实例化聚类簇数组
		//实例化每一聚类簇
		for (int i = 0; i < clusterGroup.length; i++) {
			clusterGroup[i] = new Instances(totalInsts, -1);
		}
		//根据聚类时候每一个流分配到的聚类id，将其添加到对应的聚类簇中
		for (int i = 0; i < assignment.length; i++) {
			clusterGroup[assignment[i]].add(totalInsts.instance(i));
		}
		//统计每一个聚类簇中的概率最大的标签流，标记聚类簇为对应的标签
		for (int i = 0; i < clusterGroup.length; i++) {
			//聚类簇中缺失标签的流数目
			int unlabelcount = clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).missingCount;
			if(unlabelcount == clusterGroup[i].numInstances())	//聚类簇中所有的流都缺失标签
			{
				clusterClass[i] = -1;	//设置聚类簇id为-1
				clusterLabel[i] = "unknown";	//设置聚类簇标签为unknown
			}
			else{	//如果聚类簇中的流没有全部缺失标签
				//将聚类簇id和标签设置为聚类簇中概率最大的标签，
				clusterClass[i] = Utils.maxIndex(clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).nominalCounts);
				clusterLabel[i] = totalInsts.attribute(totalInsts.numAttributes()-1).value(clusterClass[i]);
			}
		}

		//输出每一个聚类簇的流分配
		
//		System.out.printf("%10s","");
//		Enumeration enu =  totalInsts.attribute(totalInsts.numAttributes()-1).enumerateValues();
//		while (enu.hasMoreElements()) {
//			Object object = (Object) enu.nextElement();
//			System.out.printf("%10s",object.toString());
//		}
//		System.out.printf("%10s","missing");
//		System.out.println();
//		for (int i = 0; i < clusterClass.length; i++) {
//			int[] arr = clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).nominalCounts;
//			System.out.printf("%10s","cluster"+i);
//			for (int j = 0; j < arr.length; j++) {
//				System.out.printf("%10d",arr[j]);
//				
//			}
//			System.out.printf("%10d",clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).missingCount);
//			System.out.println();
//			
//		}
//		
//		for (int i = 0; i < clusterClass.length; i++) {
//			System.out.println("cluster"+i+"->"+clusterLabel[i]+"("+clusterGroup[i].numInstances()+")");
//		}
		
		
		//合并具有相同标签的聚类簇的id列表
		ArrayList<Integer> clusterClassList = new ArrayList<Integer>();
		//合并具有相同标签的聚类簇的标签列表
		ArrayList<String> clusterLabelList = new ArrayList<String>();
		//合并具有相同标签的聚类簇的Instances列表
		ArrayList<Instances> clusterInstList = new ArrayList<Instances>();
		//合并具有相同标签的聚类簇
		for (int i = 0; i < clusterClass.length; i++) {
			if(!clusterClassList.contains(clusterClass[i])){//id列表中不含有当前聚类簇的id
				clusterClassList.add(clusterClass[i]);	//添加到id列表
				clusterLabelList.add(clusterLabel[i]);	//添加到标签列表
				clusterInstList.add(clusterGroup[i]);	//添加到Instances列表
			}
			else{	//id列表中已含有当前聚类簇id
				int tmpInt = clusterClassList.indexOf(clusterClass[i]);	//找到对应标签id在列表中的位置
				//合并当前聚类簇与对应位置的Instances合并
				Instances tmpInst = this.combineInstances(clusterInstList.get(tmpInt), clusterGroup[i]);
				//替换对应位置的Instances
				clusterInstList.set(tmpInt, tmpInst);
			}
			
		} 
		
		//输出聚类簇合并之后的结果
		System.out.println("*****************************");		
		int numKnown = 0;	//所有已知标签的聚类簇包含的流的总数目
		for (int i = 0; i < clusterClassList.size(); i++) {
			if(clusterLabelList.get(i).equals("unknown"))
			{
				//训练集总数目减去未知聚类簇集的中的流的总数目
				numKnown = totalInsts.numInstances() - clusterInstList.get(i).numInstances();	
			}
			//输出聚类簇合并之后的结果
			System.out.println("cluster"+i+"->"+clusterLabelList.get(i)+"("+clusterInstList.get(i).numInstances()+")");
		}
		//正确识别的流的数目
		int correctFlows = 0;
		//计算正确识别的流的数目
		for (int i = 0; i < clusterInstList.size(); i++) {
			Instances tmp =  clusterInstList.get(i);
			int itmp = clusterClassList.get(i);
			for (int j = 0; j < tmp.numInstances(); j++) {
				//得到每一个流的原始标签id
				double dtmp = tmp.instance(j).value(totalInsts.numAttributes()-2);	
				if(itmp == -1)	
				{
					if(Double.isNaN(dtmp)){	//正确识别未知流
						correctFlows++;
					}
				}
				else
				{
					int idtmp = (int)dtmp;
					if(itmp == idtmp){	//正确识别对应标签流
						correctFlows++;
					}
				}
			}
		}
		
		System.out.println("the errors of squared:"+squaredError);
		
		System.out.println("Propagation Rate:"+ numKnown/(double)numPreLabelled);
		
		System.out.println("Training Purity:"+correctFlows/(double)totalInsts.numInstances());
		
	}
	
	
	//分类识别单个网络流
	public int classifyFlow(Instance inst)
	{
		double minDist = Integer.MAX_VALUE;	//每一个流聚类聚类中心点的最小距离
		int bestCluster = 0;	//流被识别的标签id
		for (int i = 0; i < clusterCentroids.numInstances(); i++) {
			
			 double dist = distanceFunction.distance(inst, clusterCentroids.instance(i));
			 //找到最小距离对应的标签id
		     if (dist < minDist) {
		    	 minDist = dist;
		    	 bestCluster = i;
		     }
		}
		return bestCluster;
	}
	
	//用分类器对测试数据集进行分类，评价分类器的分类性能
	public void evaluateClassifier(Instances insts){
		//测试数据集中缺失标签的流的数目
		int numTestUnknowFlows = insts.attributeStats(insts.numAttributes()-1).missingCount;
		//测试数据集中已知标签的流的数目
		int numTestKnowFlows = insts.numInstances() - numTestUnknowFlows;
		//保存测试数据集的标签属性
		Attribute classAttr = insts.attribute(insts.numAttributes()-1);
		//测试数据集的标签属性的种类数目
		int numofAttrVal = classAttr.numValues();
		//每一种种类的标签的分类识别数组，包括unknown
		int[][] preNum = new int[numofAttrVal+1][numofAttrVal+1];
		//评价指标
		String[] rateName = {"TP Rate","FP Rate","Precision","Recall","F-Measure"};
		//每一种标签的分类评价指标数组
		double[][] allRate = new double[numofAttrVal+1][rateName.length];
		//保存测试数据集的标签属性值
		FastVector classVal = new FastVector(insts.numInstances());
		for (int j = 0; j < insts.numInstances(); j++) {
			double douA = insts.instance(j).value(insts.numAttributes()-1);
			if(Double.isNaN(douA))
			{
				classVal.addElement((double)(-1));
			}
			else{
				classVal.addElement(douA);	
			}
			
		}
		//删除测试数据集的标签属性
		insts.deleteAttributeAt(insts.numAttributes()-1);
		//每一流预测的标签属性值
		FastVector preClassVal = new FastVector(insts.numInstances());
		//对每一个流进行识别，保存识别的标签纸
		for (int i = 0; i < insts.numInstances(); i++) {
			preClassVal.addElement((double)clusterClass[this.classifyFlow(insts.instance(i))]);
		}
		int numOfAccuracy = 0;	//正确识别的流的数目
		//将预测标签属性值遇原始的标签属性值进行对比，如果相同正确识别的流数目加1
		for (int i = 0; i < insts.numInstances(); i++) {
			if((double)classVal.elementAt(i)==-1.0){	//未知流
				if((double)(preClassVal.elementAt(i))==-1.0){	//未知流正确识别
					preNum[numofAttrVal][numofAttrVal]++;
				}else{	//未知流错误识别
					preNum[numofAttrVal][(int)(double)(preClassVal.elementAt(i))]++;
				}
			}
			else{	//已知标签的流
				if((double)(preClassVal.elementAt(i))==-1.0){	//已知标签流识别为未知流
					preNum[(int)(double)(classVal.elementAt(i))][numofAttrVal]++;
				}else{	//已知标签流识别为已知标签流，包括正确识别
					preNum[(int)(double)(classVal.elementAt(i))][(int)(double)(preClassVal.elementAt(i))]++;
				}
			}
			//正确识别后，正确识别数目加1
			if((double)(classVal.elementAt(i))==(double)(preClassVal.elementAt(i)))
			{
				numOfAccuracy++;
			}
		}
				
		//每一种流的分类识别的转置数组
		int[][] rotatePreNum = new int[numofAttrVal+1][numofAttrVal+1];
		//给转置数组赋值
		for (int i = 0; i < rotatePreNum.length; i++) {
			for (int k = 0; k < rotatePreNum[0].length; k++) {
				rotatePreNum[i][k] = preNum[k][i];
			}
		}
		//正确识别的未知流的数目
		int numAccPreUnkonw = preNum[numofAttrVal][numofAttrVal];
		//已知标签流被识别为未知流的数目
		int numKnownPreUnknown = Utils.sum(rotatePreNum[numofAttrVal]) - numAccPreUnkonw;
		
		System.out.println("False Detection Rate:" + numKnownPreUnknown/(double)numTestKnowFlows);
		System.out.println("True Detection Rate:" + 
				((numTestUnknowFlows==0) ? 0 : numAccPreUnkonw/(double)numTestUnknowFlows));
		//计算各种评价指标
		for (int i = 0; i < allRate.length; i++) {
			int tp = preNum[i][i];	//TP值
			int fn = Utils.sum(preNum[i])-tp;	//FN值
			int fp = Utils.sum(rotatePreNum[i])-tp;	//FP值
			int tn = insts.numInstances()-(tp+fn+fp);	//TN值
			
			allRate[i][0] = ((tp+fn)==0) ? 0 : (tp/(double)(tp+fn));	//TP Rate
			allRate[i][1] = ((fp+tn)==0) ? 0 : (fp/(double)(fp+tn));	//FP Rate
			allRate[i][2] = ((fp+tn)==0) ? 0 : (tp/(double)(tp+fp));	//Precision
			allRate[i][3] = allRate[i][0];	//Recall
			allRate[i][4] = ((2*tp+fp+fn)==0) ? 0 : (2*tp/(double)(2*tp+fp+fn));	//F-Measure
		}
		
		System.out.println("=== Detailed Accuracy By Class ===");
		//输出每一种标签的各种评价指标
		System.out.printf("%10s","");
		for (int i = 0; i < rateName.length; i++) {
			System.out.printf("%10s",rateName[i]);
		}
		System.out.println();
		
		for (int i = 0; i < allRate.length; i++) {
			if(i==allRate.length-1){
				System.out.printf("%10s","unknown");
			}else{
				System.out.printf("%10s",classAttr.value(i));
			}

			for (int j = 0; j < allRate[0].length; j++) {
				System.out.printf("    %1.4f",allRate[i][j]);
			}
			System.out.println();
		}
		
		System.out.println("=== Confusion Matrix ===");
		//输出每一中标签的分类情况
		System.out.printf("%10s","");
		for (int i = 0; i < numofAttrVal; i++) {
			System.out.printf("%10s",classAttr.value(i));
		}
		System.out.printf("%10s","unknown");
		System.out.println();
		for (int i = 0; i < preNum.length; i++) {
			if(i==preNum.length-1){
				System.out.printf("%10s","unknown");
			}else{
				System.out.printf("%10s",classAttr.value(i));
			}
			for (int j = 0; j < preNum[0].length; j++) {
				System.out.printf("%10d",preNum[i][j]);
			}
			System.out.println();
		}
		
		System.out.println("Total Number of Instances :"+insts.numInstances());
		System.out.println("the rate of accuracy:"+(numOfAccuracy/(double)insts.numInstances()));
		
	}
	
	
			
}
