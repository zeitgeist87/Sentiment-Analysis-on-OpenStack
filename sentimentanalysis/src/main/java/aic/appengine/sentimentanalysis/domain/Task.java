package aic.appengine.sentimentanalysis.domain;

public class Task {

	private String name;
	private String query;
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String query){
		this.query = query;
	}
}