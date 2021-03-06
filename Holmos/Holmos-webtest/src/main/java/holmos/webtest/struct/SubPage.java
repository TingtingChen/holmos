package holmos.webtest.struct;

import static org.junit.Assert.fail;
import holmos.webtest.Allocator;
import holmos.webtest.BrowserWindow;
import holmos.webtest.SeleniumBrowserWindow;
import holmos.webtest.WebDriverBrowserWindow;
import holmos.webtest.basetools.HolmosBaseTools;
import holmos.webtest.basetools.HolmosWindow;
import holmos.webtest.constvalue.ConfigConstValue;
import holmos.webtest.constvalue.ConstValue;
import holmos.webtest.element.Element;
import holmos.webtest.element.locator.Locator;
import holmos.webtest.element.locator.LocatorChain;
import holmos.webtest.element.locator.LocatorValue;
import holmos.webtest.element.tool.SeleniumElementExist;
import holmos.webtest.element.tool.WebDriverElementExist;
import holmos.webtest.element.tool.WebElementExist;
import holmos.webtest.log.MyLogger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

/**一种页面元素的组织结构，包括三种类型<br>
 * (1)SubPage<br>
 * (2)Collection<br>
 * (3)Element<br>
 * @author 吴银龙(15857164387)
 * */
public class SubPage implements LocatorValue{
	protected String wholeComment;
	public String getWholeComment() {
		return wholeComment;
	}
	public void setWholeComment(String wholeComment) {
		this.wholeComment = wholeComment;
	}
	private WebElementExist exist;
	/**此元素的全名，在css校验的时候，为了给css本地文件取名设置的变量信息*/
	protected String fullName="";
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**SubPage的注释说明*/
	private String comment;
	/**当前正在用的locator*/
	private String locatorCurrent="";
	public String getLocatorCurrent() {
		return locatorCurrent;
	}
	public void setLocatorCurrent(String locatorCurrent) {
		this.locatorCurrent = locatorCurrent;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Locator getLocator() {
		return locator;
	}
	public void setLocator(Locator locator) {
		this.locator = locator;
	}
	/**SubPage里面所包含的链信息<br>
	 * 用来保存这个节点所用到的comment和locator信息<br>*/
	private LocatorChain infoChain=new LocatorChain();
	public LocatorChain getInfoChain() {
		return infoChain;
	}
	/**代表这个集合的基础元素，集合其实也是一个元素，这个元素里面包含着<br>
	 * 很多结构相同或者极其相似，这个在以webdriver作为底层的时候用到<br>*/
	protected WebElement element;
	public void setElement(WebElement element) {
		this.element = element;
	}
	public WebElement getElement() {
		return element;
	}
	/**此SubPage的定位器*/
	protected Locator locator;
	/**subpage里面的元素*/
	protected List<Element> elements=new ArrayList<Element>();
	/**subpage里面的subpages*/
	protected List<SubPage> subpages=new ArrayList<SubPage>();
	/**这个页面的集合Collections*/
	protected List<Collection> collections=new ArrayList<Collection>();
	/**此SubPage的日志记录器*/
	protected MyLogger logger=MyLogger.getLogger(this.getClass().getName());
	public SubPage(String comment){
		this.locator=new Locator();
		this.infoChain=new LocatorChain();
		this.comment=comment;
	}
	/**收集Sub页面元素，这是一个简单的观察者模式的应用<br>
	 * 以此来获取页面元素的信息，在重新写locator<br>
	 * 和comment的时候用得到<br>*/
	protected void init(){
		Field[] fields=this.getClass().getFields();
		try{
			for(Field field : fields){
				if(field.getModifiers()==ConstValue.nestedFatherObjectModifier){
					continue;
				}Object o=field.get(this);
				if(o instanceof Element){
					HolmosBaseTools.insertElementName((Element)o, field.getName());
					((Element)o).getInfoChain().addParentNode(this.getInfoChain().getInfoNodes());
					((Element)o).setFullName(fullName+field.getName());
					((Element)o).getInfoChain().addNode(this);
					elements.add((Element)o);
				}else if(o instanceof SubPage){
					HolmosBaseTools.insertSubPageName((SubPage)o, field.getName());
					((SubPage)o).getInfoChain().addParentNode(this.getInfoChain().getInfoNodes());
					((SubPage)o).setFullName(fullName+field.getName());
					((SubPage)o).getInfoChain().addNode(this);
					((SubPage)o).init();
					this.subpages.add((SubPage)o);
				}else if(o instanceof Collection){
					HolmosBaseTools.insertCollectionName((Collection)o, field.getName());
					((Collection)o).getInfoChain().addParentNode(this.getInfoChain().getInfoNodes());
					((Collection)o).setFullName(fullName+field.getName());
					((Collection)o).getInfoChain().addNode(this);
					((Collection)o).init();
					this.collections.add((Collection)o);
				}
			}
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 查看SubPage是否存在，等待加载waitCount次
	 * */
	private boolean isSubPageExist(int waitCount){
		BrowserWindow currentWindow=Allocator.getInstance().currentWindow;
		if(currentWindow instanceof SeleniumBrowserWindow){
			exist=new SeleniumElementExist(this);
		}
		else if(currentWindow instanceof WebDriverBrowserWindow){
			exist=new WebDriverElementExist(this);
		}
		return exist.isElementExist(waitCount);
	}
	
	public void addIDLocator(String id){
		this.locator.addIdLocator(id);
	}
	public void addNameLocator(String name){
		this.locator.addNameLocator(name);
	}
	public void addXpathLocator(String xpath){
		this.locator.addXpathLocator(xpath);
	}
	public void addCSSLocator(String css){
		this.locator.addCSSLocator(css);
	}
	public void addLinkTextLocator(String linkText){
		this.locator.addLinkTextLocator(linkText);
	}
	public void addPartialLinkTextLocator(String partialLinkText){
		this.locator.addPartialLinkTextLocator(partialLinkText);
	}
	public void addAttributeLocator(String attributeName, String attributeValue) {
		this.locator.addAttributeLocator(attributeName, attributeValue);
	}
	public void addTagNameLocator(String tagName){
		this.locator.addTagNameLocator(tagName);
	}
	public void addClassLocator(String className){
		this.locator.addClassLocator(className);
	}
	/**判断这个SubPage在上层容器中是否存在<br>
	 * 必须保证当前的subpage所在的是当前活动窗口<br>
	 * @return true 存在<br>
	 * false 不存在*/
	
	public boolean isExist(){
		locatorCurrent="";
		return isSubPageExist(ConfigConstValue.defaultWaitCount);
	}
	/**校验SubPage是否存在，则失败返回*/
	public void assertExist(){
		StringBuilder message=new StringBuilder();
		if(isExist()){
			message.append(this.wholeComment+":");
			message.append(":元素存在性校验成功！元素存在！");
			logger.info(message);
		}else{
			message.append(this.wholeComment+":");
			message.append(":元素存在校验失败！元素不存在!");
			logger.error(message);
			HolmosWindow.closeAllWindows();
			fail(message.toString());
		}
	}
	/**校验SubPage是否不存在，则失败返回*/
	public void assertNotExist(){
		StringBuilder message=new StringBuilder();
		if(!isExist()){
			message.append(this.wholeComment+":");
			message.append("元素不存在性校验成功！元素不存在！");
			logger.info(message);
		}else{
			message.append(this.wholeComment+":");
			message.append("元素存在校验失败！元素存在!");
			logger.error(message);
			HolmosWindow.closeAllWindows();
			fail(message.toString());
		}
	}
	/**校验SubPage是否存在，失败继续运行*/
	public void verifyExist(){
		StringBuilder message=new StringBuilder();
		if(isExist()){
			message.append(this.wholeComment+":");
			message.append("元素存在性校验成功！元素存在！");
			logger.info(message);
		}else{
			message.append(this.wholeComment+":");
			message.append("元素存在校验失败！元素不存在!");
			logger.error(message);
		}
	}
	/**校验SubPage是否不存在，失败继续运行*/
	public void verifyNotExist(){
		StringBuilder message=new StringBuilder();
		if(!isExist()){
			message.append(this.wholeComment+":");
			message.append("元素不存在性校验成功！元素不存在！");
			logger.info(message);
		}else{
			message.append(this.wholeComment+":");
			message.append("元素存在校验失败！元素存在!");
			logger.error(message);
		}
	}
	/**等待SubPage出现，失败打错误日志，程序继续运行<br>
	 * 默认等待时间是30s
	 * */
	public boolean waitForExist() {
	    if(!isSubPageExist(ConfigConstValue.waitCount)){
            logger.error("元素"+wholeComment+"一直没有出现");
            return false;
        }return true;
	}
	/**等待SubPage消失,失败打错误日志，程序一直运行*/
	public void waitForDisppear() {
		int waitCount=0;
		while(waitCount++<ConfigConstValue.waitCount){
			if(!exist.isElementExistForCheckOnce())
				return;
		}
	}
	
}
