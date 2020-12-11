package isi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/recurso")
public class Tiempo {	
	
	
	/*   http://localhost:8080/Tiempo_REST_Server/tiempoREST/recurso/json    */
	
	
	
    @GET
	@Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
 
    public String getList(@QueryParam("WOEID") String WOEID) throws IOException {
         return leerArchivo(WOEID);
    }
    
    
    
	@POST
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	
	public static Recomendacion guardarRecomendacion(
			@QueryParam("fecha") String fecha,
			@QueryParam("recomendacion") String recomendacion,
			@QueryParam("WOEID") String WOEID
			) throws IOException {
		
		Recomendacion reco = new Recomendacion();
		reco.setRecomendacion(recomendacion);
		reco.setFecha(fecha);
		reco.setWOEID(WOEID);	
		
		String path="C:\\Users\\victo\\txt\\reco.txt";
		String str = reco.toString();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
	    writer.append(str);
	    writer.close();

		return reco;
	
	}
	
	public static String leerArchivo(String woeid) throws IOException {
		
		String ciudad=woeid;
    	File f=new File("C:\\Users\\victo\\txt\\reco.txt");
    	BufferedReader freader = new BufferedReader(new FileReader(f));
    	String s;
    	freader.readLine();
    	String recuperada = "\nRecomendaciones anteriores para Madrid: \n";
    	while((s = freader.readLine()) != null) {
    		
	    	String[] st = s.split(Pattern.quote("+"));
	  		
	    	String woeid_lector = st[0];
	    	String fecha_lector = st[1];
	    	String recomendacion_lector = st[2];
	    	 	
	    	if(woeid_lector.equals(ciudad)){	
		    	recuperada = recuperada+"\n"+fecha_lector+" "+recomendacion_lector;
	    	}
	    	
    	}
    	return recuperada;
		
	}   
}