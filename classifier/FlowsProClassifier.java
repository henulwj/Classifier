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
	
	//�޲пչ��캯��
	public FlowsProClassifier(){
		
	}
	
	//�б�ǩ���ݼ�
	private Instances labelInsts;
	
	//�ޱ�ǩ���ݼ�
	private Instances unlabelInsts;
	
	//ѵ����ԭʼ���ݱ�ǩ
	private FastVector originLabel;
	
	//����ǩ��ֳ���ѵ����  
	private Instances totalInsts;
	
	//�������ĵ�
	private Instances clusterCentroids;
	
	//����֮ǰ���õľ������Ŀ
	private int numPreClusters;
	
	//����֮������ľ������Ŀ  
	private int numClusters;
	
	//K-Means���������ľ����
	private Instances[] clusterGroup;
	
	//K-Means�����ľ���֮������ƽ����
	double squaredError;

	//K-Means���������ľ���صı�ǩid
	private int[] clusterClass;
	
	//K-Means���������ľ���صı�ǩ
	private String[] clusterLabel;
	
	//K-Means�����������������֮��ľ���ķ�����
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

	//��ȡ���ݼ��ļ�ת��ΪInstances
	public Instances getFileInstances( String fileName ) throws FileNotFoundException,IOException   {
		
		FileReader frData = new FileReader( fileName ); 
		Instances inst = new Instances( frData ); 
		return inst;
	}
	
	//�ϲ�����������ͬ���Ե�Instances
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
	
	//����ѵ������ԭʼ����ǩ������Traning Purity��Ҫ�õ�
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
	
	
	//�ȶ���Ԫ�飬��������ǩ��ֳ
	public void flowLabelPropagation() {
		Instance instA;
		Instance instB;
		for (int i = 0; i < labelInsts.numInstances(); i++) {
			instA=labelInsts.instance(i);
			for (int j = 0; j < unlabelInsts.numInstances(); j++) {
				instB=unlabelInsts.instance(j);				
				if((instB.isMissing(instB.numAttributes()-1))){
					//ʵ��ʹ�õ����ݼ�����û��Э���ֶΣ����ڴ�ֻ�Ƚ�Ŀ��IP��Ŀ�Ķ˿�
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
	
	
	//����K-Means���෽�����������
	public void constructNCC() throws Exception {
		totalInsts = this.combineInstances(labelInsts, unlabelInsts);	//�ϲ��б�ǩ���ݼ����ޱ�ǩ���ݼ�
		int numPreLabelled = labelInsts.numInstances();	//�б�ǩ���ݼ��е����ĸ���
		//���಻����Ԫ�飬��ɾ����Ԫ�飬�������ݼ���û��Э�飬���ڴ�ֻɾ��ԴIP��Դ�˿ڣ�Ŀ��IP��Ŀ�Ķ˿�
		int num = 4;
		while (num-- > 0){
			totalInsts.deleteAttributeAt(0);
		}
		
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(clustersInsts);
//		saver.setFile(new File("F:/isp-preprocess/afterFLPClustersInsts.arff"));
//		saver.writeBatch();
		
		Attribute classAttr = totalInsts.attribute(totalInsts.numAttributes()-1);	//��������ǩ����
		FastVector classVal = new FastVector(totalInsts.numInstances());	//��������ǩ��ֳ��ѵ����������ǩ
		for (int i = 0; i < totalInsts.numInstances(); i++) {
			classVal.addElement( totalInsts.instance(i).value(totalInsts.numAttributes()-1));	
			
		}
		//�����ݼ����о��࣬����Ҫ����ǩ��ɾ��
		totalInsts.deleteAttributeAt(totalInsts.numAttributes()-1);
		
		//����Weka�е�K-Means�㷨���о���
		SimpleKMeans KMean = new SimpleKMeans();	//ʵ����K-Means����
		
		KMean.setNumClusters(this.numPreClusters);	//����K-Means�����������Ŀ
		
		KMean.setPreserveInstancesOrder(true);	//���þ���֮�����ݼ�����ԭ����˳��
		
		KMean.setDontReplaceMissingValues(true);	//���þ����ǲ��滻ȷʵ������ǩ
		
		KMean.setSeed(100);	//���þ������������
		
		KMean.buildClusterer(totalInsts);	//��������������ʼ����
		
		squaredError =  KMean.getSquaredError();	//�õ�����֮������ƽ����
		
		clusterCentroids = KMean.getClusterCentroids();	//�õ�����ص����ĵ�
		
		distanceFunction = KMean.getDistanceFunction();	//�õ�����ʹ�õľ��뷽����
		
		//������һ������ǩ��ǩ���ԣ���������ԭʼ����ǩ
		Attribute orginClassAttr = classAttr.copy("originClass");	
		//ѵ�������ӱ���ԭʼ����ǩ������
		totalInsts.insertAttributeAt(orginClassAttr,totalInsts.numAttributes());
		//ѵ�������ӱ�������ǩ��ֳ�������ǩ����
		totalInsts.insertAttributeAt(classAttr,totalInsts.numAttributes());
		//�����ӵ�ԭʼ����ǩ���Ը�ֵ
		for (int i = 0; i < totalInsts.numInstances(); i++) {
			double dou =  (double)(originLabel.elementAt(i));
			if(Double.isNaN(dou))	//���ݼ�����ǩ��ȱʧ��
			{
				totalInsts.instance(i).setMissing(totalInsts.numAttributes()-2);	//��������ǩȱʧ
			}
			else{	//��ȱʧ�������ö�Ӧ������ǩ
				int index = (int)dou;
				totalInsts.instance(i).setValue(totalInsts.numAttributes()-2, classAttr.value(index));
			}
		}
		//�����ӵ�����ǩ��ֳ֮�������ǩ���Ը�ֵ
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
		//�õ�����֮��ľ������Ŀ
		numClusters = KMean.getNumClusters();
		//�õ�����֮��ÿ���������䵽�ľ���id
		int[] assignment = KMean.getAssignments();
		
		clusterClass = new int[numClusters];	//ʵ��������ر�ǩid����
		clusterLabel = new String[numClusters];	//ʵ��������ر�ǩ����
		clusterGroup = new Instances[numClusters];	//ʵ�������������
		//ʵ����ÿһ�����
		for (int i = 0; i < clusterGroup.length; i++) {
			clusterGroup[i] = new Instances(totalInsts, -1);
		}
		//���ݾ���ʱ��ÿһ�������䵽�ľ���id��������ӵ���Ӧ�ľ������
		for (int i = 0; i < assignment.length; i++) {
			clusterGroup[assignment[i]].add(totalInsts.instance(i));
		}
		//ͳ��ÿһ��������еĸ������ı�ǩ������Ǿ����Ϊ��Ӧ�ı�ǩ
		for (int i = 0; i < clusterGroup.length; i++) {
			//�������ȱʧ��ǩ������Ŀ
			int unlabelcount = clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).missingCount;
			if(unlabelcount == clusterGroup[i].numInstances())	//����������е�����ȱʧ��ǩ
			{
				clusterClass[i] = -1;	//���þ����idΪ-1
				clusterLabel[i] = "unknown";	//���þ���ر�ǩΪunknown
			}
			else{	//���������е���û��ȫ��ȱʧ��ǩ
				//�������id�ͱ�ǩ����Ϊ������и������ı�ǩ��
				clusterClass[i] = Utils.maxIndex(clusterGroup[i].attributeStats(totalInsts.numAttributes()-1).nominalCounts);
				clusterLabel[i] = totalInsts.attribute(totalInsts.numAttributes()-1).value(clusterClass[i]);
			}
		}

		//���ÿһ������ص�������
		
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
		
		
		//�ϲ�������ͬ��ǩ�ľ���ص�id�б�
		ArrayList<Integer> clusterClassList = new ArrayList<Integer>();
		//�ϲ�������ͬ��ǩ�ľ���صı�ǩ�б�
		ArrayList<String> clusterLabelList = new ArrayList<String>();
		//�ϲ�������ͬ��ǩ�ľ���ص�Instances�б�
		ArrayList<Instances> clusterInstList = new ArrayList<Instances>();
		//�ϲ�������ͬ��ǩ�ľ����
		for (int i = 0; i < clusterClass.length; i++) {
			if(!clusterClassList.contains(clusterClass[i])){//id�б��в����е�ǰ����ص�id
				clusterClassList.add(clusterClass[i]);	//��ӵ�id�б�
				clusterLabelList.add(clusterLabel[i]);	//��ӵ���ǩ�б�
				clusterInstList.add(clusterGroup[i]);	//��ӵ�Instances�б�
			}
			else{	//id�б����Ѻ��е�ǰ�����id
				int tmpInt = clusterClassList.indexOf(clusterClass[i]);	//�ҵ���Ӧ��ǩid���б��е�λ��
				//�ϲ���ǰ��������Ӧλ�õ�Instances�ϲ�
				Instances tmpInst = this.combineInstances(clusterInstList.get(tmpInt), clusterGroup[i]);
				//�滻��Ӧλ�õ�Instances
				clusterInstList.set(tmpInt, tmpInst);
			}
			
		} 
		
		//�������غϲ�֮��Ľ��
		System.out.println("*****************************");		
		int numKnown = 0;	//������֪��ǩ�ľ���ذ�������������Ŀ
		for (int i = 0; i < clusterClassList.size(); i++) {
			if(clusterLabelList.get(i).equals("unknown"))
			{
				//ѵ��������Ŀ��ȥδ֪����ؼ����е���������Ŀ
				numKnown = totalInsts.numInstances() - clusterInstList.get(i).numInstances();	
			}
			//�������غϲ�֮��Ľ��
			System.out.println("cluster"+i+"->"+clusterLabelList.get(i)+"("+clusterInstList.get(i).numInstances()+")");
		}
		//��ȷʶ���������Ŀ
		int correctFlows = 0;
		//������ȷʶ���������Ŀ
		for (int i = 0; i < clusterInstList.size(); i++) {
			Instances tmp =  clusterInstList.get(i);
			int itmp = clusterClassList.get(i);
			for (int j = 0; j < tmp.numInstances(); j++) {
				//�õ�ÿһ������ԭʼ��ǩid
				double dtmp = tmp.instance(j).value(totalInsts.numAttributes()-2);	
				if(itmp == -1)	
				{
					if(Double.isNaN(dtmp)){	//��ȷʶ��δ֪��
						correctFlows++;
					}
				}
				else
				{
					int idtmp = (int)dtmp;
					if(itmp == idtmp){	//��ȷʶ���Ӧ��ǩ��
						correctFlows++;
					}
				}
			}
		}
		
		System.out.println("the errors of squared:"+squaredError);
		
		System.out.println("Propagation Rate:"+ numKnown/(double)numPreLabelled);
		
		System.out.println("Training Purity:"+correctFlows/(double)totalInsts.numInstances());
		
	}
	
	
	//����ʶ�𵥸�������
	public int classifyFlow(Instance inst)
	{
		double minDist = Integer.MAX_VALUE;	//ÿһ��������������ĵ����С����
		int bestCluster = 0;	//����ʶ��ı�ǩid
		for (int i = 0; i < clusterCentroids.numInstances(); i++) {
			
			 double dist = distanceFunction.distance(inst, clusterCentroids.instance(i));
			 //�ҵ���С�����Ӧ�ı�ǩid
		     if (dist < minDist) {
		    	 minDist = dist;
		    	 bestCluster = i;
		     }
		}
		return bestCluster;
	}
	
	//�÷������Բ������ݼ����з��࣬���۷������ķ�������
	public void evaluateClassifier(Instances insts){
		//�������ݼ���ȱʧ��ǩ��������Ŀ
		int numTestUnknowFlows = insts.attributeStats(insts.numAttributes()-1).missingCount;
		//�������ݼ�����֪��ǩ��������Ŀ
		int numTestKnowFlows = insts.numInstances() - numTestUnknowFlows;
		//����������ݼ��ı�ǩ����
		Attribute classAttr = insts.attribute(insts.numAttributes()-1);
		//�������ݼ��ı�ǩ���Ե�������Ŀ
		int numofAttrVal = classAttr.numValues();
		//ÿһ������ı�ǩ�ķ���ʶ�����飬����unknown
		int[][] preNum = new int[numofAttrVal+1][numofAttrVal+1];
		//����ָ��
		String[] rateName = {"TP Rate","FP Rate","Precision","Recall","F-Measure"};
		//ÿһ�ֱ�ǩ�ķ�������ָ������
		double[][] allRate = new double[numofAttrVal+1][rateName.length];
		//����������ݼ��ı�ǩ����ֵ
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
		//ɾ���������ݼ��ı�ǩ����
		insts.deleteAttributeAt(insts.numAttributes()-1);
		//ÿһ��Ԥ��ı�ǩ����ֵ
		FastVector preClassVal = new FastVector(insts.numInstances());
		//��ÿһ��������ʶ�𣬱���ʶ��ı�ǩֽ
		for (int i = 0; i < insts.numInstances(); i++) {
			preClassVal.addElement((double)clusterClass[this.classifyFlow(insts.instance(i))]);
		}
		int numOfAccuracy = 0;	//��ȷʶ���������Ŀ
		//��Ԥ���ǩ����ֵ��ԭʼ�ı�ǩ����ֵ���жԱȣ������ͬ��ȷʶ�������Ŀ��1
		for (int i = 0; i < insts.numInstances(); i++) {
			if((double)classVal.elementAt(i)==-1.0){	//δ֪��
				if((double)(preClassVal.elementAt(i))==-1.0){	//δ֪����ȷʶ��
					preNum[numofAttrVal][numofAttrVal]++;
				}else{	//δ֪������ʶ��
					preNum[numofAttrVal][(int)(double)(preClassVal.elementAt(i))]++;
				}
			}
			else{	//��֪��ǩ����
				if((double)(preClassVal.elementAt(i))==-1.0){	//��֪��ǩ��ʶ��Ϊδ֪��
					preNum[(int)(double)(classVal.elementAt(i))][numofAttrVal]++;
				}else{	//��֪��ǩ��ʶ��Ϊ��֪��ǩ����������ȷʶ��
					preNum[(int)(double)(classVal.elementAt(i))][(int)(double)(preClassVal.elementAt(i))]++;
				}
			}
			//��ȷʶ�����ȷʶ����Ŀ��1
			if((double)(classVal.elementAt(i))==(double)(preClassVal.elementAt(i)))
			{
				numOfAccuracy++;
			}
		}
				
		//ÿһ�����ķ���ʶ���ת������
		int[][] rotatePreNum = new int[numofAttrVal+1][numofAttrVal+1];
		//��ת�����鸳ֵ
		for (int i = 0; i < rotatePreNum.length; i++) {
			for (int k = 0; k < rotatePreNum[0].length; k++) {
				rotatePreNum[i][k] = preNum[k][i];
			}
		}
		//��ȷʶ���δ֪������Ŀ
		int numAccPreUnkonw = preNum[numofAttrVal][numofAttrVal];
		//��֪��ǩ����ʶ��Ϊδ֪������Ŀ
		int numKnownPreUnknown = Utils.sum(rotatePreNum[numofAttrVal]) - numAccPreUnkonw;
		
		System.out.println("False Detection Rate:" + numKnownPreUnknown/(double)numTestKnowFlows);
		System.out.println("True Detection Rate:" + 
				((numTestUnknowFlows==0) ? 0 : numAccPreUnkonw/(double)numTestUnknowFlows));
		//�����������ָ��
		for (int i = 0; i < allRate.length; i++) {
			int tp = preNum[i][i];	//TPֵ
			int fn = Utils.sum(preNum[i])-tp;	//FNֵ
			int fp = Utils.sum(rotatePreNum[i])-tp;	//FPֵ
			int tn = insts.numInstances()-(tp+fn+fp);	//TNֵ
			
			allRate[i][0] = ((tp+fn)==0) ? 0 : (tp/(double)(tp+fn));	//TP Rate
			allRate[i][1] = ((fp+tn)==0) ? 0 : (fp/(double)(fp+tn));	//FP Rate
			allRate[i][2] = ((fp+tn)==0) ? 0 : (tp/(double)(tp+fp));	//Precision
			allRate[i][3] = allRate[i][0];	//Recall
			allRate[i][4] = ((2*tp+fp+fn)==0) ? 0 : (2*tp/(double)(2*tp+fp+fn));	//F-Measure
		}
		
		System.out.println("=== Detailed Accuracy By Class ===");
		//���ÿһ�ֱ�ǩ�ĸ�������ָ��
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
		//���ÿһ�б�ǩ�ķ������
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
