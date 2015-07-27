package com.touchKin.touchkinapp.model;

public class TravelItem {
	
	private String title;
	private String destination;
	private String departure;
	private String date;
	private String time;
	
	public TravelItem(){
		this.title = null;
		this.destination = null;
		this.departure = null;
		this.date = null;
		this.time = null;
	}
	
	public TravelItem(String title, String dest, String dep, String date, String time){
		this.title = title;
		this.destination = dest;
		this.departure = dep;
		this.date = date;
		this.time = time;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getDestination(){
		return this.destination;
	}
	
	public void setDestination(String dest){
		this.destination = dest;
	}
	
	public String getDeparture(){
		return this.departure;
	}
	
	public void setDeparture(String dep){
		this.departure = dep;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setTime(String time){
		this.time = time;
	}
}