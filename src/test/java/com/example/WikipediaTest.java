package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.extractProperty;

public class WikipediaTest {

    private WebDriver driver;   //interfejs umożliwiający sterowanie przeglądarką
    private WebDriverWait wait; //mechanizm czekający na określony stan strony

    private static final String BASE_URL = "https://pl.wikipedia.org/wiki/Wikipedia:Strona_g%C5%82%C3%B3wna"; //adres testowanej strony
    private static final By SEARCH_BAR = By.cssSelector("input.cdx-text-input__input"); //wyszukiwarka
    private static final By TITLE_NAME = By.cssSelector(".mw-page-title-main"); //tytuł artykułu
    private static final By SUGGESTIONS = By.cssSelector(".cdx-typeahead-search-menu a.cdx-menu-item"); //wyswietlane sugestie
    private static final By CARLOS_ALCARAZ_LINK = By.cssSelector("a[title=\"Carlos Alcaraz\"]");
    private static final By STRONA_GLOWNA = By.cssSelector("a[title=\"Przejdź na stronę główną [alt-z]\"]");
    private static final By BIBLIOGRAFIA_PRZYPISY = By.cssSelector(".vector-toc-text");
    private static final By HEADER = By.cssSelector(".mw-page-title-main");
    private static final By MAIN_MENU = By.id("vector-main-menu-dropdown");
    private static final By CONTENTS = By.cssSelector(".vector-toc");

    @BeforeEach
    void setUp(){
        WebDriverManager.chromedriver().setup(); //ustawia nowy driver, jeśli nie istnieje

        ChromeOptions options = new ChromeOptions(); //pozwala na modyfikowanie i kontrolowanie przeglądarki podczas puszczania testów
        options.addArguments("--start-maximized"); //okno Chrome zostanie zmaksymalizowane
        driver = new ChromeDriver(options); //uruchomienie przeglądarki z podanymi wcześniej ustawieniami
        driver.get(BASE_URL); //otwiera w przeglądarce stronę podaną w BASE_URL

        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); //bez tego wait sie nie zainicjalizuje i wywalą się testy



    }

    @AfterEach
    void tearDown(){
        if(driver !=null){
            driver.quit(); //zamyka przeglądarkę
        }
    }

    @Test
    @DisplayName("Should find search bar visible")
    void shouldFindSearchBarVisible(){
        WebElement searchBar = driver.findElement(By.cssSelector("input.cdx-text-input__input"));

        assertThat(searchBar.isDisplayed()).isTrue(); // czy searchBar jest wyświetlony
    }

    @Test
    @DisplayName("Should redirect user to article")
    void shouldRedirectUserToArticle() {
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR)); //czekamy az element bedzie widoczny i załadowany

        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER);

        assertThat(driver.getCurrentUrl()).contains("Jannik");

    }

    @Test
    @DisplayName("Title should contain looked up phrase")
    void titleShouldContainLookedUpPhrase(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR)); //czekamy az element bedzie widoczny i załadowany
        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER); //wchodzimy na stronę jannik sinner

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_NAME)); //czekamy az pojawi się title name
        wait.until(ExpectedConditions.urlContains("Jannik_Sinner")); //aż w url będzie jannik sinner

        assertThat(title.getText()).containsIgnoringCase("jannik sinner"); //sprawdzamy czy strona jest tak zatytułowana

    }

    @Test
    @DisplayName("Should show recommended search results")
    void shouldShowRecommendedSearchResults(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR)); //czekamy az element bedzie widoczny i załadowany
        searchBar.sendKeys("Iga");

        List<WebElement> suggestions = driver.findElements(SUGGESTIONS); //lista sugestii

        assertThat(!suggestions.isEmpty());

    }

    @Test
    @DisplayName("Should change url if link in article clicked")
    void shouldChangeUrlIfLinkInArticleClicked(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR)); //czekamy az element bedzie widoczny i załadowany

        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("Jannik_Sinner")); //aż w url będzie jannik sinner

        String jannikUrl = driver.getCurrentUrl();
        WebElement alcarazLink = wait.until(ExpectedConditions.elementToBeClickable(CARLOS_ALCARAZ_LINK));

        alcarazLink.click();
        wait.until(ExpectedConditions.urlContains("Carlos"));

        assertThat(!Objects.equals(driver.getCurrentUrl(), jannikUrl));
    }

    @Test
    @DisplayName("Should check Strona Główna link in side menu")
    void shouldCheckStronaGlownaLinkInSideMenu(){

        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(MAIN_MENU));
        menu.click();


        WebElement stronaGlowna = wait.until(ExpectedConditions.elementToBeClickable(STRONA_GLOWNA));
        stronaGlowna.click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL));

        assertThat(Objects.equals(driver.getCurrentUrl(), BASE_URL));

    }

    @Test
    @DisplayName("Should verify existence of contents")
    void shouldVerifyExistenceOfContents(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR)); //czekamy az element bedzie widoczny i załadowany
        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("Jannik"));

        WebElement spisTresci = wait.until(ExpectedConditions.visibilityOfElementLocated(CONTENTS));

        assertThat(spisTresci.isDisplayed()).isTrue();

    }

    @Test
    @DisplayName("Article header should match searched phase")
    void articleNameShouldMatchSearchedPhase(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR));//czekamy az element bedzie widoczny i załadowany

        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("Jannik"));

        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER));
        assertThat(header.getText().equals("Jannik Sinner"));
    }

    @Test
    @DisplayName("Should check for images")
    void shouldCheckForImages(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        List<WebElement> images = driver.findElements(By.tagName("img"));

        assertThat(!images.isEmpty());
    }

    @Test
    @DisplayName("Should contain 'przypisy' or 'bibliografia'")
    void shouldContainPrzypisyOrBibliografia(){
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BAR));
        searchBar.sendKeys("Jannik Sinner" + Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("Jannik"));

        WebElement tested = wait.until(ExpectedConditions.visibilityOfElementLocated(BIBLIOGRAFIA_PRZYPISY));

        assertThat(tested.getText().contains("Przypisy") || tested.getText().contains("Bibliografia"));

    }






}
