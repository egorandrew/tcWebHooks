/**
 * 
 */
package webhook.teamcity.payload.format;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import webhook.teamcity.Loggers;
import webhook.teamcity.payload.WebHookPayload;
import webhook.teamcity.payload.WebHookPayloadManager;
import webhook.teamcity.payload.WebHookTemplateContent;
import webhook.teamcity.payload.content.WebHookPayloadContent;
import webhook.teamcity.payload.template.render.WebHookStringRenderer;
import webhook.teamcity.payload.template.render.WwwFormUrlEncodedToHtmlPrettyPrintingRenderer;


public class WebHookPayloadNameValuePairs extends WebHookPayloadGeneric implements WebHookPayload {
	
	public static final String FORMAT_SHORT_NAME = "nvpairs";
	public static final String FORMAT_CONTENT_TYPE = "application/x-www-form-urlencoded";

	public WebHookPayloadNameValuePairs(WebHookPayloadManager manager) {
		super(manager);
	}

	Integer rank = 100;
	String charset = "UTF-8";
	
	public void setPayloadManager(WebHookPayloadManager manager){
		myManager = manager;
	}
	
	public void register(){
		myManager.registerPayloadFormat(this);
	}
	
	public String getFormatDescription() {
		return "Name Value Pairs";
	}

	public String getFormatShortName() {
		return FORMAT_SHORT_NAME;
	}

	public String getFormatToolTipText() {
		return "Send the payload as a set of normal Name/Value Pairs";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getStatusAsString(WebHookPayloadContent content, WebHookTemplateContent webHookTemplateContent){
		String returnString = ""; 
		
		Map<String, String> contentMap = null;
		try {
			 contentMap = BeanUtils.describe(content);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			Loggers.SERVER.debug("It was not possible to convert 'content' into a bean", ex );
		}
		
		if (contentMap != null && contentMap.size() > 0){
			
			for(Iterator<String> param = contentMap.keySet().iterator(); param.hasNext();)
			{
				String key = param.next();
				String pair = "&";
				try {
					if (key != null){
						pair += URLEncoder.encode(key, this.charset);
						if (contentMap.get(key) != null){
							pair += "=" + URLEncoder.encode((String)contentMap.get(key), this.charset);
						} else {
							pair += "=" + URLEncoder.encode("null", this.charset);
						}
					}
				} catch (UnsupportedEncodingException | ClassCastException ex ){
					pair = "";
					Loggers.SERVER.debug("failed to encode 'content' parameter '" + key + "' to URL format. Value has been converted to an empty string", ex );
				}
				returnString += pair;
			}
		
		}
		
		if (content != null && content.getExtraParameters() != null  && content.getExtraParameters().size() > 0){
			
			for(Iterator<String> param = content.getExtraParameters().keySet().iterator(); param.hasNext();)
			{
				String key = param.next();
				String pair = "&";
				try {
					if (key != null){
						pair += URLEncoder.encode(key, this.charset);
						if (content.getExtraParameters().get(key) != null){
							pair += "=" + URLEncoder.encode((String)content.getExtraParameters().get(key), this.charset);
						} else {
							pair += "=" + URLEncoder.encode("null", this.charset);
						}
					}
				} catch (UnsupportedEncodingException | ClassCastException ex ) {
					pair = "";
					Loggers.SERVER.debug("failed to encode 'extra' parameter '" + key + "' to URL format. Value has been converted to an empty string.", ex );
				}
				returnString += pair;
				Loggers.SERVER.debug(this.getClass().getSimpleName() + ": payload is " + returnString);
			}
		}
		
		Loggers.SERVER.debug(this.getClass().getSimpleName() + ": payload is " + returnString);
		if (returnString.length() > 0){
			return returnString.substring(1);
		} else {
			return returnString;
		}
	}



	public String getContentType() {
		return FORMAT_CONTENT_TYPE;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getCharset() {
		return this.charset;
	}

	@Override
	public WebHookStringRenderer getWebHookStringRenderer() {
		return new WwwFormUrlEncodedToHtmlPrettyPrintingRenderer();
	}
	
}
