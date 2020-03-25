package Tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HW4 {
    protected static WebDriver driver;
    @BeforeTest
    public void setUp(){
        if(driver==null){
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
    }
    @AfterTest
    public void done(){
        if(driver!=null){
            driver.quit();
            driver=null;
        }
    }

    @Test
    public void days(){
        driver.get("http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCheckBox");
        List<WebElement> l1 = driver.findElements(By.cssSelector(".gwt-CheckBox>label"));
        int count = 0;
        Random r = new Random();

        while(count<3){
            int index = r.nextInt(l1.size());
            if(l1.get(index).isEnabled()){
                l1.get(index).click();
                if(l1.get(index).getText().equals("Friday")){
                    count++;
                }
                System.out.println(l1.get(index).getText());
                l1.get(index).click();
            }
        }

    }

    @Test
    public void todays_date(){
        driver.get("http://practice.cybertekschool.com/dropdown");
        WebElement year = driver.findElement(By.id("year"));
        WebElement month = driver.findElement(By.id("month"));
        WebElement day = driver.findElement(By.id("day"));
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMMMdd");

        Select y =new Select(year);
        Select m = new Select(month);
        Select d = new Select(day);
        String y1 = y.getFirstSelectedOption().getText();
        String m1 = m.getFirstSelectedOption().getText();
        String d1 = d.getFirstSelectedOption().getText();
        Assert.assertEquals(y1+m1+d1,  sf.format(new Date()));

    }

    @Test
    public void years_months_days(){
        driver.get("http://practice.cybertekschool.com/dropdown");
        WebElement year = driver.findElement(By.id("year"));
        WebElement month = driver.findElement(By.id("month"));
        WebElement day = driver.findElement(By.id("day"));
        Select y =new Select(year);
        Select m = new Select(month);
        Select d = new Select(day);
        y.selectByIndex(new Random().nextInt(y.getOptions().size()));
        List<String> months31= new ArrayList<>(Arrays.asList(new String[]{"January", "March", "May", "July", "August", "October", "December"}));
        int febDays;
       febDays =  Integer.parseInt(y.getFirstSelectedOption().getText())%4==0?29:28;

        for(int i =0 ; i<12; i++){
            m.selectByIndex(i);
            if(months31.contains(m.getFirstSelectedOption().getText())) {
                Assert.assertEquals(d.getOptions().size(), 31);
            }else if(m.getFirstSelectedOption().getText().equals("February")){
                Assert.assertEquals(d.getOptions().size(), febDays);
            }else{
                Assert.assertEquals(d.getOptions().size(), 30);
            }


        }

    }

    @Test
    public void department_sort(){
        driver.get("https://www.amazon.com");
        Assert.assertEquals(driver.findElement(By.xpath("//*[text()='All']")).getText(), "All");

        driver.findElement(By.id("searchDropdownBox")).click();

        List<WebElement> l1 =new Select(driver.findElement(By.id("searchDropdownBox"))).getOptions();
        boolean notAlphOrder = false;
        for( int i =0; i<l1.size()-1; i++){
            if(l1.get(i).getText().compareTo(l1.get(i+1).getText())>0){
                notAlphOrder=true;
                break;
            }
        }
        Assert.assertTrue(notAlphOrder);

    }
    @Test
    public void main_departments(){
        driver.get("https://www.amazon.com/gp/site-directory");
        List<WebElement> mainDep = driver.findElements(By.tagName("h2"));
        driver.findElement(By.id("searchDropdownBox")).click();
        List<WebElement> allDep =new Select(driver.findElement(By.id("searchDropdownBox"))).getOptions();
        Set<String > mainDepS = new HashSet<String>();
        Set<String > allDepS = new HashSet<String>();
        for(WebElement each : mainDep){
            mainDepS.add(each.getText());
        }
        for(WebElement each : allDep){
            allDepS.add(each.getText());
        }
        //Most all of them out of all dep list
        for(String each : mainDepS){
            if(!allDepS.contains(each)){
                System.out.println(each);
                System.out.println("This main dep is not in All depattments list");
            }
        }
        Assert.assertTrue(allDepS.containsAll(mainDepS));
    }

    @Test
    public void links(){
        driver.get("https://www.w3schools.com/");
        List<WebElement> l1 = driver.findElements(By.tagName("a"));
        for(WebElement each: l1){
            if(each.isDisplayed()){
                System.out.println(each.getText());
                System.out.println(each.getAttribute("href"));
            }
        }


    }

    @Test
    public void valid_links() {
        driver.get("https://www.selenium.dev/documentation/en/");
        List<WebElement> links = driver.findElements(By.tagName("a"));

        for(int i=0; i<links.size(); i++){
            String href = links.get(i).getAttribute("href");
            try {
                URL url = new URL(href);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.connect();
                Assert.assertTrue(httpURLConnection.getResponseCode()==200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void cart(){
        driver.get("https://amazon.com");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("wooden spoon");
        driver.findElement(By.xpath("//span[@id='nav-search-submit-text']/following-sibling::input")).click();
        List<WebElement> price = driver.findElements(By.xpath("//span[@class='a-price']/span[@class='a-offscreen']"));

        int x = new Random().nextInt(price.size());
        x= x==0?1:x;
        System.out.println();
        String originName = driver.findElement(By.xpath("(//span[@class='a-size-base-plus a-color-base a-text-normal'])["+x+"]")).getText();
        String originPrice = "$" +
                driver.findElement(By.xpath("(//span[@class='a-price']/span[2]/span[2])["+x+"]")).getText() +"."+
                        driver.findElement(By.xpath("(//span[@class='a-price']/span[2]/span[3])["+x+"]")).getText();



        driver.findElement(By.xpath("(//span[@class='a-price-fraction'])["+x+"]")).click();

        Assert.assertEquals(driver.findElement(By.xpath("//span[text()='Qty:']/following-sibling::span")).getText(), "1");
        Assert.assertEquals(driver.findElement(By.id("productTitle")).getText(), originName);
        Assert.assertEquals(driver.findElement(By.id("price_inside_buybox")).getText(), originPrice);
        Assert.assertTrue(driver.findElement(By.id("add-to-cart-button")).isDisplayed());

    }


    @Test
    public void prime(){

        driver.get("https://amazon.com");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("wooden spoons");
        driver.findElement(By.xpath("//span[@id='nav-search-submit-text']/following-sibling::input")).click();
        WebElement firstPrimename = driver.findElement(By.xpath("(//i[@aria-label='Amazon Prime']/../../../../../..//h2)[1]"));
        String name1 = firstPrimename.getText();
        driver.findElement(By.xpath("//i[@class='a-icon a-icon-prime a-icon-medium']/../div/label/i")).click();
        String name2 = driver.findElement(By.xpath("(//i[@aria-label='Amazon Prime']/../../../../../..//h2)[1]")).getText();
        Assert.assertEquals(name2, name1);

        driver.findElement(By.xpath("//div[@id='brandsRefinements']//ul/li[last()]//i")).click();

        String name3 = driver.findElement(By.xpath("(//i[@aria-label='Amazon Prime']/../../../../../..//h2)[1]")).getText();
        System.out.println(name1);
        System.out.println(name2);
        System.out.println(name3);
        Assert.assertNotEquals(name1, name3);


    }

    @Test
    public void more_spoons(){
        driver.get("https://amazon.com");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("wooden spoons");
        driver.findElement(By.xpath("//span[@id='nav-search-submit-text']/following-sibling::input")).click();
        List<WebElement> l1 = driver.findElements(By.xpath("//div[@id='brandsRefinements']//ul/li/span/a/span"));
        List<String > s1 = new ArrayList<>();
        for(WebElement each : l1){
            s1.add(each.getText());
        }
        driver.findElement(By.xpath("//i[@class='a-icon a-icon-prime a-icon-medium']/../div/label/i")).click();
        List<WebElement> l2 = driver.findElements(By.xpath("//div[@id='brandsRefinements']//ul/li/span/a/span"));
        List<String > s2 = new ArrayList<>();
        for(WebElement each : l2){
            s2.add(each.getText());
        }
        Assert.assertEquals(s1,s2);
    }

    @Test
    public void cheap_spoons(){
       // NO Price option on amazon website on the left after search
    }
}
