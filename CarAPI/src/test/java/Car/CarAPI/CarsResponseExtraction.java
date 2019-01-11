package Car.CarAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class CarsResponseExtraction {

	/*return response as string*/
	public String extractResponse(){
	String responseString=given()
				.relaxedHTTPSValidation()
			     .when()
				.get("https://carsales.com/enterprise")
			     .then()
				.extract().response().asString();
	return responseString;
	}
	
	
	/*Print all the blue Teslas  in the response and also print the notes*/
	@Test
	public void blueTesla() throws IOException {
		/*store return response in a string*/			
		String result = extractResponse();
		/* convert response string to json*/
		JsonPath jsonResult=new JsonPath(result);
		/*get list of cars from the response*/
	    int count = jsonResult.get("car.size");
	    /*traverse through the loop and check model = tesla and color = blue and print model and notes*/
		for(int i=0; i < count;i++){
			if(  (jsonResult.get("Car["+i+"].make").equals("Tesla"))  &&  (jsonResult.get("Car["+i+"].metadata.Color").equals("Blue")  )){
				System.out.println("Car Model: "+jsonResult.get("Car["+i+"].make"));
				System.out.println("Car Color: "+jsonResult.get("Car["+i+"].metadata.Notes"));
	}
					
	}

}
	/*Question number 2*/	
	/*price only*/
	@Test
	public void priceOnly(){
		/*store return response in a string*/			
		String result = extractResponse();
		/* convert response string result to json*/
		JsonPath jsonResult=new JsonPath(result);
		/*find the car with minimum price*/
		int minimumPrice = jsonResult.get("car.perdayrent.price.min()");
		/*print car details of the minimum priced car*/
		System.out.println("Details of Car whose rent is lowest : "+ jsonResult.get("car.findAll  {it-> it.perdayrent.price == "+minimumPrice+"}"));
	}
	/*Reusable method*/
	public List<Float> discountPrice(JsonPath jsonResult){
		/*get list of price and store it in listOfPrice*/
		List<Float> listOfPrice= jsonResult.get("car.perdayrent.Price");
		/*get list of discount and store it in listOfDiscount*/
		List<Float> listOfDiscount = jsonResult.get("car.perdayrent.Discount");
		/*create arraylist to store price with discount*/
		List<Float> listOfPriceWithDiscount = new ArrayList();
		/*Minimum value after reducing discount% */
		for(int i = 0;i<listOfPrice.size();i++)
		{
			listOfPriceWithDiscount.add(listOfPrice.get(i)-listOfPrice.get(i) * listOfPriceWithDiscount.get(i)/100);
		}
		return listOfPriceWithDiscount;
	}
	
	/*price after discount*/
	@Test
	public void priceAfterDiscount(){
		/*store return response in a string*/			
		String result = extractResponse();
		/*call reusable method discountPrice and store it in a list*/
		List<Float> listOfPriceWithDiscount = new ArrayList();
		/* convert response string to json*/
		JsonPath jsonResult=new JsonPath(result);
		listOfPriceWithDiscount = discountPrice(jsonResult);
		/*index of min price with discount*/
		int minimumPriceIndex = listOfPriceWithDiscount.indexOf(Collections.min(listOfPriceWithDiscount));
		/*print car details with minimum price with discount*/
		System.out.println(jsonResult.get("Car[minimumPriceIndex]"));
	}
	
	/*Question number 3*/
	@Test
	public List<Float> highestRevenueCar(){
		/*store return response in a string*/			
		String result = extractResponse();
		/*call reusable method discountPrice and store it in a list*/
		List<Float> listOfPriceWithDiscount = new ArrayList();
		JsonPath jsonResult=new JsonPath(result);
		listOfPriceWithDiscount = discountPrice(jsonResult);

		/* convert response string to json*/
		jsonResult=new JsonPath(result);
		/*get list of price and store it in listOfPrice*/
		List<Float> listOfPrice= jsonResult.get("car.perdayrent.Price");
		/*get list of discount and store it in listOfDiscount*/
		
		List<Float> listOfDiscount = jsonResult.get("car.perdayrent.Discount");
		
		/*find the main value after applying discount*/
		for(int i = 0;i<listOfPrice.size();i++)
		{
			listOfPriceWithDiscount.add(listOfPrice.get(i) - (listOfPrice.get(i) *listOfPriceWithDiscount.get(i))/100);
		}
		return listOfPriceWithDiscount;
		}
	
	/*Calculate highest profit car*/
	@Test
	public void highestProfitCar(){
		/*store return response in a string*/			
		String res = extractResponse();
	/* convert response string to json*/
		JsonPath jsonResult=new JsonPath(res);
		
		List<Float> listOfPrice = jsonResult.get("car.perdayrent.Price");
		List<Float> listOfDiscount = jsonResult.get("car.perdayrent.Discount");
		List<Float> listOfMaintenenceCost = jsonResult.get("car.metrics.yoymaintenancecost");
		List<Float> listOfDepreciation = jsonResult.get("car.perdayrent.metrics.depreciation");
		List<Float> listOfPriceWithDiscount = new ArrayList();
		/*get list of rental count year to date*/
		List<Integer> listOfRentalCount = jsonResult.get("car.metrics.rentalcount.yeartodate");
		List<Float> listOfProfit = new ArrayList();
		
		/*add values to listPriceWithDiscount after applying price -(price*discount)/100*/
		for(int i = 0;i<listOfPrice.size();i++)
		{
		listOfPriceWithDiscount.add(listOfPrice.get(i) - (listOfPrice.get(i)*listOfPriceWithDiscount.get(i))/100);
		/*add values to listOfProfit after applying (pricewithdiscount * rentalcount) -(maintenane=ceCost+depreciation)*/
		for(i =0;i<listOfPrice.size();i++)
		{
			listOfProfit.add(listOfPriceWithDiscount.get(i)*listOfRentalCount.get(i) - (listOfMaintenenceCost.get(i)+listOfDepreciation.get(i)));
		}
		
		/*find highest revenue car index*/
		int index = listOfProfit.indexOf(Collections.max(listOfProfit));
		/*print highest car revenue*/
		System.out.println(jsonResult.get("Car[index]"));
		}
	}
				
}