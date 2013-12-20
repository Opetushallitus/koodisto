package fi.vm.sade.koodisto.widget;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.bsb.common.vaadin.embed.support.EmbedVaadin;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Select;
import fi.vm.sade.support.selenium.SeleniumTestCaseSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Ignore
public class KoodistoComponentTest extends SeleniumTestCaseSupport {

    private EmbedVaadinServer server;
    private KoodistoComponent widget;
    private int httpPort;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        WidgetFactory widgetFactory = new WidgetFactory();
        widget = widgetFactory.createComponent("http://www.kunnat.fi/kunta");
        ensureDebugId(widget);
        
        server = EmbedVaadin.forComponent(widget).wait(false).start();
        httpPort = server.getConfig().getPort();
        
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        server.stop();
    }

    @Override
    public void initPageObjects() {
    }

    @Test
    public void testSingleSelect() {
        // FIXME: Test must be run with firefox driver
        if(!(driver instanceof FirefoxDriver)) {
            return;
        }
        
        Select select = new Select("My cool select");
        select.setImmediate(true);
        widget.setField(select);
        
        driver.get("http://localhost:" + httpPort);
        driver.findElement(By.cssSelector("div.v-filterselect-button")).click();
        driver.findElement(By.xpath("//td[contains(., '1000')]")).click();
        String value = (String) widget.getValue();//getInputField(widget).getAttribute("value");
        assertEquals("Koodiarvo 1000", value);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMultiSelect() {
        if(!(driver instanceof FirefoxDriver)) {
            return;
        }
        ListSelect select = new ListSelect("My multi select");
        select.setMultiSelect(true);
        select.setImmediate(true);
        widget.setField(select);
        
        driver.get("http://localhost:" + httpPort);
        driver.findElement(By.xpath("//select/option[1]")).click();
        driver.findElement(By.xpath("//select/option[3]")).click();
        driver.findElement(By.xpath("//select/option[5]")).click();
        Set<String> values = (Set<String>) widget.getValue();
        assertTrue(values.contains("Koodiarvo 1000"));
        assertTrue(values.contains("Koodiarvo 1002"));
        assertTrue(values.contains("Koodiarvo 1004"));
    }
    
    @Test
    public void testComboBox() throws InterruptedException {
        if(!(driver instanceof FirefoxDriver)) {
            return;
        }
        ComboBox comboBox = new ComboBox("My awesome comboBox");
        comboBox.setImmediate(true);
        comboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        widget.setField(comboBox);
        
        driver.get("http://localhost:" + httpPort);
        driver.findElement(By.cssSelector("div.v-filterselect-button")).click();
        // Auto complete
        driver.findElement(By.cssSelector("input.v-filterselect-input")).sendKeys("1007");
        new WebDriverWait(driver, 3, 1000).until(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> elemList = driver.findElements(By.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']//tr"));
                return elemList.size() == 1;
            }
        });
        
        List<WebElement> elemList = driver.findElements(By.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']//tr"));
        assertEquals(1, elemList.size());
        
        // Has to be first element on the list.
        driver.findElement(By.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']//tr[1]/td/span")).click();
        String value = (String) widget.getValue();
        assertEquals("Koodiarvo 1007", value);
    }
    
    private void ensureDebugId(Component... components) {
        for(Component component : components) {
            String debugId = component.getDebugId();
            if(debugId == null) {
                debugId = component.toString();
                component.setDebugId(debugId);
            }
        }
    }

}
