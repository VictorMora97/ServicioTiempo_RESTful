import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.ws.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.json.*;
import javax.json.*;

public class Cliente {
	public static void main(String[] args) throws JSONException {
  
        Scanner sc = new Scanner(System.in);
       
        System.out.println("Introduzca el nombre de la ciudad a la que quiere viajar: \n");           
        String ciudad = sc.nextLine();
		 
        try {
        	
        String WOEID = getWOEID(ciudad); 
        
        Client client = ClientBuilder.newClient( new ClientConfig());
        WebTarget target = client.target(getWeatherURI(WOEID));
        
        String jsonResponse = target.path("api").path("location").path(WOEID).request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        
        System.out.println("\n" + getRecomendacion(jsonResponse) );
        
        postRecomendacion(getRecomendacion(jsonResponse),WOEID);
        getRecomendaciones("766273");
        
        } catch (Exception e) {
        	System.out.println("\nCIUDAD NO VALIDA O MAL ESCRITA\n"+e);
        }

	}		
	
	/* ----- Construccion URIs ----- */
	
	private static URI getSearchURI(String ciudad) {
        return UriBuilder.fromUri(
                "https://www.metaweather.com/api/location/search/?query="+ciudad).build();
    }
  
    private static URI getWeatherURI(String WOEID) {
        return UriBuilder.fromUri(
                "https://www.metaweather.com/api/location/"+WOEID).build();
    }
     
    private static URI getStoreURI() {
        return UriBuilder.fromUri(
                "http://localhost:8080/Tiempo_REST_Server/tiempoREST/recurso/json").build();
    }
       
    
    /* ----- Para la parte cliente (Parte 1) ----- */
    
    public static String getRecomendacion (String JSON) throws JSONException {
    	   	
    	 JSONObject obj = new JSONObject(JSON);
         JSONArray arr = obj.getJSONArray("consolidated_weather");
         String[] abreviaturasTiempo = {"","","","","",""};
         int[] tempSMax = {0,0,0,0,0,0};
         int[] tempSMin = {0,0,0,0,0,0};
         
         for (int i = 0; i < arr.length(); i++) {
             String abbr = arr.getJSONObject(i).getString("weather_state_abbr");
             int tempMax = arr.getJSONObject(i).getInt("max_temp");
             int tempMin = arr.getJSONObject(i).getInt("min_temp");
             abreviaturasTiempo[i]= abbr;
             tempSMax[i]= tempMax;
             tempSMin[i]= tempMin;
         }

    	int diasDeLLuvia = analizarTiempo(abreviaturasTiempo);
    	int temperaturaMedia = mediaTemperatura(tempSMin,tempSMax);
        String recomendacion;
        
        if (diasDeLLuvia<2) {
        	if (temperaturaMedia >= 18) {
        		recomendacion = "Puedes viajar sin problema, lloverá "+diasDeLLuvia+" dias. Puedes llevar manga corta, la temperatura media será de "+temperaturaMedia+" ºC";
        	} else {
        		recomendacion = "Puedes viajar sin problema, lloverá "+diasDeLLuvia+" dias. Lleva ropa larga, la temperatura media será de "+temperaturaMedia+" ºC";
        	}
        } else {
        	recomendacion = "No deberias viajar, lloverá "+diasDeLLuvia+" dias";
        }
             
		return recomendacion; 	
    }  
    
    public static int mediaTemperatura (int[] min, int[] max) {
    	int auxMax = 0;
    	int auxMin = 0;
    	for (int i = 0; i < 6; i++) {
    		auxMax = auxMax + max[i];
    		auxMin = auxMin + min[i];  		
    	}
    	int mediaMax  = auxMax/6;
    	int mediaMin = auxMin/6;
    	int mediaGlobal = (mediaMax + mediaMin)/2;
    	return mediaGlobal;	
    }
    
    public static int analizarTiempo (String[] abreviaturas) {
    	
    	int diasLLuvia = 0;
    	
    	for (int i = 0; i < 6; i++) { 

    		if (abreviaturas[i].equals("sl")) {
    			diasLLuvia++;
    		} else if(abreviaturas[i].equals("h")) {
    			diasLLuvia++;
    		} else if(abreviaturas[i].equals("hr")) {
    			diasLLuvia++;
    		} else if(abreviaturas[i].equals("lr")) {
    			diasLLuvia++;
    		} else if(abreviaturas[i].equals("s")) {
    			diasLLuvia++;
    		}    		
    	}
    	return diasLLuvia;   	
    }
    
    public static String getWOEID (String ciudad) throws JSONException { 	  
    	
    	Client client = ClientBuilder.newClient( new ClientConfig());
        WebTarget target = client.target(getSearchURI(ciudad));
    
        String jsonResponse = target.path("api").path("location").path("search").request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        
        String[] jsonSinCorchete = jsonResponse.split(Pattern.quote("["));
        String JSON = jsonSinCorchete[1];                      
        JSONObject obj = new JSONObject(JSON);  
        
        return obj.getString("woeid");
    }   
    
    /* ----- Para la parte de mi servicio (Parte 2) ----- */
    
    public static void postRecomendacion(String recomendacion, String WOEID) throws JSONException, UnsupportedEncodingException {

    	JSONObject jsonPOST = new JSONObject();
    	jsonPOST.put("fecha", getFecha());
    	jsonPOST.put("recomendacion", recomendacion);
    	jsonPOST.put("WOEID", WOEID);
    	
    	String url = url(getFecha(),recomendacion,WOEID);
    	Client client = ClientBuilder.newClient( new ClientConfig());
        WebTarget target = client.target(url);  

        try {
        	
        	String response = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.json(jsonPOST), String.class);
            //System.out.println(response);
            
        } catch (Exception e) {
        	System.out.println("ERROR CON POST "+e);
        }  	
    }
    
    public static void getRecomendaciones(String WOEID) throws UnsupportedEncodingException {
    	
    	String url = urlGET(WOEID);
    	Client client = ClientBuilder.newClient( new ClientConfig());
        WebTarget target = client.target(url);
        
        String ListaResponse = target.request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);
    	
    	System.out.println(ListaResponse);
    }

    public static String getFecha() {
     	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();  
        String fecha = formatter.format(date); 
    	return fecha;
    }
    
    public static String url(String fecha, String recomendacion, String WOEID) throws UnsupportedEncodingException {
    	String fechaJson = URLEncoder.encode(getFecha(), "UTF-8");
    	String recomendacionJson = URLEncoder.encode(recomendacion, "UTF-8");
    	String WOEIDJson = URLEncoder.encode(WOEID, "UTF-8");
    	String url=getStoreURI()+"?fecha="+fechaJson+"&recomendacion="+recomendacionJson+"&WOEID="+WOEIDJson;
    	return url;
    }
    
    public static String urlGET(String WOEID) throws UnsupportedEncodingException {
    	String WOEIDJson = URLEncoder.encode(WOEID, "UTF-8");
    	String url=getStoreURI()+"?WOEID="+WOEIDJson;
    	return url;
    }
    
}