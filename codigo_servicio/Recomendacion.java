package isi;

public class Recomendacion {
	private String recomendacion;
	private String fecha;
	private String WOEID;
	
	
	
	public String getRecomendacion() {
		return recomendacion;
	}


	public void setRecomendacion(String recomendacion) {
		this.recomendacion = recomendacion;
	}


	public String getFecha() {
		return fecha;
	}


	public void setFecha(String fecha) {
		this.fecha = fecha;
	}


	public String getWOEID() {
		return WOEID;
	}


	public void setWOEID(String wOEID) {
		WOEID = wOEID;
	}


	public Recomendacion() {
		this.recomendacion = "Init";
		this.fecha = "";
		this.WOEID = "";
	}
	
	 @Override
	    public String toString() 
	    { 
	        return 
	             WOEID 
	            + "+"
	            + fecha 
	            + "+"
	            + recomendacion + "\n"; 
	    } 
	
}