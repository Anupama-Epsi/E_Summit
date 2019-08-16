package com.es.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.testng.Assert;

import com.es.Utilities.DataGeneration;
import com.es.Utilities.DataProperty;
import com.es.Utilities.Excel;
import com.es.Utilities.Reporting;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Keywords extends BrowserConfig {

	public Reporting logger = new Reporting();
	public DataGeneration dataGenerate = new DataGeneration();
	public DataProperty dataProp = new DataProperty();
	/**
	 * public WebDriver getWebdriver() { return webDriver; }
	 */

	public void quitBrowser() {
		if(webDriver!=null) {
			webDriver.close();
		}
	}

	/**
	 * To switch to new window using its Title.
	 * @sValue - Title of new window.
	 */

	/**
	 *This method first clear existing values in the UI and then set  data based of input data and method to locator(input, checkbox, dropdown)
	 *@sLocator - Locator of an element from the excel.
	 *@sData -  String data. 
	 *@methodName - Name of the method. 
	 */
	public void clearAndSet(String sLocator, String sData, String methodName) {
		WebElement locator = getWebElement(sLocator);
		String Data = dataGenerate.randomDataGenerator(sData, methodName);
		try {
			if (locator != null) {
				String locatorAttributeType = locator.getAttribute("type");
				String locatorAttributeTag = locator.getTagName().trim();
				waitForPageToLoad(30);
				if (locatorAttributeType.contains("text") || locatorAttributeType.contains("password")
						|| locatorAttributeType.contains("tel")|| locatorAttributeType.contains("number")
						||locatorAttributeTag.equals("textarea")) {
					if (!isElementPresent(sLocator))
						scrollToView(sLocator);
					clear(sLocator);
					if (!verifyAndAddIntegerInput(sLocator, Data)) {
						locator.sendKeys(Data);
						logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
					}
				} else {
					String locatorAttributeRole = locator.getAttribute("role");
					if (locatorAttributeRole.contains("listbox")) {
						/*if(Data.equals(""))
						selectDropdownByLi(sLocator, " - Select an option -");
						else*/
						selectDropdownByLi(sLocator, Data);
						logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
					} else {
						String locatorAttributeCheck = locator.getAttribute("type");
						if (locatorAttributeCheck.contains("checkbox") && Data.contains("Y")) {
							scrollToView(sLocator);
							Thread.sleep(2000);
							javaScriptClick(locator);
						}
					}
				}
			}
		} catch (Exception e) {
			try {
				String locatorAttributeRole = locator.getAttribute("role");
				if (locatorAttributeRole.contains("listbox")) {
					if (!isElementPresent(sLocator))
						scrollToView(sLocator);
					selectDropdownByLi(sLocator, Data);
				}
			} catch (Exception e1) {
				try {
					String locatorAttributeCheck = locator.getAttribute("type");
					if (locatorAttributeCheck.contains("checkbox") && Data.contains("Y")) {
						scrollToView(sLocator);
						Thread.sleep(2000);
						click(locator);
					}
				} catch (Exception e2) {
					logger.logFail(
							"Given field locator was not of the type input, dropdown or checkbox. Please verify the locator");
				}
			}
		}
	}

	/**
	 *This method is used to clear the text entered or displayed in the text fields.
	 *@sLocator - Locator of the element
	 */
	public void clear(String sLocator) {
		WebElement actualLocator = getWebElement(sLocator);
		actualLocator.clear();
	}
	/**
	 *This method set random data based of input data and method to locator(input, checkbox, dropdown) .
	 */
	public void setValue(String sLocator, String sData, String methodName) {
		WebElement locator = getWebElement(sLocator);
		String Data = dataGenerate.randomDataGenerator(sData, methodName);
		try {
			if (locator != null) {			
				String locatorAttributeType = locator.getAttribute("type").trim();
				String locatorAttributeTag = locator.getTagName().trim();
				if (locatorAttributeType.contains("text") || locatorAttributeType.contains("password")
						|| locatorAttributeType.contains("tel")|| locatorAttributeType.contains("number")
						 ||locatorAttributeType.equals("email")||locatorAttributeTag.equals("textarea")) {
					if (!isElementPresent(sLocator))
						scrollToView(sLocator);
					if (!verifyAndAddIntegerInput(sLocator, Data)) {
						locator.sendKeys(Data);
						if (locatorAttributeType.contains("password"))
							logger.logPass("Entered '*******' in the field " + sLocator, "N");
						else
							logger.logPass("Entered " + Data + " in the field " + sLocator, "N");
					}
				} else {
					String locatorAttributeRole = locator.getAttribute("role").trim();
					if (locatorAttributeRole.equals("listbox")) {
						selectDropdownByLi(sLocator, Data);
						logger.logPass("Selected " + Data + " in the field " + sLocator, "N");
					} else {
						String locatorAttributeCheck = locator.getAttribute("type");
						if (locatorAttributeCheck.contains("checkbox") && Data.contains("Y")) {
							scrollToView(sLocator);
							Thread.sleep(2000);
							javaScriptClick(locator);
							logger.logPass("Clicked on " + Data + " in the field " + sLocator, "N");
						}
					}
				}
			}
		} catch (Exception e) {
			try {
				String locatorAttributeRole = locator.getAttribute("role");
				if (locatorAttributeRole.contains("listbox")) {
					if (!isElementPresent(sLocator))
						scrollToView(sLocator);
					selectDropdownByLi(sLocator, Data);
				}
			} catch (Exception e1) {
				try {
					String locatorAttributeCheck = locator.getAttribute("type");
					if (locatorAttributeCheck.contains("checkbox") && Data.contains("Y")) {
						scrollToView(sLocator);
						Thread.sleep(2000);
						javaScriptClick(locator);
					}
				} catch (Exception e2) {
					logger.logFail(
							"Given field locator was not of the type input, dropdown or checkbox. Please verify the locator");
				}
			}
		}
	}
	/**
	 * 
	 *This method gives WebElement of input locator.
	 */
	public WebElement getWebElement(String sLocator) {
		String actualLocator = Excel.getLocator(sLocator);
		WebElement element = null;
		try {
			if (actualLocator.startsWith("//")) {
				element = webDriver.findElement(By.xpath(actualLocator));
			} else {
				try {
					element = webDriver.findElement(By.id(actualLocator));
				} catch (Exception e) {
					try {
						element = webDriver.findElement(By.name(actualLocator));
					} catch (Exception e1) {
						try {
							element = webDriver.findElement(By.cssSelector(actualLocator));
						} catch (Exception e2) {
							try {
								element = webDriver.findElement(By.className(actualLocator));

							} catch (Exception e5) {
								//logger.logFail("Failed since the locator given was not found");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			//logger.logFail("Error while finding the Element. Exception: " + e.getMessage());
		}
		return element;
	}
	/**
	 *This method gives List of WebElement of input locator.
	 */
	public List<WebElement> getWebElementList(String sLocator) {
		String actualLocator = Excel.getLocator(sLocator);
		List<WebElement> element = null;
		try {
			if (actualLocator.startsWith("//")) {
				element = webDriver.findElements(By.xpath(actualLocator));
			} else {
				try {
					element = webDriver.findElements(By.cssSelector(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.name(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.id(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.className(actualLocator));
					if (element.size() != 0)
						return element;
				} catch (Exception e) {
					//logger.logFail("Error while finding the Element list due to exception " + e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.logFail("Error while finding the Element list due to exception " + e.getMessage());
		}
		return element;
	}
	/**
	 *This method gives List of WebElement for input locator (without excel's locator sheet).
	 */
	public List<WebElement> getWebElementListWithoutExcel(String actualLocator) {
		List<WebElement> element = null;
		try {
			if (actualLocator.startsWith("//")) {
				element = webDriver.findElements(By.xpath(actualLocator));
			} else {
				try {
					element = webDriver.findElements(By.cssSelector(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.name(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.id(actualLocator));
					if (element.size() != 0)
						return element;
					else
						element = webDriver.findElements(By.className(actualLocator));
					if (element.size() != 0)
						return element;
					else
						return null;
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.logFail("Error while finding the Element list due to exception " + e.getMessage());
		}
		return element;
	}

	public WebElement getWebElementWithoutExcel(String actualLocator) {
		WebElement element = null;
		try {
			if (actualLocator.startsWith("//")) {
				element = webDriver.findElement(By.xpath(actualLocator));
			} else {
				try {
					element = webDriver.findElement(By.id(actualLocator));
				} catch (Exception e) {
					try {
						element = webDriver.findElement(By.name(actualLocator));
					} catch (Exception e1) {
						try {
							element = webDriver.findElement(By.cssSelector(actualLocator));
						} catch (Exception e2) {
							try {
								element = webDriver.findElement(By.className(actualLocator));

							} catch (Exception e5) {
								//logger.logFail("Failed since the locator '"+actualLocator+"' given was not found");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.logFail("Error while finding the Element. Exception: " + e.getMessage());
		}
		return element;
	}
	/**
	 *This method launch the specified url.
	 */
	public void getURL(String url) {
		webDriver.get(url);
	}
	/**
	 *The click command emulates a click operation for a link, button, checkbox or radio button.
	 */
	public void click(String sLocator) {
		WebElement actualLocator = getWebElement(sLocator);
		jsScrollToElement(sLocator);
		actualLocator.click();
	}
	/**
	 *The click command emulates a click operation for a link, button, checkbox or radio button.
	 */
	public void click(WebElement actualLocator) {
		actualLocator.click();
	}
	/**
	 *This method select value from a dropdown based on Li.
	 */
	public void selectDropdownByLi(String sLocator, String Data) {
		try {
			WebElement Dd_locator = getWebElement(sLocator);
			String getId = Dd_locator.getAttribute("id").toString();
			WebElement Field_locator = getWebElementWithoutExcel("[aria-owns*='" + getId + "']");
			WebElement Field_locator_ToScroll = getWebElementWithoutExcel(
					"//*[contains(@aria-owns,'" + getId + "')]//ancestor::div[@class='row']");
			String locatorAttributeRole = Dd_locator.getAttribute("role");
			if (locatorAttributeRole.contains("listbox")) {
				jsScrollToElement(Field_locator_ToScroll);//scrollToView(Field_locator_ToScroll);
				scroll(0, 50);
				Field_locator.click();
				Thread.sleep(1000);
				List<WebElement> liList = Dd_locator.findElements(By.tagName("li"));
				for (WebElement li : liList) {
					if (li.getText().equals(Data)) {
						li.click();
						Thread.sleep(1000);
						waitForPageToLoad(60);
						break;
					}
				}
				try {
					String verifyDD = getWebElementWithoutExcel("[aria-owns*='" + getId + "'] span[class='k-input']")
							.getText();
					if (!verifyDD.contains(Data.trim())) {
						logger.logWarning("The data " + Data
								+ " provided in the data sheet is not available in application");
						// Assert.fail();
					}
				}catch(Exception e) {
					//Empty catch block to handle different dropdown
				}

			}
		} catch (Exception e) {
			logger.logFail("Failed to select Dropdown due to exception " + e.getMessage());
		}
	}
	/**
	 *This method waits for given input timeInSec.
	 */
	public void waitForPageToLoad(int timeInSec) {
		try {
			int iterator = 0;
			boolean blnrtrn, blnajaxIsComplete = false;
			try {
				do {
					JavascriptExecutor js = (JavascriptExecutor) webDriver;
					blnrtrn = js.executeScript("return document.readyState").equals("complete");
					try {
						blnajaxIsComplete = (Boolean) js.executeScript("return jQuery.active == 0");
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
					if (blnrtrn && blnajaxIsComplete) {
						break;
					} else {
						iterator = iterator + 1;
						Thread.sleep(1000);
					}
				} while (iterator < timeInSec);

				if (!blnrtrn && blnajaxIsComplete) {
					System.out.println("Failed to load page in " + timeInSec + " seconds");
				}
				waitForLoadingToDisappear();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.logFail("Failed to load page in " + timeInSec + " due to exception " + e.getMessage());
			}
		} catch (Exception e) {
			logger.logFail("Failed to load page in " + timeInSec + " due to exception " + e.getMessage());
		}
	}
	/**
	 *An expectation for checking that an element is either invisible or not present on the DOM.
	 */
	public void waitForLoadingToDisappear() {
		try {
			new WebDriverWait(webDriver, 30).until(
					ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(Excel.getLocator("Page_Loading"))));
		} catch (Exception e) {
			logger.logFail("Failed to wait for Loading to disappear from page due to exception " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 *It will check for an element is present or displayed on the UI.
	 *@sLocator - name of the locator from the excel.
	 */
	public boolean isElementPresent(String sLocator) {
		WebElement locator = getWebElement(sLocator);
		boolean elementPresent = false;
		if(locator!=null) {
			try {
				if (locator.isDisplayed()) {
					elementPresent = true;
				}
			} catch (Exception ex) {
				if (locator.isDisplayed())
					elementPresent = true;
			}
		}
		return elementPresent;
	}
	/**
	 *It will check for an element is present or displayed on the UI.
	 *@sLocator - name of the webelement.
	 */
	public boolean isElementPresent(WebElement element) {
		boolean elementPresent = false;
		if(element!=null) {
			try {
				if (element.isDisplayed()) {
					elementPresent = true;
				}
			} catch (Exception ex) {
				if (element.isDisplayed())
					elementPresent = true;
			}
		}
		return elementPresent;
	}

	public boolean isElementEnabled(String sLocator) {
		WebElement locator = getWebElement(sLocator);
		boolean elementPresent = false;
		if(locator!=null) {
			try {
				if (locator.isEnabled()) {
					elementPresent = true;
				}
			} catch (Exception ex) {
				if (locator.isEnabled())
					elementPresent = true;
			}
		}
		return elementPresent;
	}

	/**
	 * This will moves the mouse to the specified element.
	 * @sLocator - locator of the element from the excel.
	 */
	public void scrollToView(String sLocator) {
		try {
			WebElement locator = getWebElement(sLocator);
			if (locator != null) {
				Actions actions = new Actions(webDriver);
				actions.moveToElement(locator);
				actions.perform();
				Thread.sleep(500);

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}
	/**
	 * This will moves the mouse to the specified element.
	 * @element - element to be passed. 
	 */
	public void scrollToView(WebElement element) {

		try {
			if (element != null) {
				Actions actions = new Actions(webDriver);
				actions.moveToElement(element);
				actions.perform();
				Thread.sleep(500);
			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}

	/**
	 * This will moves the mouse to the specified element.
	 * @sLocator - locator of the element from the excel.
	 */

	public void jsScrollToElement(String sLocator) {

		try {
			WebElement locator = getWebElement(sLocator);
			if (locator != null) {
				((JavascriptExecutor) webDriver).executeScript("window.scroll(" + (locator.getLocation().getX() - 20)
						+ ", " + (locator.getLocation().getY() - 350) + ");");

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}
	
	public void jsScrollToElementWithoutExcel(String sLocator) {

		try {
			WebElement locator = getWebElementWithoutExcel(sLocator);
			if (locator != null) {
				((JavascriptExecutor) webDriver).executeScript("window.scroll(" + (locator.getLocation().getX() - 20)
						+ ", " + (locator.getLocation().getY() - 350) + ");");

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}
	

	/**
	 * This will moves the mouse to the specified element.
	 * @sLocator - locator of the element from the excel.
	 * @Xaxis - location of x axis coordinates.
	 * @Yaxis - location of y axis coordinates.
	 */
	public void jsScrollToElement(String sLocator,int Xaxis, int Yaxis) {
		try {
			WebElement locator = getWebElement(sLocator);
			if (locator != null) {
				((JavascriptExecutor) webDriver).executeScript("window.scroll(" + (locator.getLocation().getX() - 20)
						+ ", " + (Yaxis - Xaxis) + ");");

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}

	public void jsScrollToElement(WebElement element,int Xaxis, int Yaxis) {
		try {
			if (element != null) {
				((JavascriptExecutor) webDriver).executeScript("window.scroll(" + (element.getLocation().getX() - 20)
						+ ", " + (Yaxis - Xaxis) + ");");

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}

	public void jsScrollToElement(WebElement element) {

		try {
			if (element != null) {
				((JavascriptExecutor) webDriver).executeScript("window.scroll(" + (element.getLocation().getX() - 20)
						+ ", " + (element.getLocation().getY() - 350) + ");");

			}
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
		}
	}
	/**
	 * This will perform click operation.
	 * @sLocator - locator of the element from the excel.
	 */

	public void javaScriptClick(String sLocator) {

		try {
			WebElement element = getWebElement(sLocator);
			if (element != null) {
				((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", element);
			} else {
				logger.logFail("Given element to JS click is null. Verify the Locator given");
			}
		} catch (Exception e) {
			logger.logFail("Failed to perform JS click due to exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void javaScriptClick(WebElement element) {
		try {
			if (element != null) {
				((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", element);
			} else {
				logger.logFail("Given element to JS click is null. Verify the Locator given");
			}
		} catch (Exception e) {
			logger.logFail("Failed to perform JS click due to exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void scroll(int min, int max) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("window.scrollBy(" + min + "," + max + ")", "");
		} catch (Exception e) {
			logger.logFail("Failed to scroll the page due to exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void scrollToBottomOfCreateCustPage() {
		scrollToView("CreateCustomer_BottomOfPage");
		getWebElement("CreateCustomer_footer").click();
		Actions action = new Actions(webDriver);
		action.sendKeys(Keys.DOWN).build().perform();
	}

	public String[] removeNullValues(String[] arrayString) {
		try {
			ArrayList<String> list = new ArrayList<String>();
			for (String s : arrayString) {
				if (s == null)
					s = null;
				else
					list.add(s);
			}
			arrayString = list.toArray(new String[list.size()]);
		} catch (Exception e) {
			System.out.println("Failed to remove null values from String[] due to exception " + e.getMessage());
		}
		return arrayString;
	}
	
	public Object[] removeNullValues(Object[] arrayString) {
		try {
			ArrayList<Object> list = new ArrayList<Object>();
			for (Object s : arrayString) {
				if (s == null)
					s = null;
				else
					list.add(s);
			}
			arrayString = list.toArray(new String[list.size()]);
		} catch (Exception e) {
			System.out.println("Failed to remove null values from String[] due to exception " + e.getMessage());
		}
		return arrayString;
	}

	public boolean verifyAndAddIntegerInput(String sLocator, String Data) {
		boolean setValueFlag = false;
		WebElement locator = getWebElement(sLocator);
		String locatorId = getWebElement(sLocator).getAttribute("id");
		String isDataValNumber = locator.getAttribute("data-val-number");
		String className = locator.getAttribute("class");
		try {
			String isNumericTextBox = locator.getAttribute("data-role");
			if (isNumericTextBox.contains("numerictextbox")) {
				String locatorKInput = Excel.getLocator("Formatted_InputBox").replace("##", locatorId);
				if (getWebElementListWithoutExcel(locatorKInput).size() > 0) {
					scrollToView(sLocator);
					scroll(0, 20);
					click(getWebElementWithoutExcel(locatorKInput));
					locator.sendKeys(Data);
					setValueFlag = true;
					logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
				}
			}
		} catch (Exception e) {
			try {
				if (isDataValNumber.contains("must be a number")) {
					if(!className.contains("text-box single-line")) {
						String idToBeClicked = getWebElement(sLocator).getAttribute("id");
						if (!idToBeClicked.contains("_C")) {
							idToBeClicked = idToBeClicked + "_C";
						}
						scrollToView(sLocator);
						scroll(0, 50);
						click(getWebElementWithoutExcel(idToBeClicked));
						Thread.sleep(000);
						locator.sendKeys(Data);
						setValueFlag = true;
						logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
					}
				}
			} catch (Exception e1) {
				try {
					String locatorId1 = getWebElement(sLocator).getAttribute("id");
					WebElement locator1 = getWebElementWithoutExcel(locatorId1);
					if (locatorId1.contains("_C")) {
						locatorId1 = locatorId1.replace("_C", "");

						String isDataValNumber1 = locator1.getAttribute("data-val-number");
						if (isDataValNumber1.contains("must be a number")) {
							if(!className.contains("text-box single-line")) {
								String idToBeClicked = getWebElement(sLocator).getAttribute("id");
								if (!idToBeClicked.contains("_C")) {
									idToBeClicked = idToBeClicked + "_C";
								}
								scrollToView(sLocator);
								scroll(0, 20);
								click(getWebElementWithoutExcel(idToBeClicked));
								Thread.sleep(1000);
								locator1.sendKeys(Data);
								setValueFlag = true;
								logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
							}
						}
					} else {
						String locatorKInput = Excel.getLocator("Formatted_InputBox").replace("##", locatorId1);
						if (getWebElementListWithoutExcel(locatorKInput).size() > 0) {
							scrollToView(sLocator);
							scroll(0, 20);
							click(getWebElementWithoutExcel(locatorKInput));
							locator1.sendKeys(Data);
							setValueFlag = true;
							logger.logPass("Entered value " + Data + " in the field " + sLocator, "N");
						}
					}
				} catch (Exception e2) {
					// This catch block doesn't contain anything.
				}
			}
		}
		return setValueFlag;
	}

	public void waitExplicit(String sLocator, int seconds) {
		try {
			if (sLocator == null) {
				Thread.sleep((seconds * 1000));
			} else {
				for (int i = 1; i < seconds; i++) {
					if (isElementPresent(sLocator)) {
						break;
					}
					Thread.sleep(1000);
				}
				if (!isElementPresent(sLocator)) {
					logger.logFail("Element not found within the given time");
				}
			}
		} catch (Exception e) {
			logger.logFail(
					"Locator " + sLocator + " is not found within " + seconds + " seconds. Exception:" + e.toString());
		}
	}

	/**
	 * Waits for the Element to be present
	 * @param Element       : Locator of element
	 * @param TimeInSeconds : Max time to wait for the element
	 * @return : return true if element available with in time else false
	 * @throws Exception
	 */
	public boolean waitForElementPresent(String sLocator, int TimeInSeconds) {
		try {
			ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					WebElement element = getWebElement(sLocator);
					WebDriverWait wait = new WebDriverWait(webDriver, 30);
					wait.until(ExpectedConditions.stalenessOf(element));
					if (element != null) {
						return true;

					} else {
						return false;
					}
				}
			};
			Wait<WebDriver> wait = new WebDriverWait(webDriver, TimeInSeconds);
			try {
				wait.until(expectation);
				return true;
			} catch (Exception e) {
				logger.logFail("Error while waiting for element. Exception: " + e.getMessage());
			}
		} catch (Exception e) {
			logger.logFail("Error while waiting for element. Exception: " + e.getMessage());
		}
		return false;
	}
	/**
	 * It will select the value from the drop down on the basis of there li tag if thats available.
	 */
	public void SetselectDropdownByLi(String sLocator, String Data) {
		try {
			WebElement Dd_locator = getWebElement(sLocator);
			String locatorAttributeRole = Dd_locator.getAttribute("role");
			if (locatorAttributeRole.contains("listbox")) {
				jsScrollToElement(Dd_locator);
				Thread.sleep(1000);
				List<WebElement> liList = Dd_locator.findElements(By.tagName("li"));
				System.out.println(liList);
				for (WebElement li : liList) {
					if (li.getText().equals(Data)) {
						// System.out.println(li.getText());
						li.click();
						logger.logPass("Successfully able to Select the Option :" + li.getText(), "Y");
						Thread.sleep(1000);
						waitForPageToLoad(60);
						break;
					}
				}

			}
		} catch (Exception e) {
			logger.logFail("Failed to select Dropdown due to exception " + e.getStackTrace());
		}
	}
	/**
	 * It will perform drag and drop operation.
	 * @String From - is the string which we need to drag
	 * @String To - is the string on which we need to drop the first element
	 */
	public void DragNDrop(String From, String To) {
		try {
			WebElement Frm = getWebElement(From);
			WebElement Too = getWebElement(To);
			// Using Action class for drag and drop.
			Actions act = new Actions(webDriver);
			// Dragged and dropped.
			act.dragAndDrop(Frm, Too).build().perform();
			logger.logPass("Successfully performing Drag N Drop", "Y");
		} catch (Exception e) {
			// TODO: handle exception
			logger.logFail("Drag and Drop failed due to exception :" + e.getMessage());
		}
	}
	/**
	 * It will perform drag and drop operation.
	 * @WebElement From - is the element which we need to drag
	 * @WebElement To - is the element on which we need to drop the first element
	 */
	public void DragNDrop(WebElement From, WebElement To) {
		try {
			// Using Action class for drag and drop.
			Actions act = new Actions(webDriver);
			// Drag and drop.
			act.dragAndDrop(From, To).build().perform();
			logger.logPass("Successfully performing Drag N Drop", "Y");
		} catch (Exception e) {
			// TODO: handle exception
			logger.logFail("Drag and Drop failed due to exception :" + e.getMessage());
		}

	}

	public void click(String sLocator, WebElement Ele) {

		try {
			if (sLocator != null && sLocator != "" && Ele == null) {
				List<WebElement> listEle = getWebElementList(sLocator);
				if (listEle.size() > 0) {
					for (WebElement ele : listEle) {
						try {
							if (ele.isEnabled() && ele.isDisplayed()) {
								ele.click();
								break;
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				}
			}
			if (Ele != null) {
				Ele.click();
			}

		} catch (Exception e) {
			logger.logFail("Failed to click on the Duplicate List of elements");
		}
	}

	// getTextNode() function is used to get the Text of the Parent Node
	/*
	 * <div id='one'> <button id='two'>i am button</button> <button id='three'>i am
	 * button</button> i am div </div>
	 */
	public String getTextNode(WebElement e) {
		String text = e.getText().trim();
		List<WebElement> children = e.findElements(By.xpath("./*"));
		for (WebElement child : children) {
			text = text.replaceFirst(child.getText(), "").trim();
		}
		return text;
	}
	/**
	 * To get the name of the method that called the current method you can use.
	 */
	public String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	public void deleteDataPropFile() {
		try {
			dataProp.cleanData();
		} catch (Exception e) {
			System.out.println("No such file/directory exists");
		}
	}
	/**
	 * It will select a value from the dropdown.
	 * @sLocator - is the locator of the element.
	 * @VisibleText - Visible text from the dropdown. 
	 */
	public void select(String sLocator, String VisibleText) {
		WebElement ele = getWebElement(sLocator);
		Select sel = new Select(ele);
		sel.selectByVisibleText(VisibleText);

	}

	public void assertTrue(boolean condition, String assertStatement, String Screenshot) {
		if(condition) {
			logger.logPass("Assert Passed for '"+assertStatement+"'", Screenshot);
		}else {
			logger.logFail("Assert Failed for '"+assertStatement+"'");
			Assert.fail();
		}
	}

	public void assertFalse(boolean condition, String assertStatement, String Screenshot) {
		if(!condition) {
			logger.logPass("Assert Passed for '"+assertStatement+"'", Screenshot);
		}else {
			logger.logFail("Assert Failed for '"+assertStatement+"'");
			webDriver.quit();
			Assert.fail();
		}
	}
	/**
	 * To switch to new window using its Title
	 * @sValue - Title of new window
	 */
	public void switchToWindow(Object sValue){
		try{
			String title = sValue.toString();
			Set<String> availableWindows = webDriver.getWindowHandles();
			if (!availableWindows.isEmpty()) {
				for (String windowId : availableWindows) {
					if (webDriver.switchTo().window(windowId).getTitle().contains(title)) {
						logger.logPass("Window is switched to "+title , "N");
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.logFail("Failed to switch due to exception "+e.getMessage());
		}
	}
	/**
	 * WebDriver supports moving between named windows using the “switchTo” method.
	 */
	public void switchToWindowHandle(String windowHandler){
		try{
			webDriver.switchTo().window(windowHandler);
		}catch(Exception e){
			logger.logFail("Failed to switch due to exception "+e.getMessage());
		}
	}
	/**
	 * It will returns all handles from all opened browsers by Selenium WebDriver during execution.
	 */  
	public Set<String> getWindowHandles() {
		return webDriver.getWindowHandles();
	}
	/**
	 * It will get the handle of the page the webDriver is currently controlling. This handle is a unique identifier for the web page.
	 */
	public String getWindowHandle() {
		return webDriver.getWindowHandle();
	}
	/**
	 * It closes the current open window on which driver has focus on.
	 */
	public void closeWindow() {
		webDriver.close();
	}

}
