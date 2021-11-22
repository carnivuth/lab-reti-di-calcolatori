package onlineRegistry;

import java.rmi.Remote;

public class Service {
	
	private String ServiceName;
	private Remote service;
	private String[] tags;
	
	public Service( Remote service) {
		
		this.service = service;
		this.tags=new String[256];
		
		for(int i=0;i<tags.length;i++) {
			
			tags[i]=null;
		}
	}
	
	
	public Service( Remote service, String[] tags) {
	
		this.service = service;
		this.tags = tags;
	}
	
	
	
	
	public String getServiceName() {
	
		return ServiceName;
	}
	
	public void setServiceName(String serviceName) {
	
		ServiceName = serviceName;
	}
	
	public Remote getService() {
	
		return service;
	}
	
	public void setService(Remote service) {
	
		this.service = service;
	}
	
	public String[] getTags() {
	
		return tags;
	}
	
	public void setTags(String[] tags) {
	
		this.tags = tags;
	}
	
	public void addTag(String tag) {
		
		for(int i=0;i<tags.length;i++) {
			
			if(tags[i]==null) {
				tags[i]=tag;
				return;
			};
		}
		
	}
	
	
	

}
