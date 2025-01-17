package ranker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.GlobalGraph;
import graph.QuestionGraph;
import graph.SentenceGraph;
import params.Parameters;

public class Ranker {
	private Parameters parameters;
	
	public Ranker(Parameters parameters){
		setParameters(parameters);
	}

	public Ranker(Map<String, Double> parameters){
		this.parameters = new Parameters(parameters);
	}

	public RankResult rankSentences(QuestionGraph qGraph, GlobalGraph gGraph){
		Map<Double, List<SentenceGraph>> rankedGraphs = new HashMap<Double, List<SentenceGraph>>();
		List<Double> sortedKeys = new ArrayList<Double>();
		
		for(SentenceGraph sGraph : gGraph.getSentences()){
			double score = calculateSimilarityScore(qGraph, sGraph); 
			
			if(!rankedGraphs.containsKey(score)){
				rankedGraphs.put(score, new ArrayList<SentenceGraph>());
			}
			
			rankedGraphs.get(score).add(sGraph);
		}
		
		sortedKeys.addAll(rankedGraphs.keySet());
		Collections.sort(sortedKeys);
		
		return new RankResult(sortedKeys, rankedGraphs);
	}
	
	public double calculateSimilarityScore(QuestionGraph qGraph, SentenceGraph sGraph){
			double score = 0;
			
			if(sGraph.containsSubclass(qGraph.getAnswerType())){
				score -= parameters.get("qaAnswerTypeFound");
			}
			else{
				score += parameters.get("qaAnswerTypeNotFound");
			}

			score += qGraph.calculateSimilarityScore(sGraph, parameters.getParameters());
			
			return score;
	}

	public static int getRank(String sentence, RankResult result){
		int rank = 0;
		
		for(Double key : result.getSortedKeys()){
			List<SentenceGraph> sGraphList = result.getRankedGraphs().get(key);
			
			for(SentenceGraph oGraph : sGraphList){
				if(oGraph.getSentence().equals(sentence)){
					rank += sGraphList.size() - 1;
					return rank;
				}
			}
			
			rank += sGraphList.size();
		}

		return -1;	
	}
	
	public static int getRank(SentenceGraph sGraph, RankResult result){
		int rank = 0;
		
		for(Double key : result.getSortedKeys()){
			List<SentenceGraph> sGraphList = result.getRankedGraphs().get(key);
			
			for(SentenceGraph oGraph : sGraphList){
				if(sGraph.equals(oGraph)){
					rank += sGraphList.size() - 1;
					return rank;
				}
			}
			
			rank += sGraphList.size();
		}

		return -1;	
	}
	
	public static double getScore(String sentence, RankResult result){
		for(Double key : result.getSortedKeys()){
			for(SentenceGraph sGraph : result.getRankedGraphs().get(key)){
				if(sGraph.getSentence().equals(sentence)){
					return key;
				}
			}
		}

		return -1;
	}

	//Getters & Setters
	
	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters tempParams) {
		this.parameters = tempParams;
	}
	
	public void setParameter(String key, Double value){
		parameters.setParameter(key, value);
	}
}
